package websocketserver;

import android.util.Log;
import weberknecht.WebSocketException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-12-4
 * Time: 下午6:27
 * To change this template use File | Settings | File Templates.
 */
public class WebSocketServerCtrl extends ThreadServerCliente {

    private boolean finish = false;
    private InputStream input;
    public OutputStream output;
    private WebSockServerHandshake webSockServerHandshake;

    public WebSocketServerCtrl(Socket clientSocket,HashSet<Socket> set){

        super(clientSocket,set);
        webSockServerHandshake = new WebSockServerHandshake();
        handshakeThread.start();
    }


     Thread handshakeThread = new Thread(new Runnable() {
        @Override
        public void run(){

            String message;
            byte[] bytesMessage;

            try{

                input = clientSocket.getInputStream();
                output = clientSocket.getOutputStream();
                webSockServerHandshake.analyseRequest(input);
                output.write(webSockServerHandshake.handShakeResponse().getBytes());
                output.flush();

                while(!finish){

                    bytesMessage = leerUnidad(input);
                    if(isCerrado(bytesMessage)){
                        finish = true;
                        clientes.remove(clientSocket);
                        System.out.println("Conexion cerrada con: " + clientSocket.getInetAddress() + " " + clientSocket.getPort());
                        System.out.println("Usuarios Conectados: " + clientes.size());
                    }else{
                        System.out.println("Conexion cerrada con: " + clientSocket.getInetAddress() + " " + clientSocket.getPort());
                        System.out.println("Usuarios Conectados: " + clientes.size());
                    }

                    Thread.sleep(100);

                }

            }catch (IOException ioException){

                handleError();
            }catch (InterruptedException ite){
                handleError();
            }
        }
    });


    private void handleError(){

        closeStream();

    }

    private void closeStream(){

        try{

            input.close();
            output.close();
            clientSocket.close();

        }catch (IOException ioe){
            Log.e("liu","closeStream fail in webSocketServerCtrl");
            ioe.printStackTrace();
        }


    }


}
