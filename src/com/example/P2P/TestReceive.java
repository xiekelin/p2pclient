package com.example.P2P;

import websocketserver.WebSocketServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by Administrator on 13-12-13.
 */
public class TestReceive {

    public static void main(String arg[]){

        ServerSocket serverSocket = new ServerSocket("ws://172.18.5.72:9999/srv",null);

        int port = serverSocket.webSocket.getSocketLocalPort();

        WebSocketServer webSocketServer = new WebSocketServer(port);

//        try {
//            DatagramSocket socket = new DatagramSocket(null);
//            socket.setReuseAddress(true);
//            socket.bind(new InetSocketAddress(port));
//            String s = "hello";
//            byte[] b = s.getBytes();
//            DatagramPacket packet = new DatagramPacket(b,b.length,new InetSocketAddress("127.0.0.1",7777));
//            socket.send(packet);
//
//        } catch (SocketException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


    }


}
