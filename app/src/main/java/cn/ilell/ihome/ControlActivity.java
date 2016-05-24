package cn.ilell.ihome;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.ilell.ihome.base.BaseActivity;
import cn.ilell.ihome.fragment.BedroomFragment;
import cn.ilell.ihome.fragment.KitchenFragment;
import cn.ilell.ihome.fragment.ParlorFragment;
import cn.ilell.ihome.fragment.ToiletFragment;
import cn.ilell.ihome.utils.OperatingCommand;

public class ControlActivity extends BaseActivity {

    //语音识别部分
    private static String TAG = ControlActivity.class.getSimpleName();
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    int ret = 0; // 函数调用返回值
    //语音识别部分

    int test = 0;

    private OperatingCommand operatingCommand;  //语音操作指令处理
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化各种控件
        initViews();

        // 初始化mTitles、mFragments等ViewPager需要的数据
        //这里的数据都是模拟出来了，自己手动生成的，在项目中需要从网络获取数据
        initData();

        // 对各种控件进行设置、适配、填充数据
        configViews();
        //与后台服务捆绑
        bindMsgService();

        mContext = this;
        mClass = ControlActivity.class;

        mFloatingActionButton.setImageResource(R.drawable.ic_main_voice);
        operatingCommand = new OperatingCommand();

        //语音识别部分
        SpeechUtility.createUtility(ControlActivity.this, "appid=573f022f");
        mIatDialog = new RecognizerDialog(ControlActivity.this, mInitListener);
    }

    private void initData() {

        // Tab的标题采用string-array的方法保存，在res/values/arrays.xml中写
        mTitles = getResources().getStringArray(R.array.control_tab_titles);

        //初始化填充到ViewPager中的Fragment集合
        mFragments = new ArrayList<>();
        Bundle parlorBundle = new Bundle();
        parlorBundle.putInt("flag", 0);
        ParlorFragment parlorFragment = new ParlorFragment();
        parlorFragment.setArguments(parlorBundle);
        mFragments.add(0, parlorFragment);

        Bundle kitchenBundle = new Bundle();
        kitchenBundle.putInt("flag", 1);
        KitchenFragment kitchenFragment = new KitchenFragment();
        kitchenFragment.setArguments(kitchenBundle);
        mFragments.add(1, kitchenFragment);

        Bundle toiletBundle = new Bundle();
        toiletBundle.putInt("flag", 2);
        ToiletFragment toiletFragment = new ToiletFragment();
        toiletFragment.setArguments(toiletBundle);
        mFragments.add(2, toiletFragment);

        Bundle bedroomBundle = new Bundle();
        bedroomBundle.putInt("flag", 3);
        BedroomFragment bedroomFragment = new BedroomFragment();
        bedroomFragment.setArguments(bedroomBundle);
        mFragments.add(3, bedroomFragment);

    }

    public void onFloatingactionButtonClick(View v) {
        // 移动数据分析，收集开始听写事件
        FlowerCollector.onEvent(ControlActivity.this, "iat_recognize");
        mIatResults.clear();
        mIatDialog.setParameter(SpeechConstant.ASR_PTT, "0");
        mIatDialog.setListener(mRecognizerDialogListener);
        mIatDialog.show();
    }   //浮动按钮单击事件

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(ControlActivity.this, "初始化失败，错误码：" + code, Toast.LENGTH_LONG ).show();
            }
        }
    };
    //打印结果
    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }

        Toast.makeText(ControlActivity.this, operatingCommand.dealCommand(resultBuffer.toString()), Toast.LENGTH_LONG ).show();
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            if (!isLast)
                printResult(results);
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            Toast.makeText(ControlActivity.this,error.getPlainDescription(true), Toast.LENGTH_LONG ).show();
        }
    };

    @Override
    public void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }

    protected void onResume() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onResume(ControlActivity.this);
        FlowerCollector.onPageStart(TAG);
        super.onResume();
    }

    protected void onPause() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(ControlActivity.this);
        super.onPause();
    }
}
