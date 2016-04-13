package cn.ilell.ihome.base;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import cn.ilell.ihome.R;
import cn.ilell.ihome.adapter.MyViewPagerAdapter;

/**
 * Created by lhc35 on 2016/4/13.
 */
public class BaseActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    //初始化各种控件，照着xml中的顺序写
    protected DrawerLayout mDrawerLayout;
    protected CoordinatorLayout mCoordinatorLayout;
    protected AppBarLayout mAppBarLayout;
    protected Toolbar mToolbar;
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;
    protected FloatingActionButton mFloatingActionButton;
    protected NavigationView mNavigationView;

    // TabLayout中的tab标题
    protected String[] mTitles;
    // 填充到ViewPager中的Fragment
    protected List<Fragment> mFragments;
    // ViewPager的数据适配器
    protected MyViewPagerAdapter mViewPagerAdapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageSelected(int position) {
        mToolbar.setTitle(mTitles[position]);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {

    }
}
