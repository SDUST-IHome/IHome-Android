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
public class FamilyFaceActivity extends BaseFamilyActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INIT("人脸识别库","添加","例如:冷汉超");
       // web.loadUrl("http://115.159.127.79/ihome/z-familyface.php");
        web.loadUrl("http://115.159.127.79/ihome/z-familyface.php");
        backUrl = "http://115.159.127.79/ihome/backdeal/ManageFace.php";
    }

    public void onFamilyClick(View v) {
        String name = edit.getText().toString();
        if (name.isEmpty())
            Toast.makeText(FamilyFaceActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
        else if (name.length()>10)
            Toast.makeText(FamilyFaceActivity.this, "姓名长度过长", Toast.LENGTH_SHORT).show();
        else {
            Map<String, String> params = new HashMap<String, String>();
            params.put("Name", edit.getText().toString());
            params.put("Type","addP");
            String result = HttpXmlClient.post(backUrl, params);
            Toast.makeText(FamilyFaceActivity.this, result, Toast.LENGTH_SHORT).show();
        }

    }
}
