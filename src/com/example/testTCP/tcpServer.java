package com.example.testTCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/16/13
 * Time: 10:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class tcpServer {

    public static void main(String arg[]){

        try {
            ServerSocket serverSocket = new ServerSocket(8888);

            while(true){
                Socket socket = serverSocket.accept();
                invoke(socket);
            }

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    private static void invoke(final Socket client){

        new Thread(new Runnable() {
            @Override
            public void run() {

                BufferedReader in = null;
                PrintWriter out = null;

                try {
                    in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                    out = new PrintWriter(client.getOutputStream());

                    while (true){

                        String msg = in.readLine();

                        System.out.println(msg);

                        out.println("Server receive");

                        out.flush();

                        if(msg.equals("bye"))
                           break;

                    }

                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } finally {
                    try {
                        in.close();
                        out.close();
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }


            }
        }).start();



    }


}
