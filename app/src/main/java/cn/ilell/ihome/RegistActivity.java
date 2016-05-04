package cn.ilell.ihome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashMap;
import java.util.Map;

import cn.ilell.ihome.base.BaseSubPageActivity;
import cn.ilell.ihome.utils.HttpXmlClient;

/**
 * Created by lhc35 on 2016/5/1.
 */
public class RegistActivity extends BaseSubPageActivity {
    private HttpClient http;
    private Button btn_regedit;
    private EditText text_user;
    private EditText text_name;
    private EditText text_phone;
    private EditText text_pwd;
    private EditText text_pwdenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INIT(R.layout.activity_regist,"新用户注册");

        initViewAndListener();
        //HttpUtils http = new HttpUtils();
        //Toast.makeText(LoginActivity.this,http.HttpPost("admin","123"), Toast.LENGTH_SHORT).show();
    }

    private void initViewAndListener() {
        http = new DefaultHttpClient();

        btn_regedit = (Button) findViewById(R.id.regist_btn_regist);
        text_user = (EditText) findViewById(R.id.regist_text_user);
        text_name = (EditText) findViewById(R.id.regist_text_name);
        text_phone = (EditText) findViewById(R.id.regist_text_phone);
        text_pwd = (EditText) findViewById(R.id.regist_text_pwd);
        text_pwdenter = (EditText) findViewById(R.id.regist_text_pwdenter);

        btn_regedit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // FloatingActionButton的点击事件
            case R.id.regist_btn_regist:
                String username,pwd,name,phonenum;
                username = text_user.getText().toString();
                pwd = text_pwd.getText().toString();
                name = text_name.getText().toString();
                phonenum = text_phone.getText().toString();

                Map<String, String> params = new HashMap<String, String>();
                params.put("Username", username);
                params.put("Password", pwd);
                params.put("Name", name);
                params.put("Phonenum", phonenum);

                String xml = HttpXmlClient.post("http://115.159.127.79/ihome/backdeal/SaveNewUser.php", params);
                Toast.makeText(RegistActivity.this, xml, Toast.LENGTH_SHORT).show();
                if (xml.equals("注册成功")) {
                    SharedPreferences settings = getSharedPreferences("IHomeAccount", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("user",username);
                    editor.putString("password",pwd);
                    editor.commit();

                    Intent intent = new Intent();
                    ///制定intent要启动的类
                    intent.setClass(RegistActivity.this, LoginActivity.class);
                    //启动一个新的Activity
                    startActivity(intent);
                    finish();
                }
                break;

        }
    }
}
