package cn.ilell.ihome.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import cn.ilell.ihome.R;
import cn.ilell.ihome.base.BaseFragment;

/**
 * Created by Monkey on 2015/6/29.
 */
public class StateFragment extends BaseFragment{


    private Button btn;
    private TextView text;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frag_state, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.state_swiperefreshlayout);


        // 刷新时，指示器旋转后变化的颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_blue_light, R.color.main_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        initView();
        setListener();
    }

    private void initView() {
        btn = (Button) mView.findViewById(R.id.state_button);
        text = (TextView) mView.findViewById(R.id.state_textView);
    }

    private void setListener() {
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.state_button:

                //do something
                text.setText("123");

                break;

           /* case R.id. myButton2:

                //do something

                break;*/

        }
    }
}
