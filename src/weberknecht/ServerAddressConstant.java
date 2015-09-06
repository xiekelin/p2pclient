package weberknecht;

/**
 * Created by Administrator on 8/17/13.
 */
//临时类（方便开发）  把服务器地址从socket.java分离出来，这样每次更新工程的时候，若发现时这个文件更新了，就可以直接忽略，
 //而不用去关心是socket.java内部实现改了还是仅仅服务器地址改了
public class ServerAddressConstant {
    //this class should be remove when in production    shift+we32
    public static String addr = "ws://172.22.71.15:6611/src";

 }
