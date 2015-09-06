package com.example.testTCP;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/16/13
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class tcpClient_b {

    public static void main(String arg[]){

        try {
            Socket socket = new Socket("127.0.0.1",1087);
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            out.print("hello");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

}
