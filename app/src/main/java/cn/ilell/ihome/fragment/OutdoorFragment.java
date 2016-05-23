package cn.ilell.ihome.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import cn.ilell.ihome.R;
import cn.ilell.ihome.base.BaseFragment;
import cn.ilell.ihome.io.AudioClient;

/**
 * Created by Monkey on 2015/6/29.
 */
public class OutdoorFragment extends BaseFragment{

    private Switch switch_all = null;

    public static AudioClient audioClient = null;

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

    }

    /*public void onPause() {
        audioClient.stop();
    }*/
    /**
     * 调用finish方法时，这方法将被激发
     * 设置输入流为空，调用父类的onDestroy销毁资源
     */

    protected void initViewThis() {
        initView();
        switch_all = (Switch) mView.findViewById(R.id.outdoor_switch);
        //text = (TextView) mView.findViewById(R.id.outdoor_textView);
    }

    private void setListener() {
        switch_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {//打开
                    web.loadUrl("http://115.159.127.79/ihome/backdeal/VideoForPhone.php");
                    audioClient = new AudioClient();
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
                } else {// 关闭
                    web.loadUrl("about:blank");
                    audioClient.stop();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

           /* case R.id.outdoor_btn_stop:
                break;*/

        }
    }
}
