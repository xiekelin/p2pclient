package com.example.socketClient;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-22
 * Time: 下午3:24
 * To change this template use File | Settings | File Templates.
 */
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

/**

 *
 * @author lai_zs

 */
public class SocketClient {
    private final String SOCKET_NAME = "htfsk";
    private LocalSocket client;
    private Socket socket;
    private InetSocketAddress address;
    private boolean isConnected = false;
    private int connetTime = 1;

    public SocketClient(String arg_ip,int arg_port) {
//        client = new LocalSocket();
//        address = new LocalSocketAddress(SOCKET_NAME, LocalSocketAddress.Namespace.RESERVED);
//        new ConnectSocketThread().start();
        socket = new Socket();
        try {
            socket.setReuseAddress(true);
            try {
                socket.bind(new InetSocketAddress(InetAddress.getLocalHost(), arg_port));
                address = new InetSocketAddress(InetAddress.getByName(arg_ip),arg_port);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } catch (SocketException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        new ConnectSocketThread().start();
    }


    public String sendMsg(byte[] msg) {
        if (!isConnected) {
            return "Connect fail";
        }
        try {
//            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//            OutputStream os = client.getOutputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            OutputStream os = socket.getOutputStream();
            os.write(msg);
            os.flush();
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Nothing return";
    }

    /**

     * @author Administrator
     *
     */
    private class ConnectSocketThread extends Thread {
        @Override
        public void run() {
            while (!isConnected && connetTime <= 10) {

                isConnected = true;
                try {
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    Log.i("SocketClient","Try to connect socket;ConnectTime:"+connetTime);
//                    client.connect(address);
                    socket.connect(address,60000);
                    isConnected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                    connetTime++;
                    isConnected = false;
                    Log.i("SocketClient","Connect fail");
                }
            }
        }
    }

    /**
     * 关闭Socket
     */
    public void closeSocket() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}