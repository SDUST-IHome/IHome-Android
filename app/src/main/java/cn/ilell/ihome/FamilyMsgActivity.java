package cn.ilell.ihome;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import cn.ilell.ihome.base.BaseFamilyActivity;
import cn.ilell.ihome.utils.HttpXmlClient;

/**
 * Created by lhc35 on 2016/5/6.
 */
public class FamilyMsgActivity extends BaseFamilyActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INIT("家庭留言板","发送留言","例如:今晚开会,晚饭不用等我.");
        web.loadUrl("http://115.159.127.79/ihome/z-familymsg.php#bottom");
        backUrl = "http://115.159.127.79/ihome/backdeal/AddMsg.php";
    }

    public void onFamilyClick(View v) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Content", edit.getText().toString());
        String result = HttpXmlClient.post(backUrl, params);
        Toast.makeText(FamilyMsgActivity.this, result, Toast.LENGTH_SHORT).show();
    }
}
