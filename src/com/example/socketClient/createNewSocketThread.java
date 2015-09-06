package com.example.socketClient;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-21
 * Time: 下午7:27
 * To change this template use File | Settings | File Templates.
 */
public class createNewSocketThread extends Thread {

    public static Socket socket;
    public  static  String ip;
    public  static  int port;
    public createNewSocketThread( String arg_ip,int port){
           this.ip = arg_ip;
        this.port = port;
    }
    public void run(){
        Socket s = new Socket();


        try {
            s.setReuseAddress(true);
            s.bind(new InetSocketAddress(InetAddress.getLocalHost(), clientSocket.localPort));
            Log.i("tag", "localPort = " + s.getLocalPort() + "ip: " + s.getLocalSocketAddress());
            s.connect(new InetSocketAddress(ip, port), 2000);
            //    s.connect(new InetSocketAddress(InetAddress.getByName(arg_ip), Integer.parseInt(arg_port)), 5000);
            Log.i("tag", "localPort00123 = " + s.getLocalPort() + "ip: " + s.getLocalSocketAddress());


        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}
