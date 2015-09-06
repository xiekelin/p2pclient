package com.example.socketClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-19
 * Time: 下午9:31
 * To change this template use File | Settings | File Templates.
 */
public class receieveThread extends  Thread {
    private Socket s;
    private int timeout = 60000;

    public receieveThread(Socket s){
        this.s = s;
    }

    public void run(){

        try {
            s.setSoTimeout(timeout);
            while(true){
                s.setSoTimeout(timeout);
                System.out.println("in recv thread");
                byte[] receieve = new byte[0];
                try {
                    receieve = readStream(s.getInputStream());
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                String str = new String(receieve);
                System.out.println("recv:");
                System.out.println(str);
                socketCtrl.dealRec(receieve);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public static byte[] readStream(InputStream inStream) throws Exception {
        int count = 0;
        while (count == 0) {
            count = inStream.available();
//            if(count == 0){
//                break;
//            }
        }
        byte[] b = new byte[count];
        inStream.read(b);
        return b;
    }
}
