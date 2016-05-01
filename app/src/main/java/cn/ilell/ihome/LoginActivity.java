package cn.ilell.ihome;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import cn.ilell.ihome.base.BaseSubPageActivity;

/**
 * Created by lhc35 on 2016/5/1.
 */
public class LoginActivity extends BaseSubPageActivity{
    private WebView web;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INIT(R.layout.activity_login,"登陆");
        web = (WebView) findViewById(R.id.webView);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        web.loadUrl("http://115.159.127.79/ihome/backdeal_mobile/CheckLogin.php?Username=admin&Password=123");
        //HttpUtils http = new HttpUtils();
        //Toast.makeText(LoginActivity.this,http.HttpPost("admin","123"), Toast.LENGTH_SHORT).show();
    }
}
