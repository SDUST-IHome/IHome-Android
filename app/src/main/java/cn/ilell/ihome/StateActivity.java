package cn.ilell.ihome;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import cn.ilell.ihome.base.BaseActivity;
import cn.ilell.ihome.fragment.ModeFragment;
import cn.ilell.ihome.fragment.StateFragment;
import cn.ilell.ihome.utils.SnackbarUtil;

public class StateActivity extends BaseActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);


        // 初始化各种控件
        initViews();

        // 初始化mTitles、mFragments等ViewPager需要的数据
        //这里的数据都是模拟出来了，自己手动生成的，在项目中需要从网络获取数据
        initData();

        // 对各种控件进行设置、适配、填充数据
        configViews();

        //与后台服务捆绑
        bindMsgService();
        //初始化侧边栏头部
        initNavHead();

        mContext = this;
        mClass = StateActivity.class;

    }

    protected void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.state_drawerlayout);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.state_coordinatorlayout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.state_appbarlayout);
        mToolbar = (Toolbar) findViewById(R.id.state_toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.state_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.state_viewpager);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.state_floatingactionbutton);
        mNavigationView = (NavigationView) findViewById(R.id.state_navigationview);
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



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // FloatingActionButton的点击事件
            case R.id.state_floatingactionbutton:
                SnackbarUtil.show(v, getString(R.string.plusone), 0);
                break;
            case R.id.id_header_face:
                Intent intent = new Intent();
                //制定intent要启动的类
                intent.setClass(StateActivity.this, LoginActivity.class);
                //启动一个新的Activity
                startActivity(intent);
                break;

        }
    }


}
