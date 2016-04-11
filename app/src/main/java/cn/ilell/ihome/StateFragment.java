package cn.ilell.ihome;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.ilell.ihome.adapter.MyRecyclerViewAdapter;
import cn.ilell.ihome.adapter.MyStaggeredViewAdapter;

/**
 * Created by Monkey on 2015/6/29.
 */
public class StateFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MyRecyclerViewAdapter.OnItemClickListener, MyStaggeredViewAdapter.OnItemClickListener {

    private View mView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.frag_main, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.state_swiperefreshlayout);


        // 刷新时，指示器旋转后变化的颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.main_blue_light, R.color.main_blue_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

    }


    @Override
    public void onRefresh() {

        // 刷新时模拟数据的变化
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1000);
    }

    @Override
    public void onItemClick(View view, int position) {
        //SnackbarUtil.show(mRecyclerView, getString(R.string.item_clicked), 0);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        //SnackbarUtil.show(mRecyclerView, getString(R.string.item_longclicked), 0);
    }
}
