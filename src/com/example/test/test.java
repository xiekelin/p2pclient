package com.example.test;



import com.example.P2P.*;
import com.example.P2P.ServerSocket;

import java.io.*;
import java.net.*;

/**
 * Created by Administrator on 13-12-16.
 */
public class test {

    public static void main(String arg[]){


        ServerSocket serverSocket = new ServerSocket("ws://172.18.7.52:62234",null);

        serverSocket.sendByteToServer(RequestConnect.setRequestConnectBuffer());




    }


}
