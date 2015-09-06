package com.example.P2P;

import android.os.Handler;
import android.util.Log;
import com.example.ChatCtrl.ChatCtrl;
import com.example.PearCtrl.PearCtrl;
import com.example.protobuf.Packet;
import com.example.protobuf.ProtoBuffer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/12/13
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Udp {

    DatagramSocket socket;
    boolean receiveThreadFinish = false;
    final static int ECHOMAX = 1024;
    DatagramPacket packetSendToServer;
    DatagramPacket packetForReceive;
    HashMap<String,Burrow> hashMap = new HashMap<String, Burrow>();

    public Udp(){

        try {
            socket = new DatagramSocket();
//            socket.setReuseAddress(true);
//            socket.bind(new InetSocketAddress(22222));
        } catch (SocketException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        receiveThread.start();
        keepServerAlive.start();
    }

    Thread receiveThread = new Thread(new Runnable() {
        @Override
        public void run() {

            try{

                while(!receiveThreadFinish){
                    receive();
                    Thread.sleep(100);
                }

            }catch (Exception ite){
                ite.printStackTrace();
            }

        }
    });

    Thread keepServerAlive = new Thread(new Runnable() {
        @Override
        public void run() {

            while (true){

                try {

                    Thread.sleep(60000);

                    sendToServer(RequestConnect.burrowBuffer);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    });


    public void receive(){

        try{

            packetForReceive = new DatagramPacket(new byte[ECHOMAX],ECHOMAX);

            socket.receive(packetForReceive);

            String dstIp = packetForReceive.getAddress().getHostAddress();

            int dstPort = packetForReceive.getPort();
            Log.i("liu","get a packet");
            //System.out.println("get a packet");

            do{

                if(dstIp.equals(Constant.SERVER_IP)&&dstPort == Constant.SERVER_PORT)
                    break;


                Burrow burrow = hashMap.get(dstIp+":"+dstPort);

                if(burrow!=null){
                    burrow.stopBurrow();
                    hashMap.remove(dstIp+":"+dstPort);
                }else{
                    return;
                }


            }while (false);

            byte[] data = new byte[packetForReceive.getLength()];
            System.arraycopy(packetForReceive.getData(), 0, data, 0, packetForReceive.getLength());

            if(!Constant.TestStatus){
                PacketAction(data);
            }else{
               TestP2P.PacketActionForTest(data);
            }

        }catch (IOException ioe){
            ioe.printStackTrace();
            HandleError();
        }


    }

    public void sendToServer(byte[] buffer){

        try{

              packetSendToServer = new DatagramPacket(buffer,buffer.length, Constant.address);

              socket.send(packetSendToServer);

        }catch (IOException ioe){
            ioe.printStackTrace();
            HandleError();
        }

    }

    public void sendToClient(byte[] buffer,String ip){

        String[] strings = ip.split(":");

        try {
            DatagramPacket packet = new DatagramPacket(buffer,buffer.length,new InetSocketAddress(strings[0],Integer.parseInt(strings[1])));

            if(PearCtrl.localUrl.contains(strings[0])){
                socket.send(packet);
            }else{
                Burrow burrow = new Burrow(socket,packet);

                hashMap.put(ip,burrow);
            }

        } catch (SocketException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }



    private void PacketAction(byte[] buffer){

        switch (Packet.GetPackageTypeByByte(buffer)){

            case ProtoBuffer.PackageType.getPearList_VALUE:{
                PearCtrl.queue.add(buffer);
                break;
            }

            case ProtoBuffer.PackageType.requestConnect_VALUE:{

                RequestConnect.queue.add(buffer);

                break;
            }

            case ProtoBuffer.PackageType.chat_VALUE:{
                Log.i("liu", "get chat");
               // System.out.println("get chat");
                ChatCtrl.queue.add(buffer);
                break;
            }

            default:{
                break;
            }

        }

    }



    private void HandleError(){

        if(socket.isConnected()){
            return;
        }

        receiveThreadFinish = true;
        socket.close();

    }

}
