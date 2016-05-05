package cn.ilell.ihome;

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
import cn.ilell.ihome.fragment.IndoorFragment;
import cn.ilell.ihome.fragment.OutdoorFragment;
import cn.ilell.ihome.utils.SnackbarUtil;

public class MonitorActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

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
        mClass = MonitorActivity.class;
    }

    private void initData() {

        // Tab的标题采用string-array的方法保存，在res/values/arrays.xml中写
        mTitles = getResources().getStringArray(R.array.monitor_tab_titles);

        //初始化填充到ViewPager中的Fragment集合
        mFragments = new ArrayList<>();

        Bundle outdoorBundle = new Bundle();
        outdoorBundle.putInt("flag", 0);
        OutdoorFragment outdoorFragment = new OutdoorFragment();
        outdoorFragment.setArguments(outdoorBundle);
        mFragments.add(0, outdoorFragment);

        Bundle indoorBundle = new Bundle();
        indoorBundle.putInt("flag", 1);
        IndoorFragment indoorFragment = new IndoorFragment();
        indoorFragment.setArguments(indoorBundle);
        mFragments.add(1, indoorFragment);

    }
    @Override
    public void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }

    protected void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.monitor_drawerlayout);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.monitor_coordinatorlayout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.monitor_appbarlayout);
        mToolbar = (Toolbar) findViewById(R.id.monitor_toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.monitor_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.monitor_viewpager);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.monitor_floatingactionbutton);
        mNavigationView = (NavigationView) findViewById(R.id.monitor_navigationview);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // FloatingActionButton的点击事件
            case R.id.monitor_floatingactionbutton:
                SnackbarUtil.show(v, getString(R.string.plusone), 0);
                break;

        }
    }
}
