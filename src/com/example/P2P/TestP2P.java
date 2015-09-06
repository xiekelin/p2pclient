package com.example.P2P;

import com.example.ChatCtrl.ChatCtrl;
import com.example.protobuf.Packet;
import com.example.protobuf.ProtoBuffer;
import weberknecht.WebSocket;
import websocketserver.WebSocketServer;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Administrator on 13-12-13.
 */
public class TestP2P {

    static Queue<byte[]> queueForPear = new ConcurrentLinkedQueue<byte[]>();

    static Queue<byte[]> queueForRequest = new ConcurrentLinkedQueue<byte[]>();

    static Queue<byte[]> queueForChat = new ConcurrentLinkedQueue<byte[]>();

    static Udp udp;

    static ServerSocket serverSocket;

    static WebSocketServer webSocketServer;

    static SocketMgr socketMgr;

    public static void main(String arg[]){

        TestP2P testP2P = new TestP2P();
        testP2P.pearThread.start();
        testP2P.requestThread.start();
        testP2P.chatThread.start();

        if(Constant.SERVER_WAY==Constant.UDP){
            udp = new Udp();
            udp.sendToServer(Packet.registerBuffer);
            udp.sendToServer(RequestConnect.burrowBuffer);
        }else{
            socketMgr = new SocketMgr(null);
            serverSocket = new ServerSocket("ws://202.192.18.39/srv",null);
            serverSocket.sendByteToServer(Packet.setRegisterBuffer());
            Constant.localPort = serverSocket.webSocket.getSocketLocalPort();

        }


    }



    public static void PacketActionForTest(byte[] buffer){

        switch (Packet.GetPackageTypeByByte(buffer)){

            case ProtoBuffer.PackageType.getPearList_VALUE:{
                queueForPear.add(buffer);
                break;
            }

            case ProtoBuffer.PackageType.requestConnect_VALUE:{

                queueForRequest.add(buffer);
                break;
            }

            case ProtoBuffer.PackageType.chat_VALUE:{

                System.out.println("get chat");
                queueForChat.add(buffer);
                break;
            }

            default:{
                break;
            }

        }


    }

    Thread pearThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true){

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byte[] message = queueForPear.poll();

                if(message!=null){
                    String ip = getPearList(message);

                    if(Constant.SERVER_WAY==Constant.UDP){
                        udp.sendToServer(RequestConnect.setRequestConnectBuffer(getPearList(message)));
                        udp.sendToClient(ChatCtrl.setChatContent("hello"),ip);
                    }else{
                        serverSocket.sendByteToServer(RequestConnect.setRequestConnectBuffer(getPearList(message)));
                        socketMgr.CreateTCPClientSocket(ip,Constant.localPort).sendByteToClient(ChatCtrl.setChatContent("hello"));
                    }


                }


            }
        }
    });

    Thread requestThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byte[] message = queueForRequest.poll();

                if(message!=null){
                    RequestConnect.ReceiveRequest(message);
                }

            }
        }
    });

    Thread chatThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                byte[] message = queueForChat.poll();

                if(message!=null){
                    System.out.println("get chat success!");
                }

            }
        }
    });

    public String getPearList(byte[] arg_byte){

        try{

            ProtoBuffer.MainInterFaceOrBuilder mainInterFaceOrBuilder = ProtoBuffer.MainInterFace.parseFrom(arg_byte);

            List<ProtoBuffer.GetPearList> pearLists = mainInterFaceOrBuilder.getGetPearListList();

            for(ProtoBuffer.GetPearList pear : pearLists){

                String ip = pear.getIp();
                int tap = pear.getTap();
                if(tap==0){
                   return ip;
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }


}
