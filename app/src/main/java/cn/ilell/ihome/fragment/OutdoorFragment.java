package cn.ilell.ihome.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;
import java.io.InputStream;

import cn.ilell.ihome.R;
import cn.ilell.ihome.base.BaseFragment;
import cn.ilell.ihome.io.AudioClient;
import cn.ilell.ihome.io.MjpegInputStream;
import cn.ilell.ihome.view.MjpegView;

/**
 * Created by Monkey on 2015/6/29.
 */
public class OutdoorFragment extends BaseFragment{

    private Button btn_connect = null;

    private InputStream is = null;
    private MjpegInputStream mis = null;
    private MjpegView mjpegView = null;
    private AudioClient audioClient = null;

    private String ip = "192.168.0.106";
    private String mjpeg_port = "8080";
    private int audio_port = 8081;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frag_outdoor, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.outdoor_swiperefreshlayout);


        // 刷新时，指示器旋转后变化的颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_blue_light, R.color.main_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mjpegView = (MjpegView) mView.findViewById(R.id.outdoor_mjpegview);

        initView();
        setListener();

    }
    /**
     * 调用com/mjpeg/view的mjpegView.java类中mjpegView的众多方法来初始化自定义控件com.mjpeg.view.MjpegView
     * MjpegView类是重头戏
     */
    private void initMjpegView() {
        if (mis != null) {
            mjpegView.setSource(mis);// 设置数据来源
            mjpegView.setDisplayMode(mjpegView.getDisplayMode());/*设置mjpegview的显示模式*/
            /**
             * setFps和getFps方法是为了在屏幕的右上角动态显示当前的帧率
             * 如果我们只需观看画面，下面这句完全可以省去
             */
            mjpegView.setFps(mjpegView.getFps());
            /**
             * 调用mjpegView中的线程的run方法，开始显示画面
             */
            mjpegView.startPlay();
        }
    }
    /**
     * 调用finish方法时，这方法将被激发
     * 设置输入流为空，调用父类的onDestroy销毁资源
     */
    public void onDestroy() {
        is = null;
        super.onDestroy();
    }

    private void initView() {
        btn_connect = (Button) mView.findViewById(R.id.oudoor_btn_connect);
        //text = (TextView) mView.findViewById(R.id.outdoor_textView);
    }

    private void setListener() {
        btn_connect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.oudoor_btn_connect:
                String action = "http://" + ip + ":"+ mjpeg_port + "/?action=stream";
                //is = http(action);
                //MjpegInputStream.initInstance(is);
                //mis = MjpegInputStream.getInstance();
                audioClient = new AudioClient();
                audioClient.startAudioClient(ip, audio_port);
                new ConnectTask().execute(ip);
                //initMjpegView();
                //do something
                //text.setText("123");
                break;

           /* case R.id. myButton2:

                //do something

                break;*/

        }
    }
    /**
     * 连接线程
     * 此类的作用是在后台线程里执行http连接，连接卡住不会影响UI运行，适合于运行时间较长但又不能影响前台线程的情况
     * 异步任务，有3参数和4步:onPreExecute()，doInBackground()，onProgressUpdate()，onPostExecute()
     * onPreExecute()：运行于UI线程，一般为后台线程做准备，如在用户接口显示进度条
     * doInBackground():当onPreExecute执行后，马上被触发，执行花费较长时间的后台运算，将返回值传给onPostExecute
     * onProgressUpdate():当用户调用 publishProgress()将被激发，执行的时间未定义，这个方法可以用任意形式显示进度
     * 一般用于激活一个进度条或者在UI文本领域显示logo
     * onPostExecute():当后台进程执行后在UI线程被激发，把后台执行的结果通知给UI
     * 参数一:运行于后台的doInBackground的参数类型
     * 参数二:doInBackground计算的通知给UI线程的单元类型，即运行于UI线程onProgressUpdate的参数类型，这里没用到
     * 参数三:doInBackground的返回值，将传给onPostExecute作参数
     * @author Administrator
     *
     */
    private class ConnectTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
                    /**
                     * 在浏览器观察画面时,也是输入下面的字符串网址
                     */
                    String action = "http://" + ip + ":"+ mjpeg_port + "/?action=stream";
                    is = http(action);
                    if (is != null) { /*第一次必须输入IP，下次登录时才可找到之前登录成功后的IP*/
                        MjpegInputStream.initInstance(is);
                    }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (is != null) {
                MjpegInputStream.initInstance(is);
                mis = MjpegInputStream.getInstance();
                initMjpegView();
            } else{
                //Toast.makeText(MonitorActivity.this, "123", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);
        }

        /**
         * 功能：http连接
         * Android提供两种http客户端， HttpURLConnection 和 Apache HTTP Client，它们都支持HTTPS，能上传和下载文件
         * 配置超时时间，用于IPV6和 connection pooling， Apache HTTP client在Android2.2或之前版本有较少BUG
         * 但在Android2.2或之后，HttpURLConnection是更好的选择，在这里我们用的是 Apache HTTP Client
         * 凡是对IO的操作都会涉及异常，所以要try和catch
         * @param url
         * @return InputStream
         */
        private InputStream http(String url) {
            HttpResponse res;
            DefaultHttpClient httpclient = new DefaultHttpClient();/*创建http客户端，才能调用它的各种方法*/
            httpclient.getParams().setParameter(
                    CoreConnectionPNames.CONNECTION_TIMEOUT, 500);/*设置超时时间*/

            try {
                HttpGet hg = new HttpGet(url);/*这是GET方法的http API， GET方法是默认的HTTP请求方法*/
                res = httpclient.execute(hg);
                return res.getEntity().getContent(); // 从响应中获取消息实体内容
            } catch (IOException e) {
            }

            return null;
        }

    }
}
