package cn.ilell.ihome;

import android.os.Bundle;

import cn.ilell.ihome.base.BaseFamilyActivity;

/**
 * Created by lhc35 on 2016/5/6.
 */
public class FamilyMemoActivity extends BaseFamilyActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INIT("家庭备忘录","添加","例如:这周六全家郊游");
        web.loadUrl("http://115.159.127.79/ihome/z-familymemo.php");
        backUrl = "http://115.159.127.79/ihome/backdeal/AddNote.php";
    }
}
