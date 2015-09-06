package com.example.P2P;

import android.util.Log;

import java.io.IOException;
import java.net.*;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/12/13
 * Time: 11:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class Burrow implements Runnable{

    private boolean burrowFinish = false;
    boolean threadFinish = false;
    DatagramSocket socket;
    DatagramPacket packetForSend;
    Thread burrowThread;
    public Burrow(DatagramSocket arg_socket,DatagramPacket arg_packet){

        socket = arg_socket;
        packetForSend = arg_packet;
        burrowThread = new Thread(this);
        burrowThread.start();

    }

    @Override
    public void run(){

        for(int i=0;i<10;i++){

            try{

                socket.send(packetForSend);
                Log.i("liu","localPort:"+socket.getLocalPort()+",target:"+packetForSend.getSocketAddress());
                Thread.sleep(100);

            }catch (InterruptedException ite){
                ite.printStackTrace();
            }catch (IOException ioe){
                ioe.printStackTrace();
            }

        }

    }

    public void stopBurrow(){
        burrowFinish = true;
    }

}
