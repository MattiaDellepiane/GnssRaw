import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
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
    boolean closed = false;

    public static void main(String... args) {
        GnssServer server = new GnssServer();
        new Thread(()->server.start()).start();
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try{
            String mess;
            while((mess = in.readLine()) != null && !mess.equalsIgnoreCase("stop")){
                in.readLine(); //wait for any input
            }
        }catch(IOException ex){
            ex.printStackTrace();
        }
        server.close();
        System.out.println("Closed");
    }

    public void start(){
        try {
            System.out.println("Server listening on " + address + ":" + port);
            ExecutorService executor = Executors.newCachedThreadPool();
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(address, port));
            while(!closed){
                client = serverSocket.accept();
                if(!closed)
                    executor.execute(new ConnHandler(client));
            }
            serverSocket.close();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public void close(){
        closed = true;
        try{
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
                String mess = in.readLine();
                if(mess.equalsIgnoreCase("PING")){
                    out.println("OK");
                    return;
                }
                while(mess != null){
                    System.out.println(mess);
                    mess = in.readLine();
                }
                out.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}