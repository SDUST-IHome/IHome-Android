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
import cn.ilell.ihome.fragment.BrightFragment;
import cn.ilell.ihome.fragment.HumidFragment;
import cn.ilell.ihome.fragment.TempFragment;
import cn.ilell.ihome.utils.SnackbarUtil;

public class HistoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

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
        mClass = HistoryActivity.class;
    }
    @Override
    public void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }
    private void initData() {

        // Tab的标题采用string-array的方法保存，在res/values/arrays.xml中写
        mTitles = getResources().getStringArray(R.array.history_tab_titles);

        //初始化填充到ViewPager中的Fragment集合
        mFragments = new ArrayList<>();

        Bundle tempBundle = new Bundle();
        tempBundle.putInt("flag", 0);
        TempFragment tempFragment = new TempFragment();
        tempFragment.setArguments(tempBundle);
        mFragments.add(0, tempFragment);

        Bundle humidBundle = new Bundle();
        humidBundle.putInt("flag", 1);
        HumidFragment humidFragment = new HumidFragment();
        humidFragment.setArguments(humidBundle);
        mFragments.add(1, humidFragment);

        Bundle brightBundle = new Bundle();
        brightBundle.putInt("flag", 2);
        BrightFragment brightFragment = new BrightFragment();
        brightFragment.setArguments(brightBundle);
        mFragments.add(2, brightFragment);
/*
        Bundle bedroomBundle = new Bundle();
        bedroomBundle.putInt("flag", 3);
        BedroomFragment bedroomFragment = new BedroomFragment();
        bedroomFragment.setArguments(bedroomBundle);
        mFragments.add(3, bedroomFragment);*/

    }

    protected void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.history_drawerlayout);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.history_coordinatorlayout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.history_appbarlayout);
        mToolbar = (Toolbar) findViewById(R.id.history_toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.history_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.history_viewpager);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.history_floatingactionbutton);
        mNavigationView = (NavigationView) findViewById(R.id.history_navigationview);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // FloatingActionButton的点击事件
            case R.id.history_floatingactionbutton:
                SnackbarUtil.show(v, getString(R.string.plusone), 0);
                break;

        }
    }
}
