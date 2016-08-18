package cn.ilell.ihome.fragment;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import cn.ilell.ihome.R;
import cn.ilell.ihome.base.BaseFragment;
import cn.ilell.ihome.io.AudioClient;

/**
 * Created by Monkey on 2015/6/29.
 */
public class OutdoorFragment extends BaseFragment{

    private Button btn_connect = null;
    private Button btn_stop = null;
    public static TextView text_state = null;

    public static AudioClient audioClient = null;

    public static MediaPlayer mMediaPlayer = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frag_outdoor, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViewThis();
        setListener();
        audioClient = new AudioClient();

        Bundle bundle = getActivity().getIntent().getExtras();
        boolean ring = bundle.getBoolean("Ring",false);
        if (ring)   startAlarm();
    }

    /*public void onPause() {
        audioClient.stop();
        mMediaPlayer.stop();
    }*/
    /**
     * 调用finish方法时，这方法将被激发
     * 设置输入流为空，调用父类的onDestroy销毁资源
     */

    protected void initViewThis() {
        initView();
        //text = (TextView) mView.findViewById(R.id.outdoor_textView);
        btn_connect = (Button) mView.findViewById(R.id.outdoor_connect);
        btn_stop = (Button) mView.findViewById(R.id.outdoor_stop);
        text_state = (TextView) mView.findViewById(R.id.outdoor_text_state);
    }

    private void setListener() {
        btn_connect.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                web.loadUrl("http://115.159.127.79/ihome/backdeal/VideoForPhone.php");
                new Thread(){
                    public void run(){
                        int result = audioClient.autoStart();
                        if (result == 0) {
                            ((Activity)mContext).runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "服务器未响应", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }.start();
            }
        });

        btn_stop.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                mMediaPlayer.stop();
                web.loadUrl("about:blank");
                audioClient.stop();
            }
        });
    }
    private void startAlarm() {
        mMediaPlayer = MediaPlayer.create(mContext, getSystemDefultRingtoneUri());
        mMediaPlayer.setLooping(true);
        try {
            mMediaPlayer.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }
    //获取系统默认铃声的Uri
    private Uri getSystemDefultRingtoneUri() {
        return RingtoneManager.getActualDefaultRingtoneUri(mContext,
                RingtoneManager.TYPE_RINGTONE);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

           /* case R.id.outdoor_btn_stop:
                break;*/

        }
    }
}
