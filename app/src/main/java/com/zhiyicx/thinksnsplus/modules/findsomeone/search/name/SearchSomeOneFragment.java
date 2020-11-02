package com.zhiyicx.thinksnsplus.modules.findsomeone.search.name;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.widget.edittext.DeleteEditText;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.findsomeone.list.FindSomeOneListAdapter;
import com.zhiyicx.thinksnsplus.modules.personal_center.PersonalCenterFragment;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @author Jungle68
 * @describe 用户搜索界面
 * @date 2017/1/9
 * @contact master.jungle68@gmail.com
 */
public class SearchSomeOneFragment extends TSListFragment<SearchSomeOneContract.Presenter, UserInfoBean> implements SearchSomeOneContract.View,
        MultiItemTypeAdapter.OnItemClickListener {

    public static final String BUNDLE_LOCATION_STRING = "location_string";


    @BindView(R.id.fragment_info_search_edittext)
    DeleteEditText mFragmentInfoSearchEdittext;

    @BindView(R.id.fragment_search_container)
    RelativeLayout mFragmentInfoSearchContainer;
    @BindView(R.id.fragment_search_cancle)
    TextView mTvSearchCancel;
    @BindView(R.id.tv_recomment_user)
    TextView mRecommentUser;
    @BindView(R.id.ll_container)
    View mLlContainer;

    private GridLayoutManager mGridLayoutManager;
    private boolean useGridLayoutManager = true;
    private CommonAdapter<UserInfoBean> mRecommentAdapter;
    private FindSomeOneListAdapter mFindSomeOneListAdapter;

    public static SearchSomeOneFragment newInstance(Bundle args) {

        SearchSomeOneFragment fragment = new SearchSomeOneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_location_search;
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
        return true;
    }

    @Override
    protected View getRightViewOfMusicWindow() {
        return mTvSearchCancel;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        setEmptyViewVisiable(false);
        mRvList.setBackgroundColor(getColor(R.color.white));
        mFragmentInfoSearchContainer.setVisibility(searchContainerVisibility() ? View.VISIBLE : View.GONE);

        mFindSomeOneListAdapter = new FindSomeOneListAdapter(getActivity(), R.layout.item_find_some_list, mListDatas, mPresenter);
        mFindSomeOneListAdapter.setOnItemClickListener(this);

        RxTextView.editorActionEvents(mFragmentInfoSearchEdittext).subscribe(textViewEditorActionEvent -> {
            if (textViewEditorActionEvent.actionId() == EditorInfo.IME_ACTION_SEARCH) {
                if (!TextUtils.isEmpty(getKeyword())) {
                    searchUser();
                } else {
                    getRecommetUser();
                }
            }
        });
        if (searchContainerVisibility()) {
            mLlContainer.setBackgroundResource(R.color.white);
        }
        if (!recommentVisibility()) {
            mRecommentUser.setVisibility(View.GONE);
        }
    }

    protected void getRecommetUser() {
        useGridLayoutManager = true;
        mRvList.setAdapter(mRecommentAdapter);
        mRvList.setLayoutManager(mGridLayoutManager);
        mRvList.setBackgroundColor(getColor(R.color.white));
        mPresenter.getRecommentUser();
    }

    @Override
    protected void initData() {
        super.initData();
        if (getArguments() != null && !TextUtils.isEmpty(getArguments().getString(BUNDLE_LOCATION_STRING))) {
            mFragmentInfoSearchEdittext.setText(getArguments().getString(BUNDLE_LOCATION_STRING));
            mFragmentInfoSearchEdittext.setSelection(getArguments().getString(BUNDLE_LOCATION_STRING).length());
        }
        getRecommetUser();

    }

    @Override
    protected float getItemDecorationSpacing() {
        return useGridLayoutManager ? 0 : super.getItemDecorationSpacing();
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<UserInfoBean> data, boolean isLoadMore) {
        super.onNetResponseSuccess(data, isLoadMore);
        mFindSomeOneListAdapter.notifyDataSetChanged();
        if (useGridLayoutManager && recommentVisibility()) {
            mRecommentUser.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @OnClick({R.id.fragment_search_back, R.id.fragment_search_cancle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_search_back:
                getActivity().finish();
                break;
            case R.id.fragment_search_cancle:
                getActivity().finish();
                break;
            default:
        }
    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager() {
        mGridLayoutManager = new GridLayoutManager(mActivity, 2);
        return mGridLayoutManager;
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        mRecommentAdapter = new CommonAdapter<UserInfoBean>(mActivity, R.layout.item_find_some_recomment_list, mListDatas) {
            @Override
            protected void convert(ViewHolder holder, UserInfoBean userInfoBean, int position) {
                // 头像加载
                ImageUtils.loadCircleUserHeadPic(userInfoBean, holder.getView(R.id.iv_headpic));
                // 添加点击事件
                RxView.clicks(holder.getConvertView())
                        .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                        .subscribe(aVoid -> toUserCenter(getContext(), userInfoBean));

                // 设置用户名，用户简介
                holder.setText(R.id.tv_name, userInfoBean.getName());
                String followingCount = String.valueOf(userInfoBean.getExtra().getFollowings_count());
                holder.setText(R.id.tv_user_signature, getString(R.string.fans_count, ConvertUtils.numberConvert(Integer.parseInt(followingCount))));
            }
        };
        return mRecommentAdapter;
    }

    @Override
    protected int setEmptView() {
        return R.mipmap.img_default_search;
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
//
//        UserInfoBean bean = mListDatas.get(position);
//        Intent intent = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(BUNDLE_DATA, bean);
//        intent.putExtras(bundle);
//        getActivity().setResult(RESULT_OK, intent);
//        getActivity().finish();
    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }

    @Override
    public void upDateFollowFansState(int index) {
        mFindSomeOneListAdapter.notifyItemChanged(index);
    }

    @Override
    protected Long getMaxId(@NotNull List<UserInfoBean> data) {
        return (long) mListDatas.size();
    }

    private void toUserCenter(Context context, UserInfoBean userInfoBean) {
        if (mPresenter.handleTouristControl()) {
            return;
        }
        PersonalCenterFragment.startToPersonalCenter(context, userInfoBean);
    }

    protected void searchUser() {
        mPresenter.searchUser(getKeyword());
        DeviceUtils.hideSoftKeyboard(getContext(), mFragmentInfoSearchEdittext);
        useGridLayoutManager = false;
        mRvList.setAdapter(mFindSomeOneListAdapter);
        mRvList.setLayoutManager(new LinearLayoutManager(mActivity));
        mRvList.setBackgroundColor(getColor(R.color.bgColor));
        mRecommentUser.setVisibility(View.GONE);
    }

    protected String getKeyword() {
        return mFragmentInfoSearchEdittext.getText().toString().trim();
    }

    protected boolean searchContainerVisibility() {
        return true;
    }

    protected boolean recommentVisibility() {
        return true;
    }
}
