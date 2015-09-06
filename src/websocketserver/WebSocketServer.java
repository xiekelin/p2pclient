package websocketserver;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-4
 * Time: 下午6:20
 * To change this template use File | Settings | File Templates.
 */
public class WebSocketServer {

    int port;

    boolean finish = false;

    private Socket clientSocket;

    private WebSocketServerCtrl webSocketServerCtrl;

    public WebSocketServer(int arg_port){

        port = arg_port;
        listenThread.start();
    }

    Thread listenThread = new Thread(new Runnable() {
        @Override
        public void run() {
            try{

                ServerSocket serverSocket = new ServerSocket();

                serverSocket.setReuseAddress(true);

                serverSocket.bind(new InetSocketAddress(port));

                HashSet<Socket> set = new HashSet<Socket>();

                while(!finish){

                    clientSocket = serverSocket.accept();

                    System.out.println("clientSocket"+clientSocket.getInetAddress()+":"+clientSocket.getPort());
                    //Log.i("liu","clientSocket"+clientSocket.getInetAddress()+":"+clientSocket.getPort());

                    set.add(clientSocket);

                    webSocketServerCtrl = new WebSocketServerCtrl(clientSocket,set);


                }

            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
    });

    public void initServer(){

        listenThread.start();
    }

    public boolean getClientSocket(){
        return clientSocket.isConnected();
    }

}
