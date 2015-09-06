package com.example.P2P;


import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/5/13
 * Time: 10:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class test {

    boolean finish = false;

    public test(){
        thread1.start();
        thread2.start();
    }

    Thread thread1 = new Thread(new Runnable() {
        @Override
        public void run() {

            while(!finish){
                 System.out.println("continue");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    });

    public void setFinish(){
        finish = true;
        System.out.println("finish");
    }

    Thread thread2 = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(400);
                setFinish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    public static void main(String arg[]){

         test test = new test();
        HashMap<String,test> hashMap = new HashMap<String, com.example.P2P.test>();
        hashMap.put("a",test);
        test test1 = hashMap.get("a");
        test1.setFinish();

    }

}

