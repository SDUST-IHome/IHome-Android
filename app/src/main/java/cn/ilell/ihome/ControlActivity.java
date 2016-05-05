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
import cn.ilell.ihome.fragment.BedroomFragment;
import cn.ilell.ihome.fragment.KitchenFragment;
import cn.ilell.ihome.fragment.ParlorFragment;
import cn.ilell.ihome.fragment.ToiletFragment;
import cn.ilell.ihome.utils.SnackbarUtil;

public class ControlActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        // 初始化各种控件
        initViews();

        // 初始化mTitles、mFragments等ViewPager需要的数据
        //这里的数据都是模拟出来了，自己手动生成的，在项目中需要从网络获取数据
        initData();

        // 对各种控件进行设置、适配、填充数据
        configViews();
        //与后台服务捆绑
        //与后台服务捆绑
        bindMsgService();
        //初始化侧边栏头部
        initNavHead();

        mContext = this;
        mClass = ControlActivity.class;
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


    private void changeActivity(final Class mClass) {
        new Thread() {
            public void run() {
                //休眠0.256
                try {
                    Thread.sleep(256);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                //制定intent要启动的类
                intent.setClass(ControlActivity.this,mClass);
                //启动一个新的Activity
                startActivity(intent);
                //关闭当前的
                ControlActivity.this.finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            };
        }.start();
    }
    @Override
    public void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }

    protected void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.control_drawerlayout);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.control_coordinatorlayout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.control_appbarlayout);
        mToolbar = (Toolbar) findViewById(R.id.control_toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.control_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.control_viewpager);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.control_floatingactionbutton);
        mNavigationView = (NavigationView) findViewById(R.id.control_navigationview);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // FloatingActionButton的点击事件
            case R.id.control_floatingactionbutton:
                SnackbarUtil.show(v, getString(R.string.plusone), 0);
                break;

        }
    }
}
