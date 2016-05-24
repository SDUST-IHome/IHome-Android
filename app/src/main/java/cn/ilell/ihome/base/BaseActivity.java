package cn.ilell.ihome.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cn.ilell.ihome.ControlActivity;
import cn.ilell.ihome.FamilyFaceActivity;
import cn.ilell.ihome.FamilyMemoActivity;
import cn.ilell.ihome.FamilyMsgActivity;
import cn.ilell.ihome.HistoryActivity;
import cn.ilell.ihome.LoginActivity;
import cn.ilell.ihome.MediaActivity;
import cn.ilell.ihome.MonitorActivity;
import cn.ilell.ihome.R;
import cn.ilell.ihome.RegistActivity;
import cn.ilell.ihome.ScheduleActivity;
import cn.ilell.ihome.StateActivity;
import cn.ilell.ihome.adapter.MyViewPagerAdapter;
import cn.ilell.ihome.service.MsgService;
import cn.ilell.ihome.service.OnProgressListener;
import cn.ilell.ihome.utils.JsonParser;
import cn.ilell.ihome.utils.OperatingCommand;
import cn.ilell.ihome.utils.SnackbarUtil;
import cn.ilell.ihome.view.RoundedImageView;
import cn.ilell.ihome.view.StatusBarCompat;

import static android.support.design.widget.TabLayout.MODE_SCROLLABLE;

/**
 * Created by lhc35 on 2016/4/13.
 */
public class BaseActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    //语音识别部分
    private static String TAG = ControlActivity.class.getSimpleName();
    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    int ret = 0; // 函数调用返回值
    //语音识别部分

    private OperatingCommand operatingCommand;  //语音操作指令处理

    //服务
    protected MsgService msgService;

    //初始化各种控件，照着xml中的顺序写
    protected DrawerLayout mDrawerLayout;
    protected CoordinatorLayout mCoordinatorLayout;
    protected AppBarLayout mAppBarLayout;
    protected Toolbar mToolbar;
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;
    protected FloatingActionButton mFloatingActionButton;
    protected NavigationView mNavigationView;

    protected View headView;
    protected RoundedImageView mRoundedImageView;

    // TabLayout中的tab标题
    protected String[] mTitles;
    // 填充到ViewPager中的Fragment
    protected List<Fragment> mFragments;
    // ViewPager的数据适配器
    protected MyViewPagerAdapter mViewPagerAdapter;
    protected Context mContext;
    protected Class mClass;
    protected ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个MsgService对象
            msgService = ((MsgService.MsgBinder) service).getService();

            //注册回调接口来接收下载进度的变化
            msgService.setOnProgressListener(new OnProgressListener() {
                @Override
                public void onProgress(String recvMsg) {
                    SnackbarUtil.show(findViewById(R.id.main_floatingactionbutton), recvMsg, 0);
                    /*TextView textView = (TextView) findViewById(R.id.main_textView);
                    textView.setText(recvMsg);*/
                }
            });

        }
    };

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(BaseActivity.this, "初始化失败，错误码：" + code, Toast.LENGTH_LONG ).show();
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

        Toast.makeText(BaseActivity.this, operatingCommand.dealCommand(resultBuffer.toString()), Toast.LENGTH_LONG ).show();
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
            Toast.makeText(BaseActivity.this,error.getPlainDescription(true), Toast.LENGTH_LONG ).show();
        }
    };

    protected void initViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinatorlayout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.main_appbarlayout);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.main_floatingactionbutton);
        mNavigationView = (NavigationView) findViewById(R.id.main_navigationview);
    }

    protected void initNavHead() {
        TextView text_name = (TextView) headView.findViewById(R.id.id_header_authorname);
        TextView text_homeid = (TextView) headView.findViewById(R.id.id_header_homeid);
        text_name.setText(BaseData.account_name);
        text_homeid.setText(BaseData.home_id);

        //Toast.makeText(BaseActivity.this, BaseData.account_name, Toast.LENGTH_SHORT).show();
    }

    protected void bindMsgService() {
        //绑定Service
        Intent intent = new Intent();
        intent.setAction("cn.msgservice");
        intent.setPackage(getPackageName());
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    protected void configViews() {
        //设置状态栏的颜色
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_blue_light));
        // 设置显示Toolbar
        setSupportActionBar(mToolbar);

        // 设置Drawerlayout开关指示器，即Toolbar最左边的那个icon
        ActionBarDrawerToggle mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mActionBarDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        //给NavigationView填充顶部区域，也可在xml中使用app:headerLayout="@layout/header_nav"来设置
        headView = mNavigationView.inflateHeaderView(R.layout.header_nav);
        mRoundedImageView = (RoundedImageView) headView.findViewById(R.id.id_header_face);
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

        //设置侧边栏头部显示
        initNavHead();

        operatingCommand = new OperatingCommand();

        //语音识别部分
        SpeechUtility.createUtility(BaseActivity.this, "appid=573f022f");
        mIatDialog = new RecognizerDialog(BaseActivity.this, mInitListener);
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
                        if (mClass != StateActivity.class)
                            changeActivity(StateActivity.class);
                        break;
                    case R.id.nav_menu_control:
                        if (mClass != ControlActivity.class)
                            changeActivity(ControlActivity.class);
                        break;
                    case R.id.nav_menu_history:
                        if (mClass != HistoryActivity.class)
                            changeActivity(HistoryActivity.class);
                        break;
                    case R.id.nav_menu_monitor:
                        if (mClass != MonitorActivity.class)
                            changeActivity(MonitorActivity.class);
                        break;
                    case R.id.nav_menu_media:
                        if (mClass != MediaActivity.class)
                            changeActivity(MediaActivity.class);
                        break;
                }

                // Menu item点击后选中，并关闭Drawerlayout
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                // android-support-design兼容包中新添加的一个类似Toast的控件。
                //SnackbarUtil.show(mViewPager, msgString, 0);
                return true;
            }
        });
    }

    private void changeActivity(final Class orderClass) {
        new Thread() {
            public void run() {
                //休眠0.256
                try {
                    Thread.sleep(256);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /*Intent newIntent = new Intent(mContext, orderClass);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(newIntent);*/
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        //制定intent要启动的类
                        intent.setClass(mContext, orderClass);
                        //启动一个新的Activity
                        mContext.startActivity(intent);
                        //关闭当前的
                        finish();
                        overridePendingTransition(R.anim.push_left_in_quickly, R.anim.push_left_out_quickly);
                    }
                });
            }
        }.start();
    }

    public void onFloatingactionButtonClick(View v) {
        // 移动数据分析，收集开始听写事件
        FlowerCollector.onEvent(BaseActivity.this, "iat_recognize");
        mIatResults.clear();
        mIatDialog.setParameter(SpeechConstant.ASR_PTT, "0");
        mIatDialog.setListener(mRecognizerDialogListener);
        mIatDialog.show();
    }   //浮动按钮单击事件

    public void onRoundedImageViewClick(View v) {
        Intent intent = new Intent();
        //制定intent要启动的类
        intent.setClass(mContext, LoginActivity.class);
        //启动一个新的Activity
        startActivity(intent);
        overridePendingTransition(R.anim.scale_translate,
                R.anim.my_alpha_action);
    }   //侧边栏图片按钮单击事件

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.bar_menu_msg) {
            Intent intent = new Intent();
            //制定intent要启动的类
            intent.setClass(mContext, FamilyMsgActivity.class);
            //启动一个新的Activity
            mContext.startActivity(intent);
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);
            return true;
        }
        else if (id == R.id.bar_menu_memo) {
            Intent intent = new Intent();
            //制定intent要启动的类
            intent.setClass(mContext, FamilyMemoActivity.class);
            //启动一个新的Activity
            mContext.startActivity(intent);
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);
            return true;
        }
        else if (id == R.id.bar_menu_face) {
            Intent intent = new Intent();
            //制定intent要启动的类
            intent.setClass(mContext, FamilyFaceActivity.class);
            //启动一个新的Activity
            mContext.startActivity(intent);
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);
            return true;
        }
        else if (id == R.id.bar_menu_regist) {
            Intent intent = new Intent();
            //制定intent要启动的类
            intent.setClass(mContext, RegistActivity.class);
            //启动一个新的Activity
            mContext.startActivity(intent);
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);
            return true;
        }
        else if (id == R.id.bar_menu_schedule) {
            Intent intent = new Intent();
            //制定intent要启动的类
            intent.setClass(mContext, ScheduleActivity.class);
            //启动一个新的Activity
            startActivity(intent);
            overridePendingTransition(R.anim.push_left_in,
                R.anim.push_left_out);
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

    protected void onResume() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onResume(BaseActivity.this);
        FlowerCollector.onPageStart(TAG);
        super.onResume();
    }

    protected void onPause() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(BaseActivity.this);
        super.onPause();
    }
}
