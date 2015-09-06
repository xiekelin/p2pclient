package com.example.P2P;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.example.ChatCtrl.ChatCtrl;
import com.example.PearCtrl.PearCtrl;
import com.example.protobuf.Packet;
import com.example.protobuf.ProtoBuffer;
import websocketserver.WebSocketServer;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    static SocketMgr socketMgr;
    static ServerSocket socket;
    static Udp udp;
    static ArrayList<Map<String,Object>> pearList;
    ListView listView;
    TextView chatView;
    SimpleAdapter sp;
    private PearCtrl pearCtrl;
    private RequestConnect requestConnect;
    private ChatCtrl chatCtrl;
    public final static int UPDATE_PEAR_LIST = 1;
    public final static int UPDATE_CHAT_LIST = 2;
    public final static int CONNECT_ERROR = 4;

    static float windowWidth;
    static float windowHeight;

    String choseIp;
    String chatContent;
    String sendContent;
    SimpleDateFormat  sDateFormat   =   new SimpleDateFormat("yyyy-MM-dd   hh:mm:ss");

    @Override
    public void onCreate(Bundle savedInstanceState) {

         Thread socketThread = new Thread(connectServer);
         socketThread.start();

        socketMgr = new SocketMgr(msgHandle);

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        pearCtrl = new PearCtrl(msgHandle);
        requestConnect = new RequestConnect(msgHandle);
        chatCtrl = new ChatCtrl(msgHandle);
        setContentView(R.layout.main);

        initListView();

        initLayout();

        listView.setOnItemClickListener(onItemClickListener);

    }

    final Runnable connectServer = new Runnable() {
        @Override
        public void run() {

            if(Constant.SERVER_WAY == Constant.TCP){
                if(socket==null||!socket.webSocket.isConnect()){
                    socket = new ServerSocket(Constant.SEVER_IP_WEB_SOCKET,msgHandle);
                    socket.sendByteToServer(Packet.setRegisterBuffer());
                    Constant.localPort = socket.webSocket.getSocketLocalPort();
                }
            }else if(Constant.SERVER_WAY == Constant.UDP){
                if(udp==null){
                    udp = new Udp();
                    udp.sendToServer(Packet.setRegisterBuffer());
                    udp.sendToServer(RequestConnect.burrowBuffer);
                }
            }



        }
    };


    private  AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            try{

                choseIp = (String)pearList.get(i).get("ip");

                showDialog();

                Log.i("liu",choseIp);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private void showDialog(){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View dialog = LayoutInflater.from(this).inflate(R.layout.dialog,null);

        builder.setTitle(R.string.title);

        builder.setIcon(android.R.drawable.ic_dialog_info);

        builder.setView(dialog);

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                EditText et = (EditText) dialog.findViewById(R.id.dialog);

                String date = sDateFormat.format(new java.util.Date());

                chatContent = et.getText().toString();

                sendContent = PearCtrl.localUrl+" "+date+"\n"+ et.getText().toString();

                Log.i("liu", chatContent);

                chatView.append("localhost" + " " + date + "\n" + chatContent + "\n");

                new Thread(udpConnect).start();

            }
        });

        builder.setNegativeButton(R.string.no, null);

        builder.create().show();

    }

    private void showErrorDialog(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        final View dialog = LayoutInflater.from(this).inflate(R.layout.error,null);
//
//        TextView errorText = (TextView)findViewById(R.id.error);
//
//        errorText.setText(R.string.connectFail);
//
//        builder.setTitle(R.string.title_error);
//
//        builder.setIcon(android.R.drawable.ic_dialog_info);
//
//        builder.setView(dialog);
//
//        builder.setNegativeButton(R.string.no,null);
//
//        builder.setPositiveButton(R.string.yes,null);
//
//        builder.create().show();
    }

    private void initListView(){
        listView = (ListView) this.findViewById(R.id.listView);
        if(pearList==null)
        pearList = new ArrayList<Map<String, Object>>();
        sp = new SimpleAdapter(this,pearList,R.layout.list,new String[]{"image","ip"},new int[]{R.id.imageView,R.id.textView});
        listView.setAdapter(sp);
    }

    public void addItem(String arg_url){

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("image",R.drawable.ic_launcher);
        map.put("ip",arg_url);
        pearList.add(map);

    }

    private void deleteItem(String arg_url){

        for (Map<String,Object> map : pearList){
            String url = (String)map.get("ip");
            if(url.equals(arg_url)){
                pearList.remove(map);
                sp.notifyDataSetChanged();
                break;
            }
        }

    }

    private void updatePearList(){

        pearList.clear();

        for(String url : pearCtrl.pearSet){
             addItem(url);
        }

        sp.notifyDataSetChanged();
    }

    private void updateChatList(String arg_message){

        chatView.append(arg_message+"\n");
    }

    public void initLayout(){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        windowWidth = displaymetrics.widthPixels;
        windowHeight = displaymetrics.heightPixels;
        ViewGroup.LayoutParams lp = listView.getLayoutParams();
        lp.width = (int)(0.2*windowWidth);
        lp.height = (int)windowHeight;
        listView.setLayoutParams(lp);

        chatView = (TextView)findViewById(R.id.ChatView);
        lp = chatView.getLayoutParams();
        lp.width = (int)(0.8*windowWidth);
        lp.height = (int)windowHeight;
        chatView.setLayoutParams(lp);

    }



    private Handler msgHandle = new Handler(){

        @Override
        public void handleMessage(Message message){

            switch(message.what){

                case UPDATE_PEAR_LIST:{

                      updatePearList();
                      break;
                }

                case UPDATE_CHAT_LIST:{

                      updateChatList((String)message.obj);
                      break;
                }

                case CONNECT_ERROR:{
                    showErrorDialog();
                    break;
                }


            }


        }

    };


    final Runnable udpConnect = new Runnable() {
        @Override
        public void run() {
            if(Constant.SERVER_WAY == Constant.TCP){
                socket.sendByteToServer(RequestConnect.setRequestConnectBuffer(choseIp));
            }else if(Constant.SERVER_WAY == Constant.UDP){
                udp.sendToServer(RequestConnect.setRequestConnectBuffer(choseIp));
            }
            if(Constant.BURROW_WAY==Constant.TCP){
                socketMgr.CreateNatSession(choseIp);
            }else if(Constant.BURROW_WAY == Constant.UDP){
                udp.sendToClient(ChatCtrl.setChatContent(sendContent),choseIp);
            }

        }
    };



}
