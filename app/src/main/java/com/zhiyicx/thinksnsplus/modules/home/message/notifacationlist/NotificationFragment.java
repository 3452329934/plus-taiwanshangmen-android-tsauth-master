package com.zhiyicx.thinksnsplus.modules.home.message.notifacationlist;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.notify.UserNotifyMsgBean;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

/**
 * @author Catherine
 * @describe 通知列表页
 * @date 2017/8/31
 * @contact email:648129313@qq.com
 */
public class NotificationFragment extends TSListFragment<NotificationContract.Presenter, UserNotifyMsgBean.DataBeanX>
        implements NotificationContract.View, MultiItemTypeAdapter.OnItemClickListener {

    @Inject
    NotificationPresenter mNotificationPresenter;

    public static NotificationFragment instance() {
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.system_notification);
    }

    @Override
    protected boolean isNeedRefreshDataWhenComeIn() {
        return true;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        NotificationAdapter adapter = new NotificationAdapter(getContext(), mListDatas);
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        UserNotifyMsgBean.DataBeanX bean = mListDatas.get(position);
        if (bean.getData() == null) {
            return;
        }
    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }

    @Override
    protected boolean showToolbar() {
        return true;
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (mPresenter != null && isVisibleToUser) {
            mRefreshlayout.autoRefresh();
        }
    }

    @Override
    protected Long getMaxId(@NotNull List<UserNotifyMsgBean.DataBeanX> data) {
        return (long) getPage() + 1;
    }
}
