/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package websocketserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class ThreadServerCliente  {

    protected HashSet<Socket> clientes;
    protected Socket clientSocket;

    public ThreadServerCliente(Socket clientSocket, HashSet<Socket> clientes) {
        this.clientes = clientes;
        this.clientSocket = clientSocket;
    }

//    @Override
//    public void run() {
//        boolean finish = false;
//        InputStream in = null;
//        OutputStream out = null;
//        WebSockServerHandshake req = new WebSockServerHandshake();
//        String mensaje;
//        byte[] bytesMensaje;
//
//        /*
//         * Hago la conexión con el navegador web
//         */
//
//        try {
//            in = clientSocket.getInputStream();
//            out = clientSocket.getOutputStream();
//
//            req.analyseRequest(in);
//            out.write(req.handShakeResponse().getBytes());
//            out.flush();
//        } catch (IOException ex) {
//        }
//
//        while (!finish) {
//            try {
//                bytesMensaje = leerUnidad(in);
//                if(isCerrado(bytesMensaje)){
//                    finish = true;
//                    clientes.remove(clientSocket);
//                    System.out.println("Conexion cerrada con: " + clientSocket.getInetAddress() + " " + clientSocket.getPort());
//                    System.out.println("Usuarios Conectados: " + clientes.size());
//                }else{
//                    mensaje = sacarDatos(bytesMensaje);
//                    enviarATodos(mensaje);
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(ThreadServerCliente.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }


    /**
     * Hace un streaming del mensaje pasado como parametro a todos los usuarios conectados.
     * @param mensaje
     */
   protected void enviarATodos(String mensaje) {
        OutputStream out = null;
        for (Socket c : clientes) {
            try {
                out = c.getOutputStream();
                out.write(datosParaEnviar(mensaje));
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(ThreadServerCliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Analiza el opcode y se fija que tipo de mensaje es. Solo lo
     * implementé para ver si el navegador cierra la conexion o no.
     * @param byteMensaje cadena de bytes del mensaje recibido
     * @return true si esta cerrado
     */
    protected boolean isCerrado(byte[] byteMensaje) {
        /*
         * En los primeros 4 bits del mensaje esta el opcode, si el opcode da 8
         * quiere decir que el otro extremo cerro la comunicacion.
         */
        if ((byteMensaje[0] & 15) == 8) {
            return true;
        } else if ((byteMensaje[0] & 15) == 1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Lee una unidad y devuelve la totalidad de los bytes leidos. Devuelve unicamente los bytes
     * con informacion para esto analiza el tamaño del mensaje y solamente trae los datos que
     * pertenecen a ese mensaje.
     * @param in
     * @return
     * @throws java.io.IOException
     */
    protected byte[] leerUnidad(InputStream in) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] b = new byte[2];
        in.read(b, 0, 2); //solo recibo 2 porque en el segundo es donde tengo la info del tamano total

        bytes.write(b); //tengo un buffer donde voy poniendo los bytes a medida que los saco.

        int tamanoASacar = calcularEspacioDeLongitud(b);

        if (tamanoASacar != 2 && tamanoASacar != 8) {
            b = new byte[tamanoASacar + 4];
            in.read(b, 0, tamanoASacar + 4);
            bytes.write(b);
        } else if (tamanoASacar == 2) {
            b = new byte[2];
            in.read(b, 0, 2);
            bytes.write(b);

            int tamano = (int) getInt(new byte[]{b[1], b[0]});

            b = new byte[tamano + 4];
            in.read(b, 0, tamano + 4);
            bytes.write(b);
        }

        return bytes.toByteArray();

    }

    /**
     * Devuelve la cantidad de bytes que contienen la información del tamaño del mensaje.
     * En el caso que no haya bytes extras se devuelve el tamaño de los datos.
     * @param b cadena de bytes con los datos
     * @return
     */
    private int calcularEspacioDeLongitud(byte[] b) {
        if ((b[1] & 127) == 126) {
            return 2;
        } else if ((b[1] & 127) == 127) {
            return 8;
        } else {
            return (b[1] & 127);
        }
    }

    /**
     *
     * @param mensaje
     * @return
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.IOException
     */
    protected byte[] datosParaEnviar(String mensaje) throws UnsupportedEncodingException, IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bytes.write(129);
        if (mensaje.length() <= 125) {
            bytes.write(mensaje.length());
        } else {
            bytes.write(126);
            bytes.write(mensaje.length() >> 8);
            bytes.write(mensaje.length() & 0xFF);
        }

        bytes.write(mensaje.getBytes("utf-8"));
        return bytes.toByteArray();
    }


    /**
     * Siguiendo el estandar hybi 10 desenmascara los datos.
     * @param bytes
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    protected String sacarDatos(byte[] bytes) throws UnsupportedEncodingException {
        int largoPayload, opcode;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ByteArrayOutputStream bufferPayloadLargo = new ByteArrayOutputStream();

        /**
         * largoPayload contiene el tamano real del payload es menor a 126, si es igual el tamano total
         * se guarda en los dos bytes siguientes. La variable que tiene el valor real se llama tamanoTotal
         */
        largoPayload = bytes[1] & 127;
        opcode = bytes[0] & 15;

        int tamanoTotal = 0;

        if (largoPayload == 126) {
            tamanoTotal = (int) getInt(new byte[]{bytes[3], bytes[2]});
        } else {
            tamanoTotal = largoPayload;
        }

        byte[] mascara = new byte[4];
        byte[] cuerpo = new byte[tamanoTotal];

        if (largoPayload == 126) {
            sacarMascara(bytes, mascara, 4);
            sacarPayload(bytes, cuerpo, 8, tamanoTotal);
        } else {
            sacarMascara(bytes, mascara, 2);
            sacarPayload(bytes, cuerpo, 6, tamanoTotal);
        }

        for (int i = 0; i < tamanoTotal; i++) {
            buffer.write(cuerpo[i] ^ mascara[i % 4]);
        }
        return new String(buffer.toByteArray(), "utf-8");
    }

    /**
     * Imprime los bits de un byte. Muy util para analizar el frame del protocolo.
     * @param b
     */
    public void printBits(byte b) {
        int mask = 0x80;
        while (mask > 0) {
            if ((mask & b) != 0) {
                System.out.print('1');
            } else {
                System.out.print('0');
            }
            mask >>= 1;
        }
    }

    public long getInt(byte[] array) {
        long value = 0;
        for (int i = 0; i < array.length; i++) {
            value += (array[i] & 0xff) << (8 * i);
        }
        return value;
    }

    private void sacarMascara(byte[] bytes, byte[] mascara, int inicio) {
        for (int i = 0; i < 4; i++) {
            mascara[i] = bytes[inicio + i];
        }
    }

    private void sacarPayload(byte[] bytes, byte[] payload, int inicio, int largoPayload) {
        for (int i = 0; i < largoPayload; i++) {
            payload[i] = bytes[inicio + i];
        }
    }
}
