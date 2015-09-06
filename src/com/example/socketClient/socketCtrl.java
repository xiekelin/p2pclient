package com.example.socketClient;

import android.util.Log;
import com.example.ChatCtrl.ChatCtrl;
import com.example.P2P.RequestConnect;
import com.example.P2P.tcpSocket;
import com.example.PearCtrl.PearCtrl;
import com.example.protobuf.Packet;
import com.example.protobuf.ProtoBuffer;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-19
 * Time: 下午9:34
 * To change this template use File | Settings | File Templates.
 */
public class socketCtrl {

    public static void dealRec(byte[] messageByte){
        switch (Packet.GetPackageTypeByByte(messageByte)){

            case ProtoBuffer.PackageType.getPearList_VALUE:{
               Log.i("tag","pearList: "+messageByte.toString());
                PearCtrl.queue.add(messageByte);
                break;
            }

            case ProtoBuffer.PackageType.requestConnect_VALUE:{
                Log.i("tag","requestConnect: "+messageByte.toString());

                ReceiveRequest(messageByte);

                break;

            }
            case ProtoBuffer.PackageType.chat_VALUE:{
                 Log.i("tag","chat_value: "+messageByte.toString());
                 ChatCtrl.queue.add(messageByte);
                break;
            }

            default:{

            }

        }

    }

    private static void ReceiveRequest(byte[] arg_byte){

        try{

            ProtoBuffer.MainInterFaceOrBuilder mainInterFaceOrBuilder = ProtoBuffer.MainInterFace.parseFrom(arg_byte);

            ProtoBuffer.RequestConnect requestConnect = mainInterFaceOrBuilder.getRequestConnect();

            String url = requestConnect.getIp();

            String splitAfter[] =url.split(":");
            clientSocket.createSocketConnect(splitAfter[0], splitAfter[1], ChatCtrl.setChatContent("this is a test"));
          //  new SocketClient(splitAfter[0],Integer.parseInt(splitAfter[1]));

        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
