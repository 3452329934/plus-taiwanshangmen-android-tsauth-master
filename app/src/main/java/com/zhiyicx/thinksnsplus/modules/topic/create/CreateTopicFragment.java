package com.zhiyicx.thinksnsplus.modules.topic.create;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trycatch.mysnackbar.Prompt;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.impl.photoselector.DaggerPhotoSelectorImplComponent;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSelectorImpl;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSeletorImplModule;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.UIUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.TopicDetailBean;
import com.zhiyicx.thinksnsplus.modules.topic.detail.TopicDetailActivity;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicFragment;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/07/24/10:31
 * @Email Jliuer@aliyun.com
 * @Description 创建 or 修改话题
 */
public class CreateTopicFragment extends TSFragment<CreateTopicContract.Presenter> implements CreateTopicContract.View
        , PhotoSelectorImpl.IPhotoBackListener {

    public static final int CHANGETOPIC = 1000;

    /**
     * 封面上面的文字
     */
    @BindView(R.id.tv_topic_cover)
    TextView mTvTopicCover;

    /**
     * 封面
     */
    @BindView(R.id.iv_topic_cover)
    ImageView mIvTopicCover;
    @BindView(R.id.v_transparent)
    View mVTransparent;

    @BindView(R.id.fl_topic_cover_container)
    FrameLayout mFlTopicCoverContainer;

    /**
     * 标题
     */
    @BindView(R.id.et_topic_title)
    EditText mEtTopicTitle;

    /**
     * 简介
     */
    @BindView(R.id.et_topic_desc)
    EditText mEtTopicDesc;

    @BindView(R.id.tv_warning)
    TextView mTvWarning;

    @BindView(R.id.tv_limit_tip)
    TextView mTvLimitLip;

    @BindView(R.id.ol_scroll)
    View mDvViewGroup;

    private PhotoSelectorImpl mPhotoSelector;

    /**
     * 图片选择弹框
     */
    private ActionPopupWindow mPhotoPopupWindow;

    /**
     * 退出提示弹框
     */
    private ActionPopupWindow mWaringPopupWindow;

    private TopicDetailBean mTopicDetailBean;

    /**
     * 创建话题成功之后返回的 id
     */
    private Long mCreateTopicId;

    private Subscription mGlobalLayoutsSubscription;

    public static CreateTopicFragment newInstance(Bundle args) {
        CreateTopicFragment fragment = new CreateTopicFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_create_topic;
    }

    @Override
    protected boolean usePermisson() {
        return true;
    }

    @Override
    protected String setRightTitle() {
        return getString(R.string.create);
    }

    @Override
    protected String setLeftTitle() {
        return getString(R.string.cancel);
    }

    @Override
    protected int getLeftTextColor() {
        return R.color.themeColor;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.create_feed_topic);
    }

    @Override
    protected int setLeftImg() {
        return 0;
    }

    @Override
    protected void setLeftClick() {
        onBackPressed();
    }

    @Override
    protected void setRightClick() {
        mPresenter.createOrModifyTopic(mEtTopicTitle.getText().toString(), mEtTopicDesc.getText().toString(), (String) mTvTopicCover.getTag());
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mEtTopicTitle.getText()) && TextUtils.isEmpty(mEtTopicDesc.getText()) && mTvTopicCover.getTag() == null) {
            mActivity.finish();
            return;
        }
        initWaringPopupWindow();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPhotoSelector.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void initView(View rootView) {
        mPhotoSelector = DaggerPhotoSelectorImplComponent
                .builder()
                .photoSeletorImplModule(new PhotoSeletorImplModule(this, this, PhotoSelectorImpl
                        .SHAPE_RCTANGLE))
                .build().photoSelectorImpl();
    }

    @Override
    public TopicDetailBean getModifyTopic() {
        return mTopicDetailBean;
    }

    @Override
    protected void initData() {
        String html = "<font color='#f4504d'>* </font><font color='#cccccc'>话题创建成功后，标题不可更改</font>";
        mTvWarning.setText(Html.fromHtml(html));
        mToolbarRight.setEnabled(false);
        mGlobalLayoutsSubscription = RxView.globalLayouts(mDvViewGroup)
                .flatMap(aVoid -> {
                    if (mDvViewGroup == null) {
                        return Observable.just(false);
                    }
                    Rect rect = new Rect();
                    //获取root在窗体的可视区域
                    mDvViewGroup.getWindowVisibleDisplayFrame(rect);
                    //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                    int rootInvisibleHeight = mDvViewGroup.getRootView().getHeight() - rect.bottom;
                    int dispayHeight = UIUtils.getWindowHeight(getContext());
                    return Observable.just(rootInvisibleHeight > (dispayHeight * (1f / 3)));
                })
                // 监听键盘弹起隐藏状态时，会多次调用globalLayouts方法，为了避免多个数据导致状态判断出错，只取200ms内最后一次数据
                .debounce(getResources().getInteger(android.R.integer.config_mediumAnimTime), TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    //若不可视区域高度大于1/3屏幕高度，则键盘显示
                    if (!aBoolean && mEtTopicDesc != null && mEtTopicTitle != null) {
                        //键盘隐藏,清除焦点
                        if (mEtTopicDesc.hasFocus()) {
                            mEtTopicDesc.clearFocus();
                        }
                        if (mEtTopicTitle != null && mEtTopicTitle.hasFocus()) {
                            mEtTopicTitle.clearFocus();
                        }
                    }
                });

        RxTextView.textChanges(mEtTopicTitle).subscribe(charSequence -> mToolbarRight.setEnabled(!TextUtils.isEmpty(charSequence)));
        if (getArguments() != null) {

            mTopicDetailBean = getArguments().getParcelable(SearchTopicFragment.TOPIC);
            if (mTopicDetailBean != null) {
                restoreData(mTopicDetailBean);
            }
        }

        RxTextView.textChanges(mEtTopicDesc)
                .subscribe(charSequence -> {
                    mTvLimitLip.setText(charSequence.length() + "/50");
                    if (getModifyTopic() != null) {
                        mToolbarRight.setEnabled(!TextUtils.isEmpty(charSequence));
                    }
                });
    }

    @Override
    public void getPhotoSuccess(List<ImageBean> photoList) {
        if (photoList.isEmpty()) {
            return;
        }
        mTvTopicCover.setTag(photoList.get(0).getImgUrl());
        Glide.with(mActivity)
                .load(photoList.get(0).getImgUrl())
                .into(mIvTopicCover);
        if (mVTransparent.getVisibility() == View.GONE) {
            mVTransparent.setVisibility(View.INVISIBLE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTvTopicCover.getLayoutParams();
            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            params.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.spacing_mid);
            params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.spacing_large);
            mTvTopicCover.setText(R.string.change_topic_pic_hint);
            mTvTopicCover.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ico_topic_camera, 0, 0, 0);
        }
        if (getModifyTopic() != null) {
            mToolbarRight.setEnabled(true);
        }
    }

    @Override
    public void getPhotoFailure(String errorMsg) {

    }

    @OnClick({R.id.v_transparent, R.id.fl_topic_cover_container})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.v_transparent:
            case R.id.fl_topic_cover_container:
                DeviceUtils.hideSoftKeyboard(getContext().getApplicationContext(), mEtTopicDesc);
                initPhotoPopupWindow();
                mPhotoPopupWindow.show();
                break;
            default:
        }
    }

    /**
     * 初始化图片选择弹框
     */
    private void initPhotoPopupWindow() {
        if (mPhotoPopupWindow != null) {
            return;
        }
        mPhotoPopupWindow = ActionPopupWindow.builder()
                .item1Str(mActivity.getString(R.string.choose_from_photo))
                .item2Str(mActivity.getString(R.string.choose_from_camera))
                .bottomStr(mActivity.getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(0.8f)
                .with(mActivity)
                .item1ClickListener(() -> {
                    // 选择相册，单张
                    mPhotoSelector.getPhotoListFromSelector(1, null);
                    mPhotoPopupWindow.hide();
                })
                .item2ClickListener(() -> {
                    // 选择相机，拍照
                    mPhotoSelector.getPhotoFromCamera(null);
                    mPhotoPopupWindow.hide();
                })
                .bottomClickListener(() -> mPhotoPopupWindow.hide()).build();
    }

    public void initWaringPopupWindow() {
        DeviceUtils.hideSoftKeyboard(mActivity, mRootView.findFocus());
        if (mWaringPopupWindow != null) {
            mWaringPopupWindow.show();
            return;
        }
        mWaringPopupWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.info_publish_hint))
                .desStr(getString(getModifyTopic() == null ? R.string.create_topic_cancle_tip : R.string.edit_topic_cancle_tip))
                .item2Str(getString(getModifyTopic() == null ? R.string.create_topic_cancle : R.string.edit_topic_cancle))
                .item3Str(getString(R.string.create_topic_go_on))
                .item2ClickListener(() -> {
                    mActivity.finish();
                    mWaringPopupWindow.dismiss();
                })
                .item3ClickListener(() -> mWaringPopupWindow.dismiss())
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .with(getActivity())
                .bottomClickListener(() -> mWaringPopupWindow.hide())
                .build();
        mWaringPopupWindow.show();
    }

    private void restoreData(TopicDetailBean topic) {
        // 话题头像
        String coverPath = TextUtils.isEmpty(topic.getLogoUrl())
                ? topic.getLogo() != null ? topic.getLogo().getUrl() : "" : topic.getLogoUrl();
        Glide.with(mActivity)
                .load(coverPath)
                .into(mIvTopicCover);
        if (mVTransparent.getVisibility() == View.GONE) {
            mVTransparent.setVisibility(View.INVISIBLE);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTvTopicCover.getLayoutParams();
            params.gravity = Gravity.BOTTOM | Gravity.LEFT;
            params.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.spacing_mid);
            params.leftMargin = getResources().getDimensionPixelOffset(R.dimen.spacing_large);
            mTvTopicCover.setText(R.string.change_topic_pic_hint);
            mTvTopicCover.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ico_identi_camera, 0, 0, 0);
        }
        mToolbarRight.setText(R.string.save);
        mToolbarLeft.setText("");
        setCenterText(getString(R.string.edit_feed_topic));
        setToolBarLeftImage(R.mipmap.topbar_back);
        mEtTopicTitle.setHint(topic.getName());
        mEtTopicTitle.setEnabled(false);
        mEtTopicDesc.setText(topic.getDesc());
        mTvLimitLip.setText(topic.getDesc().length() + "/50");
    }

    @Override
    protected void snackViewDismissWhenTimeOut(Prompt prompt) {
        super.snackViewDismissWhenTimeOut(prompt);
        if (prompt == Prompt.SUCCESS) {
            if (getModifyTopic() != null) {
                getModifyTopic().setDesc(mEtTopicDesc.getText().toString());
                getModifyTopic().setLogoUrl((String) mTvTopicCover.getTag());
                Intent intent = new Intent();
                Bundle data = new Bundle();
                data.putParcelable(SearchTopicFragment.TOPIC, getModifyTopic());
                intent.putExtras(data);
                mActivity.setResult(Activity.RESULT_OK, intent);
                mActivity.finish();
                return;
            }
            TopicDetailActivity.startTopicDetailActivity(mActivity, mCreateTopicId);
            mActivity.finish();
        } else if (prompt == Prompt.DONE) {
            mActivity.finish();
        }
    }

    @Override
    public void setTopicId(Long id) {
        mCreateTopicId = id;
    }

    @Override
    public void onDestroyView() {
        if (mGlobalLayoutsSubscription != null && !mGlobalLayoutsSubscription.isUnsubscribed()) {
            mGlobalLayoutsSubscription.unsubscribe();
        }
        dismissPop(mWaringPopupWindow);
        dismissPop(mPhotoPopupWindow);
        super.onDestroyView();
    }
}
