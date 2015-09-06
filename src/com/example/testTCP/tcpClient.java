package com.example.testTCP;

import com.example.P2P.ServerSocket;
import com.example.P2P.TcpBurrowClient;
import websocketserver.WebSocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/16/13
 * Time: 10:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class tcpClient {

    public static void main(String arg[]){

        try {

            ServerSocket serverSocket = new ServerSocket("ws://172.22.71.77:7777/srv",null);
            int port =  serverSocket.webSocket.getSocketLocalPort();
            System.out.println(port);
            WebSocketServer webSocketServer = new WebSocketServer(port);
            TcpBurrowClient tcpBurrowClient = new TcpBurrowClient("172.22.71.77:8888/srv",port,null);
            System.out.println(tcpBurrowClient.webSocket.getSocketLocalPort());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    private static void CreateSock(int port){

        Socket socket = new Socket();

        try {
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(port));
            socket.connect(new InetSocketAddress("127.0.0.1",9999));
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            while (true){
                out.println("create socket");
                out.flush();
                String msg = in.readLine();
                System.out.println("receive");
            }
        } catch (SocketException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

}
