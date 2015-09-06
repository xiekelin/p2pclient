package com.example.P2P;

import android.os.Handler;
import android.util.Log;
import weberknecht.WebSocket;
import weberknecht.WebSocketEventHandler;
import weberknecht.WebSocketException;
import weberknecht.WebSocketMessage;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Created by Administrator on 13-12-9.
 */
public class TcpBurrowClient {

    public WebSocket webSocket;
    Handler myHandle;

    public TcpBurrowClient(String arg_url,int arg_localPort,Handler arg_handler){

       try{

        myHandle = arg_handler;

        URI url = new URI("ws://"+arg_url);

        webSocket = new WebSocket(url);

        webSocket.setEventHandler(new WebSocketEventHandler() {
            @Override
            public void onOpen() {
                Log.i("liu","burrow success");
            }

            @Override
            public void onMessage(WebSocketMessage message) {
                Log.i("liu","burrow success");
            }

            @Override
            public void onClose() {

            }

            @Override
            public void onPing() {

            }

            @Override
            public void onPong() {

            }
        });

         webSocket.burrowConnect(arg_localPort);
       }catch (WebSocketException wse){

//           Log.e("WebSocketException", String.valueOf(wse.getMessage()));
       }catch (URISyntaxException use){
//           Log.e("URISyntaxException",String.valueOf(use.getMessage()));
       }
    }

    public void sendByteToClient(byte[] bytes){

        try{

            if(webSocket!=null)
                webSocket.send(bytes);


        }catch (WebSocketException wse){
            wse.printStackTrace();
        }


    }

}
