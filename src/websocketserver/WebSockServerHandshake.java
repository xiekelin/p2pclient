
package websocketserver;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import com.example.util.ApacheBase64;


public class WebSockServerHandshake {
    private String upgrade;
    private String connection;
    private String host;
    private String secWebSocketKey;
    private String webSocketVersion;
    private String magicString = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    public String getMagicString() {
        return magicString;
    }

    public void analyseRequest(InputStream in) throws IOException{
        String str   = ".";

        while (!str.equals("")){
            str = leerLinea(in);
            if(str.startsWith("Sec-WebSocket-Key: "))
                secWebSocketKey = str.substring(19);
            if(str.startsWith("Upgrade: "))
                upgrade = str.substring(9);
            if(str.startsWith("Host: "))
                host = str.substring(6);
            if(str.startsWith("Sec-WebSocket-Version: "))
                webSocketVersion = str.substring(23);
            if(str.startsWith("Connection: "))
                connection = str.substring(12);
        }
    }

    @Override
    public String toString() {
        return "Upgrade: " + upgrade + "\n" +
                "Connection: " + connection + "\n" +
                "Host: " + host + "\n" +
                "Sec-WebSocket-key: " + secWebSocketKey + "\n" +
                "Sec-WebSocket-version: " + webSocketVersion + "\n";
    }

    public String getSecWebSocketKey() {
        return secWebSocketKey;
    }

    private String leerLinea(InputStream in) throws IOException{
        String line = "";
        int pread;
        int read = 0;
        while(true){
                pread = read;
                read = in.read();
                if(read!='\r'&&read!='\n')
                        line += (char) read;
                if(pread=='\r'&&read=='\n') break;
        }
        return line;
    }


    public String generarClave(){
        String codigo = secWebSocketKey + magicString;
        String clave="";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(codigo.getBytes("iso-8859-1"), 0, codigo.length());

            byte[] sha1hash = md.digest();

            clave = new String(ApacheBase64.encodeBase64(sha1hash));

        } catch (Exception ex) {
            System.out.println("Error Al generar la Clave");
        }
        return clave;
    }

    public String handShakeResponse(){
        String respondHandShake = "HTTP/1.1 101 Switching Protocols\r\n" +
                                            "Upgrade: websocket\r\n" +
                                            "Connection: Upgrade\r\n" +
                                            "Sec-WebSocket-Accept: " + this.generarClave() + "\r\n" +
                                            "\r\n";
        return respondHandShake;
    }
}
