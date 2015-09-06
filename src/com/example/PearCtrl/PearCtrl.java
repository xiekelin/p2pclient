package com.example.PearCtrl;


import android.os.Handler;
import com.example.P2P.Constant;
import com.example.P2P.MyActivity;
import com.example.protobuf.ProtoBuffer;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/4/13
 * Time: 9:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class PearCtrl implements Runnable{

    public static String localUrl;

    public static String localIp;

    public static Queue<byte[]> queue = new ConcurrentLinkedQueue<byte[]>();

    public HashSet<String> pearSet;

    private Handler myHandle;

    Thread PearCtrlThread;

    public PearCtrl(Handler arg_handle){

        pearSet = new HashSet<String>();

        myHandle = arg_handle;

        PearCtrlThread = new Thread(this);

        PearCtrlThread.start();
    }

    public void getPearList(byte[] arg_byte){

        try{
            pearSet.clear();

            ProtoBuffer.MainInterFaceOrBuilder mainInterFaceOrBuilder = ProtoBuffer.MainInterFace.parseFrom(arg_byte);

            List<ProtoBuffer.GetPearList> pearLists = mainInterFaceOrBuilder.getGetPearListList();

            for(ProtoBuffer.GetPearList pear : pearLists){

                String ip = pear.getIp();
                int tap = pear.getTap();
                if(tap==1){
                    localUrl = ip;
                    //localIp = localUrl.split(":")[0];
                    continue;
                }

                pearSet.add(ip);

            }

        }catch (Exception e){
            e.printStackTrace();
        }

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
                 getPearList(message);

                 myHandle.obtainMessage(MyActivity.UPDATE_PEAR_LIST,null).sendToTarget();
             }


         }

    }



}
