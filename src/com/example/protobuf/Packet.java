package com.example.protobuf;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/2/13
 * Time: 9:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class Packet {


    public static int GetPackageTypeByByte(byte[] arg_bytes) {
        try {

              ProtoBuffer.MainInterFaceOrBuilder mainInterfaceBuilder = ProtoBuffer.MainInterFace
                      .parseFrom(arg_bytes);

              if(mainInterfaceBuilder.hasPackageType()){
                  return mainInterfaceBuilder.getPackageType().getNumber();
              }else{
                  throw new Exception("could not get packageType");
              }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static byte[] registerBuffer = setRegisterBuffer();

    public static byte[] setRegisterBuffer(){

        try{

            ProtoBuffer.MainInterFace.Builder builder  = ProtoBuffer.MainInterFace.newBuilder();

            builder.setPackageType(ProtoBuffer.PackageType.register);

            ProtoBuffer.MainInterFace mainInterFace = builder.build();

            return mainInterFace.toByteArray();

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }

    public static String getPearForTest(byte[] arg_byte) throws InvalidProtocolBufferException {

        ProtoBuffer.MainInterFaceOrBuilder mainInterFaceOrBuilder = ProtoBuffer.MainInterFace.parseFrom(arg_byte);

        List<ProtoBuffer.GetPearList> pearLists = mainInterFaceOrBuilder.getGetPearListList();

        for(ProtoBuffer.GetPearList pear : pearLists){

            String ip = pear.getIp();
            int tap = pear.getTap();


        }
        return null;
    }

}
