package com.zhiyicx.thinksnsplus.modules.aaaat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.widget.edittext.DeleteEditText;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.findsomeone.list.FindSomeOneListAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.functions.Action1;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/08/13/10:54
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class AtUserListFragment extends TSListFragment<AtUserContract.Presenter, UserInfoBean> implements AtUserContract.View {

    public static final int REQUES_USER = 3000;
    public static final String AT_USER = "at_user";

    @BindView(R.id.fragment_search_back)
    ImageView mFragmentSearchBack;
    @BindView(R.id.fragment_info_search_edittext)
    DeleteEditText mFragmentInfoSearchEdittext;
    @BindView(R.id.fragment_search_container)
    RelativeLayout mFragmentSearchContainer;
    @BindView(R.id.stub_toptip)
    ViewStub mStubToptip;
    @BindView(R.id.swipe_target)
    RecyclerView mSwipeTarget;
    @BindView(R.id.refreshlayout)
    SmartRefreshLayout mRefreshlayout;
    @BindView(R.id.stub_empty_view)
    ViewStub mStubEmptyView;
    @BindView(R.id.ll_container)
    LinearLayout mLlContainer;

    private String keyWord;
    private boolean refreshExtraData;

    @Override
    protected View getRightViewOfMusicWindow() {
        return mFragmentSearchContainer;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    public void onBackPressed() {
        mActivity.setResult(Activity.RESULT_OK);
        mActivity.finish();
    }

    public static AtUserListFragment newInstance() {
        Bundle args = new Bundle();
        AtUserListFragment fragment = new AtUserListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return new FindSomeOneListAdapter(getContext(), R.layout.item_find_some_list, mListDatas, null) {
            @Override
            protected void convert(ViewHolder holder, UserInfoBean userInfoBean, int position) {
                super.convert(holder, userInfoBean, position);
                holder.setVisible(R.id.iv_user_follow, View.GONE);
            }

            @Override
            protected void toUserCenter(Context context, UserInfoBean userInfoBean) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelable(AT_USER, userInfoBean);
                intent.putExtras(bundle);
                mActivity.setResult(Activity.RESULT_OK, intent);
                mActivity.finish();
            }
        };
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        RxTextView.editorActionEvents(mFragmentInfoSearchEdittext).subscribe(textViewEditorActionEvent -> {
            if (textViewEditorActionEvent.actionId() == EditorInfo.IME_ACTION_SEARCH) {
                keyWord = mFragmentInfoSearchEdittext.getText().toString();
                if (TextUtils.isEmpty(keyWord)) {
                    refreshExtraData(false);
                }
                mPresenter.requestNetData(0L, false);
            }
        });
        RxView.clicks(mFragmentSearchBack)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> onBackPressed());
    }

    @Override
    protected int setEmptView() {
        return TextUtils.isEmpty(keyWord) ? R.mipmap.img_default_nobody : R.mipmap.img_default_search;
    }

    @Override
    public boolean refreshExtraData() {
        return refreshExtraData;
    }

    @Override
    public void refreshExtraData(boolean refresh) {
        refreshExtraData = refresh;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_at_userlist;
    }

    @Override
    public String getKeyWord() {
        return keyWord;
    }

    @Override
    protected Long getMaxId(@NotNull List<UserInfoBean> data) {
        return (long) getOffset();
    }
}
