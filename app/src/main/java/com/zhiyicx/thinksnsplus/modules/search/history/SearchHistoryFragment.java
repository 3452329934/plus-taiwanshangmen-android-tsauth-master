package com.zhiyicx.thinksnsplus.modules.search.history;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.SearchHistoryBean;
import com.zhiyicx.thinksnsplus.modules.q_a.search.list.IHistoryCententClickListener;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/06/10:42
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchHistoryFragment extends TSListFragment<SearchHistoryContract.Presenter, SearchHistoryBean> implements SearchHistoryContract.View {

    @Inject
    SearchHistoryPresenter mSearchHistoryPresenter;

    private IHistoryCententClickListener mIHistoryCententClickListener;

    public void setIHistoryCententClickListener(IHistoryCententClickListener ihistorycententclicklistener) {
        mIHistoryCententClickListener = ihistorycententclicklistener;
    }

    /**
     * 历史记录 6 条显示清除
     */
    private static final int SIZE_SHOW_DELETE = 6;

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected boolean isRefreshEnable() {
        return false;
    }

    @Override
    protected boolean isLoadingMoreEnable() {
        return false;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        MultiItemTypeAdapter adapter = new MultiItemTypeAdapter<>(mActivity, mListDatas);
        adapter.addItemViewDelegate(new ItemViewDelegate<SearchHistoryBean>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_search_history;
            }

            @Override
            public boolean isForViewType(SearchHistoryBean item, int position) {
                return SearchHistoryBean.TYPE_DEFAULT != item.getType();
            }

            @Override
            public void convert(ViewHolder holder, SearchHistoryBean searchHistoryBean, SearchHistoryBean lastT, int position, int itemCounts) {
                holder.setText(R.id.tv_content, searchHistoryBean.getContent());
                RxView.clicks(holder.getView(R.id.tv_content))
                        .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                        .subscribe(aVoid -> {
                            if (mIHistoryCententClickListener != null) {
                                mIHistoryCententClickListener.onContentClick(searchHistoryBean.getContent());
                            }

                        });
                RxView.clicks(holder.getView(R.id.iv_delete))
                        .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                        .subscribe(aVoid -> {
                            mPresenter.deleteSearchHistory(searchHistoryBean.getContent());
                            mListDatas.remove(position);
                            if (mListDatas.size() > 0 && mListDatas.get(mListDatas.size() - 1).getType() == SearchHistoryBean.TYPE_DEFAULT) {
                                mListDatas.remove(mListDatas.size() - 1);
                            }
                            refreshData();
                        });
            }
        });
        adapter.addItemViewDelegate(new ItemViewDelegate<SearchHistoryBean>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_search_history_control;
            }

            @Override
            public boolean isForViewType(SearchHistoryBean item, int position) {
                return SearchHistoryBean.TYPE_DEFAULT == item.getType();
            }

            @Override
            public void convert(ViewHolder holder, SearchHistoryBean searchHistoryBean, SearchHistoryBean lastT, int position, int itemCounts) {
                holder.setText(R.id.tv_content, searchHistoryBean.getContent());
                RxView.clicks(holder.itemView)
                        .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                        .subscribe(aVoid -> mPresenter.deleteHistory());
            }

        });
        return adapter;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        Observable.create(subscriber -> {
            DaggerSearchHistoryComponent
                    .builder()
                    .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                    .searchHistoryPresenterModule(new SearchHistoryPresenterModule(this))
                    .build()
                    .inject(this);
            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        initData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Object o) {
                    }
                });
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<SearchHistoryBean> data, boolean isLoadMore) {
        if (data.size() == SIZE_SHOW_DELETE) {
            SearchHistoryBean delete = new SearchHistoryBean(getString(R.string.clear_all_history));
            delete.setType(SearchHistoryBean.TYPE_DEFAULT);
            data.add(delete);
        }
        super.onNetResponseSuccess(data, isLoadMore);
    }
}
