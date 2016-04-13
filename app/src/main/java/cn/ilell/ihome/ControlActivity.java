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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import cn.ilell.ihome.adapter.MyViewPagerAdapter;
import cn.ilell.ihome.base.BaseActivity;
import cn.ilell.ihome.fragment.ModeFragment;
import cn.ilell.ihome.fragment.StateFragment;
import cn.ilell.ihome.utils.SnackbarUtil;

import static android.support.design.widget.TabLayout.MODE_SCROLLABLE;

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

    }

    private void initData() {

        // Tab的标题采用string-array的方法保存，在res/values/arrays.xml中写
        mTitles = getResources().getStringArray(R.array.control_tab_titles);

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


        /*for (int i = 0; i < mTitles.length; i++) {
            Bundle mBundle = new Bundle();
            mBundle.putInt("flag", i);
            StateFragment mFragment = new StateFragment();
            mFragment.setArguments(mBundle);
            mFragments.add(i, mFragment);
        }*/

    }

    private void configViews() {

        // 设置显示Toolbar
        setSupportActionBar(mToolbar);

        // 设置Drawerlayout开关指示器，即Toolbar最左边的那个icon
        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        //给NavigationView填充顶部区域，也可在xml中使用app:headerLayout="@layout/header_nav"来设置
        mNavigationView.inflateHeaderView(R.layout.header_nav);
        //给NavigationView填充Menu菜单，也可在xml中使用app:menu="@menu/menu_nav"来设置
        mNavigationView.inflateMenu(R.menu.menu_nav);

        // 自己写的方法，设置NavigationView中menu的item被选中后要执行的操作
        onNavgationViewMenuItemSelected(mNavigationView);

        // 初始化ViewPager的适配器，并设置给它
        mViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), mTitles, mFragments);
        mViewPager.setAdapter(mViewPagerAdapter);
        // 设置ViewPager最大缓存的页面个数
        mViewPager.setOffscreenPageLimit(5);
        // 给ViewPager添加页面动态监听器（为了让Toolbar中的Title可以变化相应的Tab的标题）
        mViewPager.addOnPageChangeListener(this);

        mTabLayout.setTabMode(MODE_SCROLLABLE);
        // 将TabLayout和ViewPager进行关联，让两者联动起来
        mTabLayout.setupWithViewPager(mViewPager);
        // 设置Tablayout的Tab显示ViewPager的适配器中的getPageTitle函数获取到的标题
        mTabLayout.setTabsFromPagerAdapter(mViewPagerAdapter);

        // 设置FloatingActionButton的点击事件
        mFloatingActionButton.setOnClickListener(this);


    }

    /**
     * 设置NavigationView中menu的item被选中后要执行的操作
     *
     * @param mNav
     */
    private void onNavgationViewMenuItemSelected(NavigationView mNav) {
        mNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                String msgString = "";

                switch (menuItem.getItemId()) {
                    case R.id.nav_menu_state:
                        changeActivity(StateActivity.class);
                        break;
                    case R.id.nav_menu_control:
                        msgString = (String) menuItem.getTitle();
                        break;
                    case R.id.nav_menu_history:
                        msgString = (String) menuItem.getTitle();
                        break;
                    case R.id.nav_menu_monitor:
                        msgString = (String) menuItem.getTitle();
                        break;
                }

                // Menu item点击后选中，并关闭Drawerlayout
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                // android-support-design兼容包中新添加的一个类似Toast的控件。
                SnackbarUtil.show(mViewPager, msgString, 0);

                return true;
            }
        });
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

    private void initViews() {
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
