package cn.ilell.ihome.base;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import cn.ilell.ihome.R;
import cn.ilell.ihome.adapter.MyRecyclerViewAdapter;
import cn.ilell.ihome.adapter.MyStaggeredViewAdapter;

/**
 * Created by lhc35 on 2016/4/13.
 */
public class BaseFragment extends Fragment implements
        MyRecyclerViewAdapter.OnItemClickListener, MyStaggeredViewAdapter.OnItemClickListener,
        View.OnClickListener {
    protected View mView;
    protected Context mContext;

    protected WebView web;


    protected void initView() {
        web = (WebView) mView.findViewById(R.id.webView);
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
    }
    @Override
    public void onItemClick(View view, int position) {
        //SnackbarUtil.show(mRecyclerView, getString(R.string.item_clicked), 0);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        //SnackbarUtil.show(mRecyclerView, getString(R.string.item_longclicked), 0);
    }

    @Override
    public void onClick(View v) {

    }
}
