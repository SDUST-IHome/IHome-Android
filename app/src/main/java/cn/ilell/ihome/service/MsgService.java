package cn.ilell.ihome.service;

import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

            socketIn = new DataInputStream(serviceSocket.getInputStream());
            socketOut = new DataOutputStream(serviceSocket.getOutputStream());
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
        try {
            socketOut.write(str.getBytes());
            socketOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                        recvMsg = socketIn.readUTF();
                        mHandler.sendMessage(mHandler.obtainMessage());
                    } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };  //监听接受服务器消息

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            String[] data = recvMsg.split("/");
            if (data[0].equals("0")) {    //网关级消息
                if (data[1].equals("4")) {  //火警
                    if (data[2].equals("0")) {  //取消火警
                        mNotificationManager.cancel(notifyId_WARNING);//删除一个特定的通知ID对应的通知
                    }
                    else if (data[2].equals("1")) { //火警提醒
                        showCzNotify("火灾警报","传感器检测到您的家中存在较高浓度的有害气体");
                    }
                }
            }
            else if (data[0].equals("1")) {   //用户级消息
                if (data[1].equals("2")) {  //家庭留言更新
                    showIntentActivityNotify("您有新的家庭留言",data[2]);
                }
                else if (data[1].equals("3")) {  //人员到访通知
                    showIntentActivityNotify("有客人到访",data[2]);
                }
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
