package com.example.P2P;

import android.os.Handler;
import android.util.Log;
import com.example.ChatCtrl.ChatCtrl;
import com.example.P2P.MyActivity;
import com.example.PearCtrl.PearCtrl;
import com.example.protobuf.Packet;
import com.example.protobuf.ProtoBuffer;
import weberknecht.WebSocket;

import java.io.IOException;
import java.net.*;

/**
 * Created by Administrator on 13-12-6.
 */
public class UdpBurrowClient{

    DatagramSocket socket;
    DatagramPacket packetForSend;
    DatagramPacket packetForReceive;
    InetAddress dstIp;
    SocketAddress target;
    int dstPort;
    String ipString;
    boolean receiveThreadFinish = false;
    boolean burrowThreadFinish = false;
    boolean connectServer;
    final static int ECHOMAX = 1024;
    Handler myHandle;
    boolean chatFinish = false;
    int localPort;

    public UdpBurrowClient(String arg_ip,int arg_port,Handler arg_handle,boolean arg_connectServer){

        ipString = arg_ip;

        dstPort = arg_port;

        target = new InetSocketAddress(ipString,dstPort);

        myHandle = arg_handle;

        connectServer = arg_connectServer;

        try{
            if(arg_connectServer){
                socket = new DatagramSocket();
                socket.setReuseAddress(true);
                localPort = socket.getLocalPort();
                Log.i("liu","serverPort:"+localPort);
            }else{
                socket = new DatagramSocket(null);
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(Constant.localPort));
                //socket = new DatagramSocket();
                Log.i("liu","localPort:"+Constant.localPort);
            }

        }catch (SocketException ske){
            ske.printStackTrace();
        }
        receiveThread.start();
    }

    public int getSocketLocalPort() {
        return localPort;
    }

    public boolean getSocketConnect(){
        return socket.isConnected();
    }

    Thread receiveThread = new Thread(new Runnable() {
        @Override
        public void run() {

            try{

                while(!receiveThreadFinish){
                    Log.i("liu","start receive");
                    receive();

                   // Thread.sleep(100);
                }

            }catch (Exception ite){
                ite.printStackTrace();
            }

        }
    });

    Thread burrowThread = new Thread(new Runnable() {
        @Override
        public void run() {

            while(!burrowThreadFinish){

                try{

                    socket.send(packetForSend);

                    Thread.sleep(100);
                    Log.i("liu","burrow continue and localPort: "+socket.getLocalPort());
                }catch (InterruptedException ite){
                    ite.printStackTrace();
                }catch (IOException ioe){
                    ioe.printStackTrace();
                    HandleError();
                }

            }

        }
    });

    public void SendPacket(byte[] buffer){

        try{

            packetForSend = new DatagramPacket(buffer,buffer.length, target);

            socket.send(packetForSend);

            if(!connectServer){
                burrowThread.start();
            }

        }catch (IOException ioe){
            ioe.printStackTrace();
            HandleError();
        }

    }

    public void startBurrow(){
        burrowThread.start();
    }

    private void receive(){

        try{

            packetForReceive = new DatagramPacket(new byte[ECHOMAX],ECHOMAX);

            socket.receive(packetForReceive);
            Log.i("liu","receive");
            dstIp = packetForReceive.getAddress();

            dstPort = packetForReceive.getPort();

            byte[] data = new byte[packetForReceive.getLength()];
            System.arraycopy(packetForReceive.getData(), 0, data, 0, packetForReceive.getLength());
            PacketAction(data);

        }catch (IOException ioe){
            ioe.printStackTrace();
            HandleError();
        }



    }

    private void PacketAction(byte[] buffer){

        switch (Packet.GetPackageTypeByByte(buffer)){

            case ProtoBuffer.PackageType.getPearList_VALUE:{
                PearCtrl.queue.add(buffer);
                break;
            }

            case ProtoBuffer.PackageType.requestConnect_VALUE:{
                if(connectServer){
                    RequestConnect.queue.add(buffer);
                }else{
                    burrowThreadFinish = true;

                    Log.i("liu","burrow success");
                }

                break;
            }

            case ProtoBuffer.PackageType.chat_VALUE:{
                if(!chatFinish){
                    burrowThreadFinish = true;
                    Log.i("liu","burrow success");
                    receiveThreadFinish = true;
                    ChatCtrl.queue.add(buffer);
                    chatFinish = true;
                }

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
