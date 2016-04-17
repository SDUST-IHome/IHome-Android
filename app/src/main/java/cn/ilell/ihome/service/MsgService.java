package cn.ilell.ihome.service;

import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by lhc35 on 2016/4/16.
 */
public class MsgService extends MyService {

    public void onCreate() {
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //以下是所加的代码
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        initService();
        initNotify();
        connectServer();
        return new MsgBinder();
    }
    /**
     * 连接服务器
     */
    public void connectServer(){
        try {
            serviceSocket = new Socket(serverIP,serverPort);

            socketIn = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream(), "GBK"));
            socketOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(serviceSocket.getOutputStream(), "GBK")), true);
            sendMsg("Phone");
            Thread mThread = new Thread(mRunable);
            mThread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String str) {
        socketOut.write(str);
        socketOut.flush();
    }

    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    char[] temp = new char[1024];
                    if (socketIn.read(temp) != 0) {
                        recvMsg = new String(temp);
                        mHandler.sendMessage(mHandler.obtainMessage());
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    };  //监听接受服务器消息

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            String[] data = recvMsg.split("|");
            switch (data[0]) {
                case "2":   //报警
                    showCzNotify(data[1],data[2]);
                    break;
                case "1":   //提醒
                    showIntentActivityNotify(data[1],data[2]);
                    break;
                case "0":   //非提示性指令
                    if (data[1].equals("0")) {  //关闭操作
                        if (data[2].equals("2")) {  //取消报警
                            mNotificationManager.cancel(notifyId_WARNING);//删除一个特定的通知ID对应的通知
                        }
                        else if (data[2].equals("1")) { //取消消息提醒
                            mNotificationManager.cancel(notifyId_MESSAGE);//删除一个特定的通知ID对应的通知
                        }
                    }
                    else if (data[1].equals("1")) { //打开操作

                    }
                    break;
            }
            onProgressListener.onProgress(recvMsg);    //将消息传到前端
        };
    };  //处理接收到的信息

    public class MsgBinder extends Binder {
        /**
         * 获取当前Service的实例
         * @return
         */
        public MsgService getService(){
            return MsgService.this;
        }
    }

}
