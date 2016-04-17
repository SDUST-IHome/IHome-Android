package cn.ilell.ihome.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import cn.ilell.ihome.R;
import cn.ilell.ihome.StateActivity;

/**
 * Created by lhc35 on 2016/4/16.
 */
public class MsgService extends Service {
    private String serverIP = "115.159.127.79";  //static final
    private int serverPort = 5678;
    private Socket serviceSocket;
    private PrintWriter socketOut = null;
    private BufferedReader socketIn = null;
    private String recvMsg;
    private String sendMsg;
    /** Notification构造器 */
    NotificationCompat.Builder mBuilder;
    /** Notification管理 */
    public NotificationManager mNotificationManager;
    /** Notification的ID */
    int notifyId = 100;
    /**
     * 更新的回调接口
     */
    private OnProgressListener onProgressListener;
    /**
     * 注册回调接口的方法，供外部调用
     * @param onProgressListener
     */
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    /**
     * 连接服务器
     */
    public void connectServer(){
        try {
            serviceSocket = new Socket(serverIP,serverPort);
            socketIn = new BufferedReader(new InputStreamReader(serviceSocket.getInputStream(), "GBK"));
            socketOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(serviceSocket.getOutputStream(), "GBK")), true);
            Thread mThread = new Thread(mRunable);
            mThread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showIntentActivityNotify();
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
        return new MsgBinder();
    }
    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    if ((recvMsg = socketIn.readLine()) != null) {
                        mHandler.sendMessage(mHandler.obtainMessage());
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    };  //监听接受服务器消息

    /** 显示通知栏点击跳转到指定Activity */
    public void showIntentActivityNotify(){
        // Notification.FLAG_ONGOING_EVENT --设置常驻 Flag;Notification.FLAG_AUTO_CANCEL 通知栏上点击此通知后自动清除此通知
//		notification.flags = Notification.FLAG_AUTO_CANCEL; //在通知栏上点击此通知后自动清除此通知
        mBuilder.setAutoCancel(true)//点击后让通知将消失
                .setContentTitle("测试标题")
                .setContentText("点击跳转")
                .setTicker("点我");
        //点击的意图ACTION是跳转到Intent
        Intent resultIntent = new Intent(this, StateActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(notifyId, mBuilder.build());
    }
    /**
     * 初始化要用到的系统服务
     */
    private void initService() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
    /** 初始化通知栏 */
    private void initNotify(){
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("测试标题")
                .setContentText("测试内容")
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
//				.setNumber(number)//显示数量
                .setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
//				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_launcher);
    }
    /**
     * @获取默认的pendingIntent,为了防止2.3及以下版本报错
     * @flags属性:
     * 在顶部常驻:Notification.FLAG_ONGOING_EVENT
     * 点击去除： Notification.FLAG_AUTO_CANCEL
     */
    public PendingIntent getDefalutIntent(int flags){
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
        return pendingIntent;
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
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
