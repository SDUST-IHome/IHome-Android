package cn.ilell.ihome;

import android.os.Bundle;

import java.util.ArrayList;

import cn.ilell.ihome.base.BaseActivity;
import cn.ilell.ihome.fragment.ModeFragment;
import cn.ilell.ihome.fragment.StateFragment;

public class StateActivity extends BaseActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

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
        mClass = StateActivity.class;

    }

    @Override
    public void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }

    private void initData() {

        // Tab的标题采用string-array的方法保存，在res/values/arrays.xml中写
        mTitles = getResources().getStringArray(R.array.state_tab_titles);

        //初始化填充到ViewPager中的Fragment集合
        mFragments = new ArrayList<>();
        Bundle stateBundle = new Bundle();
        stateBundle.putInt("flag", 0);
        StateFragment stateFragment = new StateFragment();
        stateFragment.setArguments(stateBundle);
        mFragments.add(0, stateFragment);
        Bundle modeBundle = new Bundle();
        modeBundle.putInt("flag", 1);
        ModeFragment modeFragment = new ModeFragment();
        modeFragment.setArguments(modeBundle);
        mFragments.add(1, modeFragment);
    }


}
