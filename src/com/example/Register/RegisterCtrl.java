package com.example.Register;

import com.example.protobuf.ProtoBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/8/13
 * Time: 8:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegisterCtrl {

    public RegisterCtrl(){


    }

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





}
