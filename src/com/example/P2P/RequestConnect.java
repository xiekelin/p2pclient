package com.example.P2P;

import android.os.Handler;
import android.util.Log;
import com.example.P2P.SocketMgr;
import com.example.protobuf.ProtoBuffer;
import weberknecht.WebSocketException;
import websocketserver.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/7/13
 * Time: 6:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestConnect implements Runnable{

    public static Queue<byte[]> queue = new ConcurrentLinkedQueue<byte[]>();

    Thread requestConnectThread;

    Handler myHandle;

   public static byte[] burrowBuffer = setRequestConnectBuffer();

      public RequestConnect(Handler arg_handler){

           myHandle = arg_handler;

          requestConnectThread = new Thread(this);

          requestConnectThread.start();

      }

    public static void ReceiveRequest(byte[] arg_byte){

        try{

            ProtoBuffer.MainInterFaceOrBuilder mainInterFaceOrBuilder = ProtoBuffer.MainInterFace.parseFrom(arg_byte);

            ProtoBuffer.RequestConnect requestConnect = mainInterFaceOrBuilder.getRequestConnect();

            String url = requestConnect.getIp();
            Log.i("liu","requestConnect :"+url);

            if(Constant.BURROW_WAY==Constant.UDP){
                MyActivity.udp.sendToClient(burrowBuffer,url);
            }else{
                MyActivity.socketMgr.CreateTcpBurrow(url,Constant.localPort);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static byte[] setRequestConnectBuffer(){

        try{

            ProtoBuffer.MainInterFace.Builder builder = ProtoBuffer.MainInterFace.newBuilder();

            builder.setPackageType(ProtoBuffer.PackageType.burrow);

            ProtoBuffer.MainInterFace mainInterFace = builder.build();

            return mainInterFace.toByteArray();

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] setRequestConnectHoleBuffer(String arg_url){

        try{

            ProtoBuffer.MainInterFace.Builder builder = ProtoBuffer.MainInterFace.newBuilder();

            builder.setPackageType(ProtoBuffer.PackageType.requestConnectHole);

            builder.setRequestConnect(requestConnectOrBuilder(arg_url));

            ProtoBuffer.MainInterFace mainInterFace = builder.build();

            return mainInterFace.toByteArray();

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] setRequestConnectBuffer(String arg_url){

        try{

            ProtoBuffer.MainInterFace.Builder builder = ProtoBuffer.MainInterFace.newBuilder();

            builder.setPackageType(ProtoBuffer.PackageType.requestConnect);

            builder.setRequestConnect(requestConnectOrBuilder(arg_url));

            ProtoBuffer.MainInterFace mainInterFace = builder.build();

            return mainInterFace.toByteArray();

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }

    public static ProtoBuffer.RequestConnect.Builder requestConnectOrBuilder(String arg_url){

        try{

            ProtoBuffer.RequestConnect.Builder builder = ProtoBuffer.RequestConnect.newBuilder();

            builder.setIp(arg_url);

            return builder;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void run(){
        while(true){

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            byte[] message = queue.poll();

            if(message!=null){
                ReceiveRequest(message);
            }



        }
    }



}
