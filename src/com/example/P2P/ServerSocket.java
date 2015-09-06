package com.example.P2P;

import android.os.Handler;
import android.util.Log;
import com.example.PearCtrl.PearCtrl;
import com.example.protobuf.Packet;
import com.example.protobuf.ProtoBuffer;
import weberknecht.WebSocket;
import weberknecht.WebSocketEventHandler;
import weberknecht.WebSocketException;
import weberknecht.WebSocketMessage;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/9/13
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServerSocket {

    public WebSocket webSocket;

    Handler myHandle;

    public ServerSocket(String arg_url,Handler arg_handler){

        try{
            myHandle = arg_handler;

            URI url = new URI(arg_url);

            webSocket = new WebSocket(url);


            webSocket.setEventHandler(new WebSocketEventHandler() {
                @Override
                public void onOpen() {
                    //To change body of implemented methods use File | Settings | File Templates.
                    System.out.println("connect success");
                }

                @Override
                public void onMessage(WebSocketMessage message) {
                    //To change body of implemented methods use File | Settings | File Templates.

                    byte[] messageByte = message.returnByte();
                    System.out.println(String.valueOf(messageByte));
                    switch (Packet.GetPackageTypeByByte(messageByte)){

                        case ProtoBuffer.PackageType.getPearList_VALUE:{
                            PearCtrl.queue.add(messageByte);
                            break;
                        }

                        case ProtoBuffer.PackageType.requestConnect_VALUE:{

                            RequestConnect.queue.add(messageByte);
                            break;

                        }

                        default:{
                            Log.i("liu","unexpected case!");
                            System.out.println("unexpected case!");
                        }

                    }



                }

                @Override
                public void onClose() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void onPing() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                @Override
                public void onPong() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });

            webSocket.connect();
        }catch (WebSocketException wse){
            myHandle.obtainMessage(MyActivity.CONNECT_ERROR,null).sendToTarget();
            Log.e("WebSocketException",String.valueOf(wse.getMessage()));
        }catch (URISyntaxException use){
            Log.e("URISyntaxException",String.valueOf(use.getMessage()));
        }

    }

    public void sendByteToServer(byte[] bytes){

        try{

            if(webSocket!=null)
                webSocket.send(bytes);


        }catch (WebSocketException wse){
            wse.printStackTrace();
        }


    }

}
