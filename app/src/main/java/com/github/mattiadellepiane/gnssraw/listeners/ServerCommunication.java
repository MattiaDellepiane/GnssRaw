package com.github.mattiadellepiane.gnssraw.listeners;


import android.util.Log;

import com.github.mattiadellepiane.gnssraw.R;
import com.github.mattiadellepiane.gnssraw.data.SharedData;

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
        if(SharedData.getInstance().isListeningForMeasurements() && SharedData.getInstance().isServerEnabled()) {
            Log.v(getDebugTag(), "Invio messaggio al server");
            executor.execute(() -> {
                if(out != null) {
                    if(s.contains("#"))
                        Log.v("PROVA", "invio header");
                    out.println(s);
                }
            });
            Log.v(getDebugTag(), "Messaggio inviato al server");
        }
    }

    public boolean isReachable() {
        Socket s = null;
        boolean reachable = false;
        try {
            s = new Socket();
            s.connect(new InetSocketAddress(SharedData.getInstance().getServerAddress(), SharedData.getInstance().getServerPort()),3000);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            out.println("PING");
            String response = in.readLine();
            if(response != null && response.equalsIgnoreCase("OK")){
                reachable = true;
            }
            out.close();
        } catch (IOException e) {
            return false;
        }
        return reachable;
    }
}
