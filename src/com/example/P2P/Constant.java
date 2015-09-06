package com.example.P2P;

import java.net.InetSocketAddress;

/**
 * Created by Administrator on 13-12-9.
 */
public class Constant {

    public final static int TCP = 1;
    public final static int UDP = 0;
    public final static int BURROW_WAY = UDP;
    public final static int SERVER_WAY = UDP;
    public static int localPort;
    public final static String SERVER_IP1 = "202.192.18.39";
    public final static String SEVER_IP_WEB_SOCKET1 = "ws://172.22.71.88:9999/srv";
    public final static String SERVER_IP = "192.168.0.111";
    public final static String SEVER_IP_WEB_SOCKET = "ws://192.168.0.111:9999/srv";
    public final static int SERVER_PORT = 1246;
    public final static InetSocketAddress address = new InetSocketAddress(SERVER_IP,SERVER_PORT);
    public final static boolean TestStatus = false;
}
