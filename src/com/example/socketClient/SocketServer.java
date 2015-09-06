package com.example.socketClient;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-22
 * Time: 下午9:49
 * To change this template use File | Settings | File Templates.
 */


import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer{
    private int prot;

    public SocketServer(int prot) {
        this.prot = prot;

        start();
    }

    public String infoUpperCase(String  line){
        return line.toUpperCase();
    }


    public void start(){
        try {

            ServerSocket serverSocket=new ServerSocket(prot);


            serverSocket.setReuseAddress(true);
            Socket socketAccept= serverSocket.accept();

            BufferedReader in=new BufferedReader(new InputStreamReader(socketAccept.getInputStream()));


         //   PrintWriter out=new PrintWriter(socketAccept.getOutputStream(),true);
//            out.println("123....");
//            out.println("456");
            boolean done=false;
            while(!done){
                Log.i("tag", "lldfkjdkdfdfdfdfdfdfdfdfdfgggggkkkkkkkkkkkkkkkkkkkkk");
                String line=in.readLine();
                if(line==null){
                    done=true;
                }else{

                    Log.i("tag", "lldfkjdkdfdfdfdfdfdfdfdfdfgggggkkkkkkkkkkkkkkkkkkkkk");
                    String message=infoUpperCase(line);
//                    out.println("0000:"+message);
                    if(line.trim().equals("exit"))
                        done=true;
                }
            }
            Log.i("tag", "lldfkjdkdfdfdfdfdfdfdfdfdfgggggkkkkkkkkkkkkkkkkkkkkk");
            socketAccept.close();
        } catch (Exception e) {
            System.out.println("000:"+e.getMessage());
        }


    }

}

