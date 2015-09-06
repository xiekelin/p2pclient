package com.example.socketClient;

import android.util.Log;
import com.example.Register.RegisterCtrl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-19
 * Time: 下午9:22
 * To change this template use File | Settings | File Templates.
 */
public class clientSocket {
    public static Socket socket;
    public static OutputStream os;
    public static int localPort;
    public static SocketAddress localSocketAddress;
    public static String localIp;

    public clientSocket(String arg_ip, String arg_port) {
        if (arg_ip == "" || arg_port == "") {
            System.out.println("the params is error");
            return;
        }
        socket = new Socket();
        // try {
        // socket.setReuseAddress(true);
        try {
            socket.setReuseAddress(true);
            socket.connect(new InetSocketAddress(arg_ip, Integer.parseInt(arg_port)));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        localPort = socket.getLocalPort();
        localSocketAddress = socket.getLocalSocketAddress();
        localIp = socket.getLocalAddress().toString();
        Log.i("tag", "localPort888888 = " + socket.getLocalPort() + "ip: " + socket.getLocalSocketAddress());
        System.out.print("localPort = " + socket.getLocalPort() + "ip: " + socket.getLocalSocketAddress());


        // } catch (SocketException e) {
        //   e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        //}
        receieveThread receive = new receieveThread(socket);
        receive.start();
    }

    public void sendToService(byte[] arg_msg) {

        try {
            os = socket.getOutputStream();
            os.write(arg_msg);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void createSocketConnect(String arg_ip, String arg_port, byte[] sendMsg) {

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Socket s = new Socket();


        try {
            s.setReuseAddress(true);
            String []splitIp = localIp.split("/");
            s.bind(new InetSocketAddress(splitIp[1], localPort));
            Log.i("tag", "localPort = " + s.getLocalPort() + "ip: " + s.getLocalSocketAddress());
//            s.connect((new InetSocketAddress(arg_ip, Integer.parseInt(arg_port))), 5000);
            s.setSoTimeout(60000);
            s.connect(new InetSocketAddress(arg_ip, Integer.parseInt(arg_port)));
            Log.i("tag", "localPort00123 = " + s.getLocalPort() + "ip: " + s.getLocalSocketAddress());
            OutputStream soT = s.getOutputStream();
            soT.write(sendMsg);
            soT.flush();

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        receieveThread receive = new receieveThread(s);
        receive.start();


    }
}
