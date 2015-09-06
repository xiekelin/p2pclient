package com.example.P2P;

import android.os.Handler;
import android.webkit.HttpAuthHandler;
import com.example.PearCtrl.PearCtrl;
import weberknecht.WebSocketException;
import websocketserver.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;


/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-1
 * Time: 下午3:45
 * To change this template use File | Settings | File Templates.
 */
public class SocketMgr {

    HashMap<String,UdpBurrowClient> UdpClientHashMap = new HashMap<String, UdpBurrowClient>();
    HashMap<String,TcpBurrowClient> TcpClientHashMap = new HashMap<String, TcpBurrowClient>();

    Handler myHandle;

    public SocketMgr(Handler arg_handle){

        myHandle = arg_handle;

    }

    public UdpBurrowClient CreateUDPSocket(String arg_url){

        String[] strings = getIp(arg_url);

        UdpBurrowClient udpBurrowClient = UdpClientHashMap.get(arg_url);

        if(udpBurrowClient!=null&&udpBurrowClient.getSocketConnect()){
            return udpBurrowClient;
        }

         udpBurrowClient = new UdpBurrowClient(strings[0],Integer.parseInt(strings[1]),myHandle,false);

        UdpClientHashMap.put(arg_url,udpBurrowClient);

        return udpBurrowClient;
    }



    public TcpBurrowClient CreateTCPClientSocket(String arg_url,int arg_localPort){

        TcpBurrowClient tcpBurrowClient = TcpClientHashMap.get(arg_url);

        if(tcpBurrowClient!=null&&tcpBurrowClient.webSocket.isConnect()){
            return tcpBurrowClient;
        }

        tcpBurrowClient = new TcpBurrowClient(arg_url,arg_localPort,myHandle);

        TcpClientHashMap.put(arg_url,tcpBurrowClient);

        return tcpBurrowClient;
    }

    public void CreateTcpBurrow(String arg_url,int arg_localPort){
        try {
            Thread.sleep(5000);
            TcpBurrowClient tcpBurrowClient = new TcpBurrowClient(arg_url,arg_localPort,myHandle);
            tcpBurrowClient.sendByteToClient(RequestConnect.burrowBuffer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void CreateNatSession(String arg_url){

        String[] strings = getIp(arg_url);
        ServerSocket serverSocket = new ServerSocket(Constant.SEVER_IP_WEB_SOCKET,myHandle);
        serverSocket.sendByteToServer(RequestConnect.setRequestConnectHoleBuffer(arg_url));
        int port = serverSocket.webSocket.getSocketLocalPort();

        Socket socket = new Socket();
        try {
            serverSocket.webSocket.close();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(port));
            socket.connect(new InetSocketAddress(strings[0],Integer.parseInt(strings[1])),5000);
        } catch (WebSocketException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            try {
                socket.close();
                WebSocketServer webSocketServer = new WebSocketServer(port);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    private String[] getIp(String arg_url){

        return arg_url.split(":");

    }



}
