package com.github.mattiadellepiane.gnssraw.listeners;


import android.util.Log;

import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.data.SharedData;

import org.apache.commons.codec.binary.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerCommunication extends MeasurementListener {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ExecutorService executor;
    private boolean headerSent = false;
    private int port = 5088;

    public ServerCommunication(){
        executor = Executors.newSingleThreadExecutor();
        SharedData.getInstance().setServerCommunication(this);
    }

    private String getDebugTag(){
        return SharedData.getInstance().getContext().getString(R.string.debug_tag);
    }

    @Override
    protected void initResources() {
        if(SharedData.getInstance().isServerEnabled()) {
            executor.execute(() -> {
                try {
                    Log.v(getDebugTag(), "Server ip: " + SharedData.getInstance().getServerAddress());
                    socket = new Socket(SharedData.getInstance().getServerAddress(), SharedData.getInstance().getServerPort());
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    Log.v("PROVA", "risorse inizializzate");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    protected void releaseResources() {
        executor.execute(()->{
            if(out != null)
                out.close();
            try {
                if(in != null)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void write(String s){
        Log.v("AAAA", s);
        if(SharedData.getInstance().isListeningForMeasurements() && SharedData.getInstance().isServerEnabled()) {
            Log.v(getDebugTag(), "Invio messaggio al server");
            executor.execute(() -> {
                if(out != null) {
                    if(!headerSent) {
                        headerSent = true;
                    }else{
                        getLocation();
                    }
                    out.println(s);
                }
            });
            Log.v(getDebugTag(), "Messaggio inviato al server");
        }
    }

    public boolean isReachable() {
        Socket s = null;
        try {
            s = new Socket();
            s.connect(new InetSocketAddress(SharedData.getInstance().getServerAddress(), SharedData.getInstance().getServerPort()),3000);
        } catch (IOException e) {
            return false;
        }finally {
            try {
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void getLocation(){
        new Thread(()-> {
            Socket s;
            try {
                s = new Socket();
                s.connect(new InetSocketAddress(SharedData.getInstance().getServerAddress(), port), 3000);
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                //out.println("R");
                String input = null;
                if ((input = in.readLine()) != null) {
                    String[] params = input.split("\\s+");
                    double lat = Double.parseDouble(params[2]);
                    double lng = Double.parseDouble(params[3]);
                    double quota = Double.parseDouble(params[4]);
                    int qfix = Integer.parseInt(params[5]);
                    int nsat = Integer.parseInt(params[6]);
                    double sdn = Double.parseDouble(params[7]);
                    double sde = Double.parseDouble(params[8]);
                    double sdu = Double.parseDouble(params[9]);
                    if (SharedData.getInstance().getMapsFragment() != null) {
                        SharedData.getInstance().getMapsFragment().update(lat, lng, qfix);
                    }
                }
                out.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
