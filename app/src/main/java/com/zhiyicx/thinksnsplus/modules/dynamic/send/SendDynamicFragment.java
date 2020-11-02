package com.zhiyicx.thinksnsplus.modules.dynamic.send;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxRadioGroup;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.zhiyi.emoji.EmojiKeyboard;
import com.zhiyi.emoji.EomjiSource;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.impl.photoselector.DaggerPhotoSelectorImplComponent;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSelectorImpl;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSeletorImplModule;
import com.zhiyicx.baseproject.impl.photoselector.Toll;
import com.zhiyicx.baseproject.widget.button.CombinationButton;
import com.zhiyicx.baseproject.widget.imageview.FilterImageView;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.baseproject.widget.textview.SpanTextViewWithEllipsize;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.common.utils.ActivityHandler;
import com.zhiyicx.common.utils.AndroidBug5497Workaround;
import com.zhiyicx.common.utils.ColorPhrase;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DrawableProvider;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.common.utils.SharePreferenceUtils;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.common.utils.ToastUtils;
import com.zhiyicx.common.utils.UIUtils;
import com.zhiyicx.common.utils.recycleviewdecoration.GridDecoration;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.imsdk.utils.common.DeviceUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.AnimationRectBean;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.GroupDynamicListBean;
import com.zhiyicx.thinksnsplus.data.beans.GroupSendDynamicDataBean;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.modules.aaaat.AtUserActivity;
import com.zhiyicx.thinksnsplus.modules.aaaat.AtUserListFragment;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.picture_toll.PictureTollActivity;
import com.zhiyicx.thinksnsplus.modules.home.HomeActivity;
import com.zhiyicx.thinksnsplus.modules.photopicker.PhotoAlbumDetailsActivity;
import com.zhiyicx.thinksnsplus.modules.photopicker.PhotoViewActivity;
import com.zhiyicx.thinksnsplus.modules.photopicker.PhotoViewFragment;
import com.zhiyicx.thinksnsplus.modules.shortvideo.cover.CoverActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.cover.CoverFragment;
import com.zhiyicx.thinksnsplus.modules.shortvideo.videostore.VideoSelectActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.videostore.VideoSelectFragment;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicActivity;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicFragment;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhiyicx.thinksnsplus.widget.IconTextView;
import com.zhiyicx.thinksnsplus.widget.UserInfoInroduceInputView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;
import com.zycx.shortvideo.media.VideoInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.zhiyicx.baseproject.impl.photoselector.PhotoSelectorImpl.TOLLBUNDLE;
import static com.zhiyicx.baseproject.impl.photoselector.PhotoSelectorImpl.TOLL_TYPE;
import static com.zhiyicx.baseproject.impl.photoselector.Toll.DOWNLOAD_TOLL_TYPE;
import static com.zhiyicx.baseproject.impl.photoselector.Toll.LOOK_TOLL;
import static com.zhiyicx.baseproject.impl.photoselector.Toll.LOOK_TOLL_TYPE;
import static com.zhiyicx.common.utils.DeviceUtils.isKeyboardShown;
import static com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean.TEXT_ONLY_DYNAMIC;
import static com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean.VIDEO_TEXT_DYNAMIC;


/**
 * @author LiuChao
 * @describe
 * @date 2017/2/20
 * @contact email:450127106@qq.com
 * @See
 */
public class SendDynamicFragment extends TSFragment<SendDynamicContract.Presenter> implements
        SendDynamicContract.View, PhotoSelectorImpl.IPhotoBackListener {

    /**
     * recyclerView的每行item个数
     */
    private static final int ITEM_COLUM = 4;

    /**
     * 一共可选的图片数量
     */
    public static final int MAX_PHOTOS = 9;

    /**
     * 一共可选的话题数量
     */
    public static final int MAX_TOPICS = 5;

    @BindView(R.id.view_emoji)
    protected View mViewEmoji;
    @BindView(R.id.emojiview)
    protected EmojiKeyboard mEmojiKeyboard;
    @BindView(R.id.rv_photo_list)
    protected RecyclerView mRvPhotoList;
    @BindView(R.id.rv_topic_list)
    protected TagFlowLayout mRvTopicList;
    @BindView(R.id.line_toipic_bottom)
    protected View mLineTopicBottom;

    @BindView(R.id.et_dynamic_title)
    protected UserInfoInroduceInputView mEtDynamicTitle;
    @BindView(R.id.et_dynamic_content)
    protected UserInfoInroduceInputView mEtDynamicContent;
    @BindView(R.id.tv_toll)
    protected CombinationButton mTvToll;
    @BindView(R.id.send_dynamic_ll_toll)
    protected LinearLayout mLLToll;
    @BindView(R.id.ll_send_dynamic)
    protected LinearLayout mLlSendDynamic;
    @BindView(R.id.tv_choose_tip)
    protected TextView mTvChooseTip;
    @BindView(R.id.tv_word_limit)
    protected TextView mTvWordsLimit;
    @BindView(R.id.rb_one)
    protected RadioButton mRbOne;
    @BindView(R.id.rb_two)
    protected RadioButton mRbTwo;
    @BindView(R.id.rb_three)
    protected RadioButton mRbThree;
    @BindView(R.id.rb_days_group)
    protected RadioGroup mRbDaysGroup;
    @BindView(R.id.et_input)
    protected EditText mEtInput;
    @BindView(R.id.sl_send_dynamic)
    protected NestedScrollView mSlSendDynamic;
    @BindView(R.id.v_line_toll)
    protected View mTollLine;
    @BindView(R.id.v_horizontal_line)
    protected View mTitleUnderLine;
    @BindView(R.id.tv_custom_money)
    protected TextView mCustomMoney;
    @BindView(R.id.tv_add_topic)
    protected CombinationButton mTvAddTopic;
    @BindView(R.id.tv_at_user)
    protected CombinationButton mTvAtUser;
    /**
     * 转发内容
     */
    @BindView(R.id.fl_froward_content)
    protected FrameLayout mFlForwardContainer;

    /**
     * 已经选择的图片
     */
    private List<ImageBean> mSelectedPhotos;

    /**
     * 已经选择的话题
     */
    private List<TopicListBean> mSelectedTopics;

    /**
     * 图片
     */
    private CommonAdapter<ImageBean> mCommonAdapter;

    /**
     * 话题
     */
    private TagAdapter<TopicListBean> mTopicAdapter;

    /**
     * 上一次的图片，用于比对返回的图片列表是否有变动
     */
    private List<ImageBean> mCachePhotos;

    /**
     * 图片选择弹框
     */
    private ActionPopupWindow mPhotoPopupWindow;
    private ActionPopupWindow mCanclePopupWindow;

    private PhotoSelectorImpl mPhotoSelector;

    /**
     * 状态值用来判断发送状态
     */
    private boolean hasContent;

    /**
     * 需要发送的动态类型
     */
    private int dynamicType;

    /**
     * 是否开启收费
     */
    private boolean isToll;

    private boolean isFromGroup;

    /**
     * 是否有图片设置了收费
     */
    private boolean hasTollPic;

    /**
     * 文字收费选择
     */
    private ArrayList<Float> mSelectMoney;

    /**
     * 文字收费金额
     */
    private double mTollMoney;

    /**
     * 各类提示信息弹窗
     */
    private ActionPopupWindow mInstructionsPopupWindow;

    private SendDynamicDataBean mSendDynamicDataBean;

    private Letter mLetter;

    private int mCurrentTollImageBean;

    /**
     * 默认选中的话题
     */
    private TopicListBean mTopicBean;

    /**
     * 内容，记录用来比较，判断文字变化是新增还是删除
     */
    private int mContentChar;

    public static SendDynamicFragment initFragment(Bundle bundle) {
        SendDynamicFragment sendDynamicFragment = new SendDynamicFragment();
        sendDynamicFragment.setArguments(bundle);
        return sendDynamicFragment;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_send_dynamic;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.send_dynamic);
    }

    @Override
    protected String setLeftTitle() {
        return getString(R.string.cancel);
    }

    @Override
    protected int setLeftImg() {
        return 0;
    }

    @Override
    protected String setRightTitle() {
        return getString(R.string.publish);
    }

    @Override
    protected void setLeftClick() {
        handleBack();
    }

    @Override
    public void onBackPressed() {
        handleBack();
    }

    @Override
    protected boolean usePermisson() {
        return true;
    }

    /**
     * 处理取消发布动态
     */
    private void handleBack() {
        boolean noPic = mSelectedPhotos == null || !isToll && mSelectedPhotos.size() <= 1;
        if (hasContent || !noPic) {
            DeviceUtils.hideSoftKeyboard(getContext(), mEtDynamicContent);
            initCanclePopupWindow();
            mCanclePopupWindow.show();
        } else {
            super.setLeftClick();
        }
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        mTvToll.setLeftTextSize(getResources().getInteger(R.integer.dynamic_toll_tip_text_size));
        mTvAddTopic.setLeftTextSize(getResources().getInteger(R.integer.dynamic_toll_tip_text_size));
        mTvAtUser.setLeftTextSize(getResources().getInteger(R.integer.dynamic_toll_tip_text_size));
        handleClipboardContent();
        // 设置右边发布文字监听
        initSendDynamicBtnState();
        // 设置左边取消文字的颜色为主题色
        setLeftTextColor();
        initDynamicType();
        setSendDynamicState();
        initWordsToll();
        // 配置收费按钮隐藏显示
        initTollState();
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            AndroidBug5497Workaround.assistActivity(getActivity());
        }


        RxTextView.textChanges(mEtDynamicContent.getEtContent())
                .subscribe(charSequence -> {
                    boolean isDelete = mContentChar > charSequence.length();
                    mContentChar = charSequence.length();
                    int atIndex = mEtDynamicContent.getEtContent().getSelectionStart() - 1;
                    if (atIndex < 0) {
                        return;
                    }
                    boolean isAt = charSequence.length() >= 1 && MarkdownConfig.AT.equals(String.valueOf(charSequence.charAt(atIndex)));
                    if (!isDelete && isAt) {
                        AtUserActivity.startAtUserActivity(mActivity);
                    }
                });
        mTvAtUser.setOnClickListener(view -> AtUserActivity.startAtUserActivity(mActivity));
        mEmojiKeyboard.setEditText(mEtDynamicContent.getEtContent());
        mEmojiKeyboard.setMaxLines(5);
        mEmojiKeyboard.setMaxColumns(8);
        mEmojiKeyboard.setEmojiLists(EomjiSource.emojiListMap("emoji.json", getContext()));
        mEmojiKeyboard.setIndicatorPadding(3);
        mViewEmoji.setOnClickListener(view -> {
            if (mEmojiKeyboard.getVisibility() == View.GONE) {
                DeviceUtils.hideSoftKeyboard(getContext(), mEtDynamicContent);
                mEmojiKeyboard.postDelayed(() -> {
                    mEmojiKeyboard.init();
                    mEmojiKeyboard.setVisibility(View.VISIBLE);
                },200);
            } else {
                mEmojiKeyboard.setVisibility(View.GONE);
                DeviceUtils.showSoftKeyboard(getContext(), mEtDynamicContent.getEtContent());
            }
        });
        mEtDynamicContent.getEtContent().setOnClickListener(view -> {
            mEmojiKeyboard.setVisibility(View.GONE);
            DeviceUtils.showSoftKeyboard(getContext(), mEtDynamicContent);
        });

        RxView.globalLayouts(mActivity.getWindow().getDecorView())
                .subscribe(aVoid -> {
                    if (mActivity != null) {
                        boolean isKeyboardShown = isKeyboardShown(mActivity.getWindow().getDecorView());
                        mViewEmoji.setVisibility((isKeyboardShown || mEmojiKeyboard.getVisibility() == View.VISIBLE) ? View.VISIBLE : View.GONE);
                    }
                });
    }

    private void initTollState() {
        boolean canPay = mPresenter.getSystemConfigBean().getFeed().hasPaycontrol() && mLetter == null;
        mTvToll.setVisibility(canPay && dynamicType != SendDynamicDataBean.VIDEO_TEXT_DYNAMIC ? View.VISIBLE : View.GONE);
        mTollLine.setVisibility(mTvToll.getVisibility());
        if (dynamicType == SendDynamicDataBean.VIDEO_TEXT_DYNAMIC) {
            mTitleUnderLine.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        mSelectMoney = new ArrayList<>();
        mSelectMoney.add(1f);
        mSelectMoney.add(5f);
        mSelectMoney.add(10f);

        mSystemConfigBean = mPresenter.getSystemConfigBean();

        if (mSystemConfigBean != null) {
            String[] amount = new String[]{};
            if (mSystemConfigBean.getFeed() != null) {
                amount = mSystemConfigBean.getFeed().getItems();
                int wordLimit = mSystemConfigBean.getFeed().getLimit();
                mTvWordsLimit.setText(String.format(getString(R.string.dynamic_send_toll_notes), wordLimit > 0 ? wordLimit : 50));
            }
            if (amount != null) {
                try {
                    mSelectMoney.add(0, Float.parseFloat(amount[0]));
                    mSelectMoney.add(1, Float.parseFloat(amount[1]));
                    mSelectMoney.add(2, Float.parseFloat(amount[2]));
                } catch (Exception ignore) {

                }
            }
        }
        initSelectMoney(mSelectMoney);
        mCustomMoney.setText(mPresenter.getGoldName());

        ActivityHandler.getInstance().removeAllSameActivity(PhotoAlbumDetailsActivity.class);
    }

    @Override
    public double getTollMoney() {
        return mTollMoney;
    }

    @Override
    public boolean hasTollVerify() {
        return isToll && !hasTollPic;
    }

    private void initSelectMoney(List<Float> mSelectDays) {
        mRbOne.setText(String.format(getString(R.string.dynamic_send_toll_select_money),
                mSelectDays.get(0)));
        mRbTwo.setText(String.format(getString(R.string.dynamic_send_toll_select_money),
                mSelectDays.get(1)));
        mRbThree.setText(String.format(getString(R.string.dynamic_send_toll_select_money),
                mSelectDays.get(2)));
    }

    private void initWordsToll() {
        mTvChooseTip.setText(R.string.dynamic_send_toll_words_count);
        RxTextView.textChanges(mEtInput).subscribe(charSequence -> {
            String spaceReg = "\n|\t";
            if (TextUtils.isEmpty(charSequence.toString().replaceAll(spaceReg, ""))) {
                return;
            }
            mRbDaysGroup.clearCheck();
            mTollMoney = Double.parseDouble(charSequence.toString());
        }, throwable -> mTollMoney = 0);

        RxRadioGroup.checkedChanges(mRbDaysGroup)
                .compose(this.bindToLifecycle())
                .subscribe(checkedId -> {
                    if (checkedId != -1) {
                        setCustomMoneyDefault();
                    }
                    switch (checkedId) {
                        case R.id.rb_one:
                            mTollMoney = mSelectMoney.get(0);
                            break;
                        case R.id.rb_two:
                            mTollMoney = mSelectMoney.get(1);
                            break;
                        case R.id.rb_three:
                            mTollMoney = mSelectMoney.get(2);
                            break;
                        default:
                            break;
                    }
                });
    }

    /**
     * 设置自定义金额数量
     */
    private void setCustomMoneyDefault() {
        mEtInput.setText("");
    }

    /**
     * 初始化图片选择弹框
     * 现在不提供图片选择弹窗，进入界面是选择动态类型
     */
    @Deprecated
    private void initPhotoPopupWindow() {
        if (mPhotoPopupWindow != null) {
            return;
        }
        mPhotoPopupWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.choose_from_photo))
                .item2Str(getString(R.string.choose_from_camera))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(0.8f)
                .with(getActivity())
                .item1ClickListener(() -> {
                    ArrayList<String> photos = new ArrayList<>();
                    // 最后一张是占位图
                    for (int i = 0; i < mSelectedPhotos.size(); i++) {
                        ImageBean imageBean = mSelectedPhotos.get(i);
                        if (!TextUtils.isEmpty(imageBean.getImgUrl())) {
                            photos.add(imageBean.getImgUrl());
                        }
                    }
                    mPhotoSelector.getPhotoListFromSelector(MAX_PHOTOS, photos);
                    mPhotoPopupWindow.hide();
                })
                .item2ClickListener(() -> {
                    ArrayList<String> photos = new ArrayList<>();
                    // 最后一张是占位图
                    for (int i = 0; i < mSelectedPhotos.size(); i++) {
                        ImageBean imageBean = mSelectedPhotos.get(i);
                        if (!TextUtils.isEmpty(imageBean.getImgUrl())) {
                            photos.add(imageBean.getImgUrl());
                        }
                    }
                    // 选择相机，拍照
                    mPhotoSelector.getPhotoFromCamera(photos);
                    mPhotoPopupWindow.hide();
                })
                .bottomClickListener(() -> mPhotoPopupWindow.hide()).build();
    }

    /**
     * 初始化取消选择弹框
     */
    private void initCanclePopupWindow() {
        if (mCanclePopupWindow != null) {
            return;
        }

        if (dynamicType == SendDynamicDataBean.VIDEO_TEXT_DYNAMIC) {
            mCanclePopupWindow = ActionPopupWindow.builder()
                    .item1Str(getString(R.string.info_publish_hint))
                    .desStr(getString(R.string.video_dynamic_send_cancel_hint))
                    .item2Str(getString(R.string.save))
                    .bottomStr(getString(R.string.drop))
                    .isOutsideTouch(true)
                    .isFocus(true)
                    .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                    .with(getActivity())
                    .item2ClickListener(() -> {
                        if (FileUtils.isFileExists(mSendDynamicDataBean.getVideoInfo().getPath())) {
                            String content = mEtDynamicContent.getInputContent();
                            if (TextUtils.isEmpty(content)) {
                                content = SharePreferenceUtils.VIDEO_DYNAMIC;
                            }
                            mSendDynamicDataBean.getVideoInfo().setDynamicContent(content);
                            SharePreferenceUtils.saveObject(mActivity, SharePreferenceUtils.VIDEO_DYNAMIC, mSendDynamicDataBean);
                        }
                        mCanclePopupWindow.hide();
                        ActivityHandler.getInstance().removeAllSameActivity(VideoSelectActivity.class);
                        mActivity.finish();
                    })
                    .bottomClickListener(() -> {
                        SharePreferenceUtils.remove(mActivity, SharePreferenceUtils.VIDEO_DYNAMIC);
                        mCanclePopupWindow.hide();
                        ActivityHandler.getInstance().removeAllSameActivity(VideoSelectActivity.class);
                        mActivity.finish();
                    })
                    .build();
        } else {
            mCanclePopupWindow = ActionPopupWindow.builder()
                    .item1Str(getString(R.string.dynamic_send_cancel_hint))
                    .item2Str(getString(R.string.determine))
                    .bottomStr(getString(R.string.cancel))
                    .isOutsideTouch(true)
                    .isFocus(true)
                    .backgroundAlpha(0.8f)
                    .with(getActivity())
                    .item2ClickListener(() -> {
                        mCanclePopupWindow.hide();
                        getActivity().finish();
                    })
                    .bottomClickListener(() -> mCanclePopupWindow.hide()).build();
        }

    }

    /**
     * 初始化图片选择器
     */
    private void initPhotoSelector() {
        mPhotoSelector = DaggerPhotoSelectorImplComponent
                .builder()
                .photoSeletorImplModule(new PhotoSeletorImplModule(this, this, PhotoSelectorImpl
                        .NO_CRAFT))
                .build().photoSelectorImpl();
    }

    @Override
    public void getPhotoSuccess(List<ImageBean> photoList) {
        if (isPhotoListChanged(mCachePhotos, photoList)) {
            // 图片改变了
            hasTollPic = false;
            mSelectedPhotos.clear();
            mSelectedPhotos.addAll(photoList);
            addPlaceHolder();
            setSendDynamicState();// 每次刷新图片后都要判断发布按钮状态
            mCommonAdapter.notifyDataSetChanged();
        } else {
            // 图片没改变
            if (mSelectedPhotos != null) {
                hasTollPic = false;
                for (ImageBean selectedPhoto : mSelectedPhotos) {
                    if (selectedPhoto.getToll_type() > 0) {
                        hasTollPic = true;
                        return;
                    }
                }
            }
        }
    }

    private void addPlaceHolder() {
        if (mSelectedPhotos.size() < MAX_PHOTOS) {
            // 占位缺省图
            ImageBean camera = new ImageBean();
            mSelectedPhotos.add(camera);
        }

    }

    @Override
    public void getPhotoFailure(String errorMsg) {
        ToastUtils.showToast(errorMsg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SearchTopicFragment.CHOOSE_TOPIC) {
            // 选择话题
            if (data != null && data.getExtras() != null) {
                TopicListBean topic = data.getExtras().getParcelable(SearchTopicFragment.TOPIC);
                if (!mSelectedTopics.contains(topic)) {
                    if (!mSelectedTopics.isEmpty()) {
                        mSelectedTopics.remove(mSelectedTopics.size() - 1);
                    }
                    mSelectedTopics.add(topic);
                    if (mSelectedTopics.size() < MAX_TOPICS) {
                        // 添加 add 图标
                        TopicListBean add = new TopicListBean();
                        mSelectedTopics.add(add);
                    }
                    mTopicAdapter.notifyDataChanged();
                }
                if (mRvTopicList.getVisibility() == View.GONE) {
                    mRvTopicList.setVisibility(View.VISIBLE);
                    mLineTopicBottom.setVisibility(View.VISIBLE);
                    mTvAddTopic.setVisibility(View.GONE);
                }
            }
        } else if (requestCode == AtUserListFragment.REQUES_USER) {
            // @ 选人返回
            if (data != null && data.getExtras() != null) {
                UserInfoBean userInfoBean = data.getExtras().getParcelable(AtUserListFragment.AT_USER);
                mEtDynamicContent.appendAt(userInfoBean);
            }
        } else if (mPhotoSelector != null && dynamicType != SendDynamicDataBean.VIDEO_TEXT_DYNAMIC) {
            if (requestCode == PhotoViewFragment.REQUEST_CODE) {
                // 设置收费金额快捷入口返回
                Toll toll = data.getBundleExtra(TOLL_TYPE).getParcelable(TOLL_TYPE);
                mSelectedPhotos.get(mCurrentTollImageBean).setToll(toll);
                mCommonAdapter.notifyDataSetChanged();
                return;
            }
            // 图片选择器界面数据保存操作
            if (data != null) {
                Bundle tollBundle = new Bundle();
                data.putExtra(TOLLBUNDLE, tollBundle);
                List<ImageBean> oldData = mCommonAdapter.getDatas();
                if (oldData == null) {
                    oldData = new ArrayList<>();
                }
                tollBundle.putParcelableArrayList(TOLLBUNDLE, new ArrayList<>(oldData));
            }
            mPhotoSelector.onActivityResult(requestCode, resultCode, data);
        } else if (dynamicType == SendDynamicDataBean.VIDEO_TEXT_DYNAMIC && resultCode == Activity.RESULT_OK) {
            // 获取视频返回结果
            if (requestCode == CoverFragment.REQUEST_COVER_CODE) {
                // 删除视频
                SharePreferenceUtils.remove(mActivity, SharePreferenceUtils.VIDEO_DYNAMIC);
                mSelectedPhotos.clear();
                addPlaceHolder();
                mCommonAdapter.notifyDataSetChanged();
            } else if (requestCode == VideoSelectFragment.RECHOSE_VIDEO) {
                // 重选视频
                if (data != null && data.getExtras() != null) {
                    SendDynamicDataBean sendDynamicDataBean = data.getExtras().getParcelable(SendDynamicActivity
                            .SEND_DYNAMIC_DATA);
                    if (sendDynamicDataBean != null) {
                        mSendDynamicDataBean = sendDynamicDataBean;
                        List<ImageBean> originPhotos = sendDynamicDataBean.getDynamicPrePhotos();
                        mSelectedPhotos.clear();
                        mSelectedPhotos.addAll(originPhotos);
                        addPlaceHolder();
                        mCommonAdapter.notifyDataSetChanged();
                    }
                }
            }
            // 每次刷新图片后都要判断发布按钮状态
            setSendDynamicState();
        }
    }

    @Override
    public void setPresenter(SendDynamicContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void sendDynamicComplete(boolean gotoHome) {
        com.zhiyicx.common.utils.DeviceUtils.hideSoftKeyboard(getContext(), mToolbarRight);
        // 让主页先刷新
        if (getView() != null) {
            getView().postDelayed(() -> {
                if (getActivity() != null) {
                    if (mTopicBean == null && gotoHome) {
                        // 在话题详情发的就不跳转
                        startActivity(new Intent(getActivity(), HomeActivity.class));
                    } else {
                        mActivity.finish();
                    }
                    getActivity().overridePendingTransition(0, R.anim.fade_out);
                }
            }, 100);
        }


    }

    @Override
    public SendDynamicDataBean getDynamicSendData() {
        Bundle bundle = getArguments();
        SendDynamicDataBean sendDynamicDataBean = null;
        if (bundle != null) {
            sendDynamicDataBean = bundle.getParcelable(SendDynamicActivity.SEND_DYNAMIC_DATA);
        }
        return sendDynamicDataBean;
    }

    @Override
    protected void setRightClick() {
        // 圈子
        if (isFromGroup) {
            mPresenter.sendGroupDynamic(packageGroupDynamicData());
        } else {
            mPresenter.sendDynamicV2(packageDynamicData());
        }
    }

    /**
     * 封装动态上传的数据
     */
    @Override
    public void packageDynamicStorageDataV2(SendDynamicDataBeanV2 sendDynamicDataBeanV2) {
        List<SendDynamicDataBeanV2.StorageTaskBean> storageTask = new ArrayList<>();
        List<ImageBean> photos = new ArrayList<>();
        if (mSelectedPhotos != null && !mSelectedPhotos.isEmpty()) {
            for (int i = 0; i < mSelectedPhotos.size(); i++) {
                if (!TextUtils.isEmpty(mSelectedPhotos.get(i).getImgUrl())) {
                    SendDynamicDataBeanV2.StorageTaskBean taskBean = new SendDynamicDataBeanV2.StorageTaskBean();
                    ImageBean imageBean = mSelectedPhotos.get(i);
                    photos.add(imageBean);
                    taskBean.setAmount(imageBean.getToll_monye() > 0 ? imageBean.getToll_monye() : null);
                    taskBean.setType(imageBean.getToll_monye() * imageBean.getToll_type() > 0
                            ? (imageBean.getToll_type() == LOOK_TOLL ? LOOK_TOLL_TYPE : DOWNLOAD_TOLL_TYPE) : null);
                    storageTask.add(taskBean);
                }
            }
        }
        if (mSelectedTopics != null && !mSelectedTopics.isEmpty()) {
            List<Integer> topics = new ArrayList<>();
            TopicListBean empty = null;
            for (TopicListBean topicListBean : mSelectedTopics) {
                if (TextUtils.isEmpty(topicListBean.getName())) {
                    empty = topicListBean;
                    continue;
                }
                topics.add(topicListBean.getId().intValue());
            }
            if (empty != null) {
                mSelectedTopics.remove(empty);
            }
            sendDynamicDataBeanV2.setTopicListBeans(mSelectedTopics);
            sendDynamicDataBeanV2.setTopics(topics);
        }
        if (sendDynamicDataBeanV2.getVideoInfo() != null) {
            sendDynamicDataBeanV2.getVideoInfo().setNeedGetCoverFromVideo(needGetCoverFromVideo());
            sendDynamicDataBeanV2.getVideoInfo().setNeedCompressVideo(needCompressVideo());
            sendDynamicDataBeanV2.getVideoInfo().setDuration(mSendDynamicDataBean.getVideoInfo().getDuration());
        }
        sendDynamicDataBeanV2.setPhotos(photos);
        sendDynamicDataBeanV2.setStorage_task(storageTask);
    }

    @Override
    public void packageGroupDynamicStorageData(GroupSendDynamicDataBean sendDynamicDataBeanV2) {
        List<ImageBean> photos = new ArrayList<>();
        if (mSelectedPhotos != null && !mSelectedPhotos.isEmpty()) {
            for (int i = 0; i < mSelectedPhotos.size(); i++) {
                if (!TextUtils.isEmpty(mSelectedPhotos.get(i).getImgUrl())) {
                    SendDynamicDataBeanV2.StorageTaskBean taskBean = new SendDynamicDataBeanV2.StorageTaskBean();
                    ImageBean imageBean = mSelectedPhotos.get(i);
                    photos.add(imageBean);
                    taskBean.setAmount(imageBean.getToll_monye() > 0 ? imageBean.getToll_monye() : null);
                    taskBean.setType(imageBean.getToll_monye() * imageBean.getToll_type() > 0
                            ? (imageBean.getToll_type() == LOOK_TOLL ? LOOK_TOLL_TYPE : DOWNLOAD_TOLL_TYPE) : null);
                }
            }
        }
        sendDynamicDataBeanV2.setPhotos(photos);
    }

    @Override
    public boolean wordsNumLimit() {
        return mLLToll.getVisibility() == View.VISIBLE;
    }

    private void setLeftTextColor() {
        mToolbarLeft.setTextColor(ContextCompat.getColor(getContext(), R.color.themeColor));
    }

    /**
     * 发布按钮的状态监听
     */
    private void initSendDynamicBtnState() {

        mEtDynamicContent.setContentChangedListener(s -> {
            hasContent = !TextUtils.isEmpty(s.toString().trim())/* || mLetter != null*/;
            setSendDynamicState();
        });

        mTvToll.setRightImageClickListener(v -> {
            if (!mPresenter.getSystemConfigBean().getFeed().hasPaycontrol()) {
                showSnackErrorMessage(getString(R.string.dynamic_close_pay));
                return;
            }
            isToll = !isToll;
            mTvToll.setRightImage(isToll ? R.mipmap.btn_open : R.mipmap.btn_close);
            mTollLine.setVisibility(isToll && dynamicType == TEXT_ONLY_DYNAMIC || dynamicType == VIDEO_TEXT_DYNAMIC ? View.GONE : View.VISIBLE);
            if (dynamicType == TEXT_ONLY_DYNAMIC) {
                mLLToll.setVisibility(isToll ? View.VISIBLE : View.GONE);
                mSlSendDynamic.smoothScrollTo(0, 0);
            } else {
                mCommonAdapter.notifyDataSetChanged();
            }
        });

    }

    /**
     * 设置动态发布按钮的点击状态
     */
    private void setSendDynamicState() {
        // 没有内容，并且只有占位图时不能够发送
        boolean noPic = mSelectedPhotos == null || !isToll && mSelectedPhotos.size() <= 1;
        if (!hasContent && noPic/* && mLetter == null*/) {
            mToolbarRight.setEnabled(false);
        } else {
            // 有内容或者有图片时都可以发送
            mToolbarRight.setEnabled(true);
        }
    }

    @Override
    public boolean needCompressVideo() {
        if (mSendDynamicDataBean == null || mSendDynamicDataBean.getVideoInfo() == null || dynamicType != SendDynamicDataBean.VIDEO_TEXT_DYNAMIC) {
            return false;
        }
        return mSendDynamicDataBean.getVideoInfo().needCompressVideo();
    }

    @Override
    public boolean needGetCoverFromVideo() {
        if (mSendDynamicDataBean == null || mSendDynamicDataBean.getVideoInfo() == null || dynamicType != SendDynamicDataBean.VIDEO_TEXT_DYNAMIC) {
            return false;
        }
        return mSendDynamicDataBean.getVideoInfo().needGetCoverFromVideo();
    }

    /**
     * 封装动态上传的数据
     */
    private DynamicDetailBeanV2 packageDynamicData() {

        DynamicDetailBeanV2 dynamicDetailBeanV2 = new DynamicDetailBeanV2();

        long userId = AppApplication.getmCurrentLoginAuth() != null ? AppApplication
                .getmCurrentLoginAuth().getUser_id() : 0;
        String feedMarkString = userId + "" + System.currentTimeMillis();
        long feedMark = Long.parseLong(feedMarkString);

        // 浏览量没有 0 ，从1 开始
        dynamicDetailBeanV2.setFeed_view_count(1);
        dynamicDetailBeanV2.setFeed_mark(feedMark);
        dynamicDetailBeanV2.setCreated_at(TimeUtils.getCurrenZeroTimeStr());
        dynamicDetailBeanV2.setFeed_content(mEtDynamicContent.getInputContent());
        dynamicDetailBeanV2.setFeed_from(ApiConfig.ANDROID_PLATFORM);
        // 因为关注里面需要显示我的动态
        dynamicDetailBeanV2.setIsFollowed(true);
        dynamicDetailBeanV2.setState(DynamicDetailBeanV2.SEND_ING);
        dynamicDetailBeanV2.setComments(new ArrayList<>());
        dynamicDetailBeanV2.setUser_id(userId);
        dynamicDetailBeanV2.setAmount((long) mTollMoney);

        if (mSelectedPhotos != null && !mSelectedPhotos.isEmpty()) {

            List<DynamicDetailBeanV2.ImagesBean> images = new ArrayList<>();
            // 最后一张占位图，扔掉
            for (int i = 0; i < mSelectedPhotos.size(); i++) {
                if (!TextUtils.isEmpty(mSelectedPhotos.get(i).getImgUrl())) {
                    DynamicDetailBeanV2.ImagesBean imagesBean = new DynamicDetailBeanV2.ImagesBean();
                    imagesBean.setImgUrl(mSelectedPhotos.get(i).getImgUrl());
                    BitmapFactory.Options options = DrawableProvider.getPicsWHByFile
                            (mSelectedPhotos.get(i).getImgUrl());
                    imagesBean.setHeight(options.outHeight);
                    imagesBean.setWidth(options.outWidth);
                    imagesBean.setImgMimeType(options.outMimeType);
                    images.add(imagesBean);
                }
            }
            dynamicDetailBeanV2.setImages(images);

            if (dynamicType == SendDynamicDataBean.VIDEO_TEXT_DYNAMIC) {
                VideoInfo videoInfo = mSendDynamicDataBean.getVideoInfo();
                DynamicDetailBeanV2.Video video = new DynamicDetailBeanV2.Video();
                video.setCreated_at(dynamicDetailBeanV2.getCreated_at());
                video.setHeight(videoInfo.getHeight());
                video.setCover(videoInfo.getCover());
                video.setWidth(videoInfo.getWidth());
                video.setUrl(videoInfo.getPath());
                dynamicDetailBeanV2.setVideo(video);
                SharePreferenceUtils.remove(mActivity, SharePreferenceUtils.VIDEO_DYNAMIC);
            }
        }
        dynamicDetailBeanV2.setMLetter(mLetter);
        return dynamicDetailBeanV2;
    }

    private GroupDynamicListBean packageGroupDynamicData() {
        GroupDynamicListBean groupSendDynamicDataBean = new GroupDynamicListBean();
        long userId = AppApplication.getmCurrentLoginAuth() != null ? AppApplication
                .getmCurrentLoginAuth().getUser_id() : 0;

        String feedMarkString = userId + "" + System.currentTimeMillis();
        long feedMark = Long.parseLong(feedMarkString);

        groupSendDynamicDataBean.setViews(1);
        groupSendDynamicDataBean.setFeed_mark(feedMark);
        groupSendDynamicDataBean.setGroup_id((int) getDynamicSendData().getDynamicChannlId());
        groupSendDynamicDataBean.setCreated_at(TimeUtils.getCurrenZeroTimeStr());
        groupSendDynamicDataBean.setContent(mEtDynamicContent.getInputContent());
        groupSendDynamicDataBean.setTitle(mEtDynamicTitle.getInputContent());
        groupSendDynamicDataBean.setNew_comments(new ArrayList<>());
        groupSendDynamicDataBean.setUser_id(userId);


        if (mSelectedPhotos != null && !mSelectedPhotos.isEmpty()) {
            List<GroupDynamicListBean.ImagesBean> images = new ArrayList<>();
            // 最后一张占位图，扔掉
            for (int i = 0; i < mSelectedPhotos.size(); i++) {
                if (!TextUtils.isEmpty(mSelectedPhotos.get(i).getImgUrl())) {
                    GroupDynamicListBean.ImagesBean imagesBean = new GroupDynamicListBean.ImagesBean();
                    imagesBean.setImgUrl(mSelectedPhotos.get(i).getImgUrl());
                    BitmapFactory.Options options = DrawableProvider.getPicsWHByFile
                            (mSelectedPhotos.get(i).getImgUrl());
                    imagesBean.setHeight(options.outHeight);
                    imagesBean.setWidth(options.outWidth);
                    imagesBean.setImgMimeType(options.outMimeType);
                    images.add(imagesBean);
                }
            }
            groupSendDynamicDataBean.setImages(images);
        }

        return groupSendDynamicDataBean;
    }

    /**
     * 初始化图片列表
     */
    private void initPhotoList(Bundle bundle) {
        if (mSelectedPhotos == null) {
            mSelectedPhotos = new ArrayList<>();
        }
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), ITEM_COLUM);
        mRvPhotoList.setLayoutManager(gridLayoutManager);
        mRvPhotoList.setVisibility(View.VISIBLE);
        // 设置recyclerview的item之间的空白
        int witdh = ConvertUtils.dp2px(getContext(), 5);
        mRvPhotoList.addItemDecoration(new GridDecoration(witdh, witdh));
        // 占位缺省图
        addPlaceHolder();
        mCommonAdapter = new CommonAdapter<ImageBean>(getContext(), R.layout
                .item_send_dynamic_photo_list, mSelectedPhotos) {
            @Override
            protected void convert(ViewHolder holder, final ImageBean imageBean, final int
                    position) {
                // 固定每个item的宽高
                // 屏幕宽高减去左右margin以及item之间的空隙
                int width = UIUtils.getWindowWidth(getContext()) - getResources()
                        .getDimensionPixelSize(R.dimen.spacing_large) * 2
                        - ConvertUtils.dp2px(getContext(), 5) * (ITEM_COLUM - 1);
                View convertView = holder.getConvertView();
                convertView.getLayoutParams().width = width / ITEM_COLUM;
                convertView.getLayoutParams().height = width / ITEM_COLUM;
                final FilterImageView imageView = holder.getView(R.id.iv_dynamic_img);
                final IconTextView paintView = holder.getView(R.id.iv_dynamic_img_paint);
                final View filterView = holder.getView(R.id.iv_dynamic_img_filter);
                if (TextUtils.isEmpty(imageBean.getImgUrl())) {
                    // 最后一项作为占位图
                    paintView.setVisibility(View.GONE);
                    filterView.setVisibility(View.GONE);
                    holder.setVisible(R.id.iv_dynamic_img_video, View.GONE);
                    // 换成摄像图标
                    imageView.setImageResource(dynamicType == SendDynamicDataBean.VIDEO_TEXT_DYNAMIC ?
                            R.mipmap.ico_video_remake : R.mipmap.img_edit_photo_frame);
                    holder.setVisible(R.id.tv_record, dynamicType == SendDynamicDataBean.VIDEO_TEXT_DYNAMIC ? View.VISIBLE : View.GONE);
                    imageView.setIshowGifTag(false);

                } else {
                    holder.setVisible(R.id.iv_dynamic_img_video, dynamicType == SendDynamicDataBean.VIDEO_TEXT_DYNAMIC ? View.VISIBLE : View.GONE);
                    paintView.setVisibility(isToll ? View.VISIBLE : View.GONE);
                    filterView.setVisibility(isToll ? View.VISIBLE : View.GONE);
                    holder.setVisible(R.id.tv_record, View.GONE);
                    if (!isToll) {
                        imageBean.setToll(null);
                    }

                    if (imageBean.getToll_type() > 0) {
                        hasTollPic = true;
                        filterView.setVisibility(View.VISIBLE);
                        paintView.setIconRes(R.mipmap.ico_coins);
                        paintView.setText(imageBean.getToll_monye() + "");
                    } else {
                        paintView.setIconRes(R.mipmap.ico_edit_pen);
                        filterView.setVisibility(View.GONE);
                        paintView.setText(getString(R.string.dynamic_send_toll_quick));
                    }
                    Glide.with(getContext())
                            .load(imageBean.getImgUrl())
                            .asBitmap()
                            .placeholder(R.drawable.shape_default_image)
                            .error(R.drawable.shape_default_error_image)
                            .override(convertView.getLayoutParams().width, convertView.getLayoutParams().height)
                            .into(imageView);
                    imageView.setIshowGifTag(ImageUtils.imageIsGif(imageBean.getImgMimeType()));

                }
                paintView.setOnClickListener(v -> {
                    DeviceUtils.hideSoftKeyboard(getContext(), v);
                    Intent intent = new Intent(getActivity(), PictureTollActivity.class);
                    Bundle bundle1 = new Bundle();
                    bundle1.putParcelable(PhotoViewFragment.OLDTOLL, imageBean);
                    mCurrentTollImageBean = position;
                    intent.putExtra(PhotoViewFragment.OLDTOLL, bundle1);
                    startActivityForResult(intent, PhotoViewFragment.REQUEST_CODE);
                });
                imageView.setOnClickListener(v -> {
                    try {
                        DeviceUtils.hideSoftKeyboard(getContext(), v);
                        if (TextUtils.isEmpty(imageBean.getImgUrl())) {
                            if (dynamicType == SendDynamicDataBean.VIDEO_TEXT_DYNAMIC) {
                                VideoSelectActivity.startVideoSelectActivity(mActivity, true);
                                return;
                            }
                            ArrayList<String> photos = new ArrayList<>();
                            // 最后一张是占位图
                            for (int i = 0; i < mSelectedPhotos.size(); i++) {
                                ImageBean imageBean1 = mSelectedPhotos.get(i);
                                if (!TextUtils.isEmpty(imageBean1.getImgUrl())) {
                                    photos.add(imageBean1.getImgUrl());
                                }
                            }
                            mPhotoSelector.getPhotoListFromSelector(MAX_PHOTOS, photos);
                        } else {
                            if (dynamicType == SendDynamicDataBean.VIDEO_TEXT_DYNAMIC) {
                                ArrayList<String> srcList = new ArrayList<>();
                                srcList.add(mSendDynamicDataBean.getVideoInfo().getPath());
                                CoverActivity.startCoverActivity(mActivity, srcList, true, false, false);
                                return;
                            }

                            // 预览图片
                            ArrayList<String> photos = new ArrayList<>();
                            // 最后一张是占位图
                            for (int i = 0; i < mSelectedPhotos.size(); i++) {
                                ImageBean imageBean1 = mSelectedPhotos.get(i);
                                if (!TextUtils.isEmpty(imageBean1.getImgUrl())) {
                                    photos.add(imageBean1.getImgUrl());
                                }
                            }
                            ArrayList<AnimationRectBean> animationRectBeanArrayList
                                    = new ArrayList<>();
                            for (int i = 0; i < photos.size(); i++) {

                                if (i < gridLayoutManager.findFirstVisibleItemPosition()) {
                                    // 顶部，无法全部看见的图片
                                    AnimationRectBean rect = new AnimationRectBean();
                                    animationRectBeanArrayList.add(rect);
                                } else if (i > gridLayoutManager.findLastVisibleItemPosition()) {
                                    // 底部，无法完全看见的图片
                                    AnimationRectBean rect = new AnimationRectBean();
                                    animationRectBeanArrayList.add(rect);
                                } else {
                                    View view = gridLayoutManager
                                            .getChildAt(i - gridLayoutManager
                                                    .findFirstVisibleItemPosition());
                                    ImageView imageView1 = (ImageView) view.findViewById(R.id
                                            .iv_dynamic_img);
                                    // 可以完全看见的图片
                                    AnimationRectBean rect = AnimationRectBean.buildFromImageView
                                            (imageView1);
                                    animationRectBeanArrayList.add(rect);
                                }
                            }
                            ArrayList<ImageBean> datas = new ArrayList<>();
                            datas.addAll(mSelectedPhotos);
                            mCachePhotos = new ArrayList<>(mSelectedPhotos);
                            PhotoViewActivity.startToPhotoView(SendDynamicFragment.this,
                                    photos, photos, animationRectBeanArrayList, MAX_PHOTOS,
                                    position, isToll, datas);
                        }
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                });
            }
        };

        mRvPhotoList.setAdapter(mCommonAdapter);
    }

    private void initTopicList(Bundle bundle) {
        if (mSelectedTopics == null) {
            mSelectedTopics = new ArrayList<>();
        }
        if (mTopicBean != null) {
            mSelectedTopics.add(mTopicBean);
        }
        if (mSelectedTopics.size() < MAX_TOPICS) {
            TopicListBean add = new TopicListBean();
            mSelectedTopics.add(add);
        }
        mTopicAdapter = new TagAdapter<TopicListBean>(mSelectedTopics) {

            @Override
            public View getView(FlowLayout parent, int position, TopicListBean topicListBean) {
                View v = LayoutInflater.from(mActivity).inflate(R.layout.item_topic_channel, null);
                TextView name = v.findViewById(R.id.tv_content);
                View delete = v.findViewById(R.id.iv_delete);

                delete.setVisibility(topicListBean.equals(mTopicBean) ? View.GONE : View.VISIBLE);
                if (TextUtils.isEmpty(topicListBean.getName())) {
                    name.setText("");
                    delete.setVisibility(View.GONE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) name.getLayoutParams();
                    params.leftMargin = params.bottomMargin = params.rightMargin = params.topMargin = 0;
                    name.setLayoutParams(params);
                    name.setBackground(null);
                    name.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ico_add_topic, 0, 0, 0);
                } else {
                    name.setBackgroundResource(R.drawable.item_channel_bg_normal);
                    name.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    name.setText(topicListBean.getName());
                }
                v.setOnClickListener(view -> {
                    if (topicListBean.equals(mTopicBean)) {
                        return;
                    }
                    if (TextUtils.isEmpty(topicListBean.getName())) {
                        SearchTopicActivity.startSearchTopicActivity(mActivity, true);
                    } else {
                        mSelectedTopics.remove(position);
                        if (!mSelectedTopics.contains(new TopicListBean())) {
                            // 添加 add 图标
                            TopicListBean add = new TopicListBean();
                            mSelectedTopics.add(add);
                        }
                        mTopicAdapter.notifyDataChanged();
                        if (mSelectedTopics.size() == 1) {
                            mRvTopicList.setVisibility(View.GONE);
                            mLineTopicBottom.setVisibility(View.GONE);
                            mTvAddTopic.setVisibility(View.VISIBLE);
                        }
                    }
                });
                return v;
            }
        };
        if (mTopicBean != null) {
            mRvTopicList.setVisibility(View.VISIBLE);
            mLineTopicBottom.setVisibility(View.VISIBLE);
            mTvAddTopic.setVisibility(View.GONE);
        } else {
            mTvAddTopic.setVisibility(View.VISIBLE);
        }
        mTvAddTopic.setOnClickListener(view -> SearchTopicActivity.startSearchTopicActivity(mActivity, true));
        mRvTopicList.setAdapter(mTopicAdapter);
    }

    private void initLetterForward(Letter letter) {
        if (letter == null) {
            return;
        }

        mEtDynamicContent.getEtContent().setLines(getResources().getInteger(R.integer.dynamic_content_show_lines_forward));
        mFlForwardContainer.removeAllViews();
        int contentId;
        if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC.equals(letter.getType())) {
            // 动态
            contentId = R.layout.forward_for_feed;
        } else if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO.equals(letter.getType())) {
            // 资讯
            contentId = R.layout.forward_for_info;
        } else if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_CIRCLE.equals(letter.getType())) {
            // 圈子
            contentId = R.layout.forward_for_circle;
        } else if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION.equals(letter.getType())) {
            // 问题
            contentId = R.layout.forward_for_question;
        } else if (TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_ANSWERS.equals(letter.getType())) {
            // 回答
            contentId = R.layout.forward_for_answer;
        } else {
            // 帖子
            contentId = R.layout.forward_for_post;
        }
        mFlForwardContainer.addView(LayoutInflater.from(mActivity).inflate(contentId, null));
        if (contentId != R.layout.forward_for_feed &&
                contentId != R.layout.forward_for_question &&
                contentId != R.layout.forward_for_answer) {
            initImageView(letter.getImage(), R.id.iv_forward_image);
        }

        initTextView(letter.getType(), letter.getContent(), 0, R.id.tv_forward_content, letter.getDynamic_type());
        initTextView(letter.getType(), letter.getName(), 0, R.id.tv_forward_name, letter.getDynamic_type());
    }

    protected void initTextView(String letterType, String text, int colorId, int resId, String dynamicType) {

        if (!TextUtils.isEmpty(text)) {
            TextView textView = mFlForwardContainer.findViewById(resId);
            if (textView instanceof SpanTextViewWithEllipsize) {
                ((SpanTextViewWithEllipsize) textView).setNeedLookMore(false);
            }
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
            if (R.id.tv_forward_content == resId && TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC.equals(letterType)) {
                if (TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_IMAGE.equals(dynamicType)) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ico_pic_disabled, 0, 0, 0);
                }
                if (TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_VIDEO.equals(dynamicType)) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ico_video_disabled, 0, 0, 0);
                }
            }
            if (R.id.tv_title == resId) {
                String title = textView.getContext().getResources().getString(R.string.letter_send_to, text);
                CharSequence titleString;
                if (!TextUtils.isEmpty(text)) {
                    titleString = ColorPhrase.from(title).withSeparator("<>")
                            .innerColor(ContextCompat.getColor(mActivity, R.color.important_for_theme))
                            .outerColor(ContextCompat.getColor(mActivity, R.color.important_for_content))
                            .format();
                } else {
                    titleString = title;
                }
                textView.setText(titleString);
            }

            if (colorId != 0) {
                textView.setTextColor(ContextCompat.getColor(mActivity, colorId));
            }
        }
    }

    private void initImageView(String path, int resId) {
        ImageView imageView = mFlForwardContainer.findViewById(resId);

        imageView.setVisibility(TextUtils.isEmpty(path) ? View.GONE : View.VISIBLE);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Glide.with(mActivity)
                .load(path)
                .placeholder(R.drawable.shape_default_image)
                .error(R.drawable.shape_default_image)
                .into(imageView);
    }

    /**
     * 根据 dynamicType 初始化界面布局，比如发纯文字动态就隐藏图片
     */
    private void initDynamicType() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mSendDynamicDataBean = bundle.getParcelable(SendDynamicActivity
                    .SEND_DYNAMIC_DATA);

            mLetter = bundle.getParcelable(SendDynamicActivity
                    .SEND_LETTER_DATA);
            if (mSendDynamicDataBean == null) {
                throw new IllegalArgumentException("SEND_DYNAMIC_DATA can not be null");
            }
            dynamicType = mSendDynamicDataBean.getDynamicType();
            mTopicBean = bundle.getParcelable(SearchTopicFragment.TOPIC);
            restoreVideoDraft(mSendDynamicDataBean);
            List<ImageBean> originPhotos = mSendDynamicDataBean.getDynamicPrePhotos();
            if (originPhotos != null) {
                mSelectedPhotos = new ArrayList<>(MAX_PHOTOS);
                mSelectedPhotos.addAll(originPhotos);
            }
            if (mSendDynamicDataBean.getDynamicBelong() == SendDynamicDataBean.GROUP_DYNAMIC) {
                isFromGroup = true;
                mTvToll.setVisibility(View.GONE);
            }
        }
        switch (dynamicType) {
            case SendDynamicDataBean.VIDEO_TEXT_DYNAMIC:
                mEtDynamicContent.getEtContent().setHint(getString(R.string
                        .dynamic_content_no_pic_hint));
            case SendDynamicDataBean.PHOTO_TEXT_DYNAMIC:
                initPhotoSelector();
                initPhotoList(bundle);
                break;
            case TEXT_ONLY_DYNAMIC:
                hasTollPic = true;
                // 隐藏图片控件
                mRvPhotoList.setVisibility(View.GONE);
                mEtDynamicContent.getEtContent().setHint(getString(R.string
                        .dynamic_content_no_pic_hint));
                break;
            default:
        }
        initTopicList(bundle);
        initLetterForward(mLetter);
    }

    private void restoreVideoDraft(SendDynamicDataBean sendDynamicDataBean) {
        VideoInfo videoInfo = sendDynamicDataBean.getVideoInfo();
        if (videoInfo != null) {

            // 如果没有经过选择封面步骤，则cover是 null
            if (TextUtils.isEmpty(videoInfo.getCover())) {
                videoInfo.setNeedGetCoverFromVideo(true);
            } else {
                videoInfo.setNeedGetCoverFromVideo(false);
            }

            if (!TextUtils.isEmpty(videoInfo.getDynamicContent())) {
                String content = videoInfo.getDynamicContent();
                if (SharePreferenceUtils.VIDEO_DYNAMIC.equals(content)) {
                    return;
                }
                mEtDynamicContent.setText(content);
            }
        }

    }

    /**
     * 图片列表返回后，判断图片列表内容以及顺序是否发生变化，如果没变，就可以不用刷新
     */
    private boolean isPhotoListChanged(List<ImageBean> oldList, List<ImageBean> newList) {
//        if (!newList.isEmpty()) {
//            return true;
//        }
        // 取消了所有选择的图片
        if (newList == null || newList.isEmpty()) {
            return oldList.size() > 1;
        } else {
            boolean oldListIsNull = oldList == null || oldList.isEmpty();
            if (oldListIsNull) {
                return true;
            }
            int oldSize = 0;
            // 最后一张是占位图
            if (TextUtils.isEmpty(oldList.get(oldList.size() - 1).getImgUrl())) {
                oldSize = oldList.size() - 1;
            } else {
                oldSize = oldList.size();
            }
            if (oldSize != newList.size()) {
                // 如果长度不同，那肯定改变了
                return true;
            } else {
                // 继续判断内容和顺序变了没有
                for (int i = 0; i < newList.size(); i++) {
                    ImageBean newImageBean = newList.get(i);
                    ImageBean oldImageBean = oldList.get(i);
                    boolean tollIsSame = newImageBean.getToll() != null && !newImageBean.getToll().equals(oldImageBean.getToll
                            ());
                    if (!tollIsSame) {
                        return true;
                    }
                    if (!oldImageBean.equals(newImageBean)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    @Override
    public void initInstructionsPop(String des) {
        DeviceUtils.hideSoftKeyboard(getContext(), mRootView);
        if (mInstructionsPopupWindow != null) {
            mInstructionsPopupWindow = mInstructionsPopupWindow.newBuilder()
                    .item1Str(des)
                    .build();
            mInstructionsPopupWindow.show();
            return;
        }
        mInstructionsPopupWindow = ActionPopupWindow.builder()
                .item1Str(des)
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .with(getActivity())
                .bottomClickListener(() -> mInstructionsPopupWindow.hide())
                .build();
        mInstructionsPopupWindow.show();
    }

    /**
     * 把剪切板的文字去除样式
     */
    protected void handleClipboardContent() {
        ClipboardManager clipboardManager = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        //判断剪切版时候有内容
        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                if (clipData.getItemAt(0) == null) {
                    return;
                }
                if (clipData.getItemAt(0).coerceToText(mActivity) == null) {
                    return;
                }
                String text = clipData.getItemAt(0).coerceToText(mActivity).toString();
                ClipData mClipData = ClipData.newPlainText("Label", text);
                clipboardManager.setPrimaryClip(mClipData);
            }
        }

    }

}
