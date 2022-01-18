package com.github.mattiadellepiane.gnssraw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GnssServer {

    String address = "0.0.0.0";
    int port = 5087;
    ServerSocket serverSocket;
    Socket client;

    public static void main(String... args) {
        new GnssServer();
    }

    public GnssServer() {
        try {
            System.out.println("Server in ascolto su " + address + ":" + port);
            ExecutorService executor = Executors.newCachedThreadPool();
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(address, port));
            client = serverSocket.accept();
            executor.execute(new ConnHandler(client));
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class ConnHandler implements Runnable{

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;

        public ConnHandler(Socket client){
            this.client = client;
            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new PrintWriter(client.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run(){
            try{
                String mess = "";
                while((mess = in.readLine()) != null){
                    if(mess.equalsIgnoreCase("PING")){
                        out.println("OK");
                    }
                    System.out.println(mess);
                }
                in.close();
                client.close();
                serverSocket.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}