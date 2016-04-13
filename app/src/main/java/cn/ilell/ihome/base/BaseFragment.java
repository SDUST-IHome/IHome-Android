package cn.ilell.ihome.base;

import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import cn.ilell.ihome.adapter.MyRecyclerViewAdapter;
import cn.ilell.ihome.adapter.MyStaggeredViewAdapter;

/**
 * Created by lhc35 on 2016/4/13.
 */
public class BaseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        MyRecyclerViewAdapter.OnItemClickListener, MyStaggeredViewAdapter.OnItemClickListener,
        View.OnClickListener {
    protected View mView;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onRefresh() {

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
