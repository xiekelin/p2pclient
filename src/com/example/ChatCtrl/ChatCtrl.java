package com.example.ChatCtrl;

import com.example.P2P.MyActivity;
import android.os.Handler;
import com.example.protobuf.ProtoBuffer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/7/13
 * Time: 4:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChatCtrl implements Runnable{

    public static Queue<byte[]> queue = new ConcurrentLinkedQueue<byte[]>();

    private Handler myHandle;

    Thread chatThread;

    public ChatCtrl(Handler arg_handle){

        myHandle = arg_handle;

        chatThread = new Thread(this);

        chatThread.start();

    }

    public static byte[] setChatContent(String arg_string){

        try{

            ProtoBuffer.MainInterFace.Builder builder  = ProtoBuffer.MainInterFace.newBuilder();

            builder.setPackageType(ProtoBuffer.PackageType.chat);

            builder.setChat(getChatProtoBuffer(arg_string));

            ProtoBuffer.MainInterFace mainInterFace = builder.build();

            return mainInterFace.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static ProtoBuffer.Chat.Builder getChatProtoBuffer(String arg_string){

        ProtoBuffer.Chat.Builder builder = ProtoBuffer.Chat.newBuilder();
        builder.setMessage(arg_string);

        return builder;
    }

    public String getChatContent(byte[] arg_byte){

        try{

            ProtoBuffer.MainInterFaceOrBuilder mainInterFaceOrBuilder = ProtoBuffer.MainInterFace.parseFrom(arg_byte);

            ProtoBuffer.Chat chat = mainInterFaceOrBuilder.getChat();

            String message = chat.getMessage();

            if(message!=null){
                return message;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public void run(){

        while(true){

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            byte[] message = queue.poll();

            if(message!=null)
                myHandle.obtainMessage(MyActivity.UPDATE_CHAT_LIST,getChatContent(message)).sendToTarget();


        }
    }

}
