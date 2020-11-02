package com.zhiyicx.thinksnsplus.modules.dynamic.send.dynamic_type.V2;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.zhiyicx.baseproject.base.SystemConfigBean;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.impl.photoselector.DaggerPhotoSelectorImplComponent;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSelectorImpl;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSeletorImplModule;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.common.utils.SharePreferenceUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.config.EventBusTagConfig;
import com.zhiyicx.thinksnsplus.config.SharePreferenceTagConfig;
import com.zhiyicx.thinksnsplus.data.beans.SendCertificationBean;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean;
import com.zhiyicx.thinksnsplus.data.beans.TopicListBean;
import com.zhiyicx.thinksnsplus.data.beans.UserCertificationInfo;
import com.zhiyicx.thinksnsplus.modules.certification.detail.CertificationDetailActivity;
import com.zhiyicx.thinksnsplus.modules.certification.input.CertificationInputActivity;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.SendDynamicActivity;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.SendDynamicFragment;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.dynamic_type.SelectDynamicTypeContract;
import com.zhiyicx.thinksnsplus.modules.information.publish.detail.EditeInfoDetailActivity;
import com.zhiyicx.thinksnsplus.modules.markdown_editor.BaseMarkdownActivity;
import com.zhiyicx.thinksnsplus.modules.q_a.publish.question.PublishQuestionActivity;
import com.zhiyicx.thinksnsplus.modules.shortvideo.videostore.VideoSelectActivity;
import com.zhiyicx.thinksnsplus.modules.topic.search.SearchTopicFragment;
import com.zhiyicx.thinksnsplus.widget.IconTextView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;
import static com.zhiyicx.thinksnsplus.config.EventBusTagConfig.EVENT_CHECK_IN_CLICK;
import static com.zhiyicx.thinksnsplus.modules.certification.detail.CertificationDetailActivity.BUNDLE_DETAIL_DATA;
import static com.zhiyicx.thinksnsplus.modules.certification.detail.CertificationDetailActivity.BUNDLE_DETAIL_TYPE;
import static com.zhiyicx.thinksnsplus.modules.certification.input.CertificationInputActivity.BUNDLE_CERTIFICATION_TYPE;
import static com.zhiyicx.thinksnsplus.modules.certification.input.CertificationInputActivity.BUNDLE_TYPE;
import static me.iwf.photopicker.PhotoPicker.DEFAULT_REQUST_ALBUM;

/**
 * @Author Jliuer
 * @Date 2017/05/25/14:47
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SelectDynamicTypeFragmentV2 extends TSFragment<SelectDynamicTypeContract.Presenter>
        implements SelectDynamicTypeContract.View, PhotoSelectorImpl.IPhotoBackListener {
    public static final int DEFAULT_ANIMATE_DELAY_START = 80;
    public static final int DEFAULT_ANIMATE_DELAY = 80;

    public static final String SEND_OPTION = "send_option";
    public static final String GROUP_ID = "group_id";
    public static final String TYPE = "type";

    @BindView(R.id.im_close_dynamic)
    ImageView mImCloseDynamic;

    /**
     * 类型列表
     */
    @BindView(R.id.select_dynamic_parent)
    RecyclerView mSelectDynamicParent;
    private PhotoSelectorImpl mPhotoSelector;

    /**
     * 动态还是圈子动态
     */
    private int mType = SendDynamicDataBean.NORMAL_DYNAMIC;

    /**
     * 提示需要认证的
     */
    private ActionPopupWindow mCertificationAlertPopWindow;

    /**
     * 提示需要付钱的
     */
    private ActionPopupWindow mPayAlertPopWindow;

    private UserCertificationInfo mUserCertificationInfo;

    /**
     * 全部类型
     */
    private List<ContentType> mTypes = new ArrayList<>();

    private TopicListBean mTopicBean;

    public static SelectDynamicTypeFragmentV2 getInstance(Bundle b) {
        SelectDynamicTypeFragmentV2 selectDynamicTypeFragment = new SelectDynamicTypeFragmentV2();
        selectDynamicTypeFragment.setArguments(b);
        return selectDynamicTypeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(TYPE);
            mTopicBean = getArguments().getParcelable(SearchTopicFragment.TOPIC);
        }
    }

    @Override
    protected boolean usePermisson() {
        return true;
    }

    @Override
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @Override
    protected void initView(View rootView) {
        initPopWindow();
        initTypes();
        mSelectDynamicParent.setAdapter(new CommonAdapter<ContentType>(mActivity, R.layout.item_send_type, mTypes) {
            @Override
            protected void convert(ViewHolder holder, ContentType type, int position) {
                IconTextView iconTextView = holder.getView(R.id.it_item);
                iconTextView.setText(type.name);
                iconTextView.setIconRes(type.icoId);
                holder.itemView.setOnClickListener(view -> onViewClicked(type.icoId));
            }
        });
        mSelectDynamicParent.setLayoutManager(new GridLayoutManager(mActivity, mTopicBean == null ? 4 : 3));
        mImCloseDynamic.setOnClickListener(view -> closeActivity());

    }

    @Override
    protected void initData() {
        mPhotoSelector = DaggerPhotoSelectorImplComponent
                .builder()
                .photoSeletorImplModule(new PhotoSeletorImplModule(this, this, PhotoSelectorImpl
                        .NO_CRAFT))
                .build().photoSelectorImpl();
        initAnimation(mSelectDynamicParent);
    }

    @Override
    public void setUserCertificationInfo(UserCertificationInfo userCertificationInfo) {
        mUserCertificationInfo = userCertificationInfo;
        mSystemConfigBean = mPresenter.getSystemConfigBean();
        SystemConfigBean.NewsConfig mPublishInfoConfig = mSystemConfigBean.getNewsContribute();
        if (userCertificationInfo.getStatus() == UserCertificationInfo.CertifyStatusEnum.PASS.value || !mPublishInfoConfig.getContribute().hasVerified()) {
            if (mPresenter.isNeedPayTip() && (mPublishInfoConfig != null
                    && mPublishInfoConfig.getContribute().hasPay())) {
                mPayAlertPopWindow.show();
            } else {
                startActivity(new Intent(getActivity(), EditeInfoDetailActivity.class));
                closeActivity();
            }
        } else {
            mCertificationAlertPopWindow.show();
        }
    }

    private void initAnimation(final View view) {
        if (view == null) {
            return;
        }
        view.post(() -> {
            if (mSelectDynamicParent == null) {
                return;
            }
            AnimatorSet mAnimatorSet = new AnimatorSet();
            int verticalDistance = view.getTop() - mImCloseDynamic.getBottom();
            ViewCompat.setPivotX(view, view.getWidth() / 2.0f);
            ViewCompat.setPivotY(view, view.getHeight() / 2.0f);
            mAnimatorSet.setDuration(300);
            OvershootInterpolator interpolator = new OvershootInterpolator(1f);
            mAnimatorSet.setInterpolator(interpolator);
            ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", verticalDistance, 0);
            //第一个参数为 view对象，第二个参数为 动画改变的类型，第三，第四个参数依次是开始透明度和结束透明度。
            ObjectAnimator alpha = ObjectAnimator.ofFloat(getActivity().getWindow().getDecorView(), "alpha", 0f, 0.99f);
            alpha.setDuration(700);//设置动画时间
            alpha.setInterpolator(new OvershootInterpolator());//设置动画插入器，减速
            alpha.start();
            mAnimatorSet.play(translationY);
            view.setVisibility(View.VISIBLE);
            mAnimatorSet.start();
        });
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_dynamic_typev2;
    }


    private void onViewClicked(int view) {
        switch (view) {
            case R.mipmap.ico_word:
                SendDynamicDataBean sendWordsDynamicDataBean = new SendDynamicDataBean();
                sendWordsDynamicDataBean.setDynamicBelong(mType);
                sendWordsDynamicDataBean.setDynamicType(SendDynamicDataBean.TEXT_ONLY_DYNAMIC);
                if (getArguments() != null) {
                    sendWordsDynamicDataBean.setDynamicChannlId(getArguments().getLong(GROUP_ID));
                }
                SendDynamicActivity.startToSendDynamicActivity(getContext(), sendWordsDynamicDataBean, mTopicBean);
                closeActivity();
                break;
            case R.mipmap.ico_potoablum:
                clickSendPhotoTextDynamic();
                break;
            case R.mipmap.ico_attendance:
                EventBus.getDefault().post(true, EVENT_CHECK_IN_CLICK);
                getActivity().finish();
                break;
            case R.mipmap.ico_question:
                // 提问
                startActivity(new Intent(getActivity(), PublishQuestionActivity.class));
                closeActivity();
                break;
            case R.mipmap.ico_contribute:
                // 投稿
                // 发布提示 1、首先需要认证 2、需要付费
                mPresenter.checkCertification();
                break;
            case R.mipmap.ico_video:
                // 小视频
                mRxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                        .subscribe(aBoolean -> {
                            if (aBoolean) {
                                if (DeviceUtils.getSDCardAvailableSize() >= 100) {
                                    SendDynamicDataBean sendDynamicDataBean = SharePreferenceUtils.getObject(mActivity, SharePreferenceUtils
                                            .VIDEO_DYNAMIC);
                                    if (checkVideoDraft(sendDynamicDataBean)) {
                                        SendDynamicActivity.startToSendDynamicActivity(getContext(),
                                                sendDynamicDataBean, mTopicBean);
                                    } else {
                                        VideoSelectActivity.startVideoSelectActivity(mActivity, false,mTopicBean);
                                    }
                                    closeActivity();
                                } else {
                                    showSnackErrorMessage(getString(R.string.storage_no_free));
                                }
                            } else {
                                showSnackWarningMessage(getString(R.string.please_open_camera_and_mic_permisssion));

                            }
                        });
                break;
                /*
                 发帖
                 */
            case R.mipmap.ico_fatie:
                BaseMarkdownActivity.startActivityForPublishPostOutCircle(mActivity);
                closeActivity();
                break;
            default:
        }
    }


    @Override
    public void getPhotoSuccess(List<ImageBean> photoList) {
        // 跳转到发送动态页面
        SendDynamicDataBean sendDynamicDataBean = new SendDynamicDataBean();
        sendDynamicDataBean.setDynamicBelong(mType);
        sendDynamicDataBean.setDynamicPrePhotos(photoList);
        if (getArguments() != null) {
            sendDynamicDataBean.setDynamicChannlId(getArguments().getLong(GROUP_ID));
        }
        sendDynamicDataBean.setDynamicType(SendDynamicDataBean.PHOTO_TEXT_DYNAMIC);
        SendDynamicActivity.startToSendDynamicActivity(getContext(), sendDynamicDataBean, mTopicBean);
        closeActivity();
    }

    @Override
    public void getPhotoFailure(String errorMsg) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 获取图片选择器返回结果
        if (mPhotoSelector != null) {
            mPhotoSelector.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscriber(tag = EventBusTagConfig.EVENT_SEND_DYNAMIC_PHOT_FIRST_OPEN_SEND_DYNAMIC_PAGE)
    public void sendDynamicPhotFirstOpenSendDynamicPage(Intent data) {
        // 获取图片选择器返回结果
        if (mPhotoSelector != null) {
            mPhotoSelector.onActivityResult(DEFAULT_REQUST_ALBUM, RESULT_OK, data);
        }
    }

    private void clickSendPhotoTextDynamic() {
        mPhotoSelector.getPhotoListFromSelector(SendDynamicFragment.MAX_PHOTOS, null);
    }

    private void initPopWindow() {

        if (mCertificationAlertPopWindow == null) {
            mCertificationAlertPopWindow = ActionPopupWindow.builder()
                    .item1Str(getString(R.string.info_publish_hint))
                    .item2Str(getString(R.string.certification_personage))
                    .item3Str(getString(R.string.certification_company))
                    .desStr(getString(R.string.info_publish_hint_certification))
                    .bottomStr(getString(R.string.cancel))
                    .isOutsideTouch(true)
                    .isFocus(true)
                    .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                    .with(getActivity())
                    .bottomClickListener(() -> mCertificationAlertPopWindow.hide())
                    .item2ClickListener(() -> {// 个人认证
                        mCertificationAlertPopWindow.hide();
                        if (mUserCertificationInfo != null // 待审核
                                && mUserCertificationInfo.getId() != 0
                                && SendCertificationBean.USER.equals(mUserCertificationInfo.getCertification_name())
                                && mUserCertificationInfo.getStatus() != UserCertificationInfo.CertifyStatusEnum.REJECTED.value) {
                            Intent intentToDetail = new Intent(getActivity(), CertificationDetailActivity.class);
                            Bundle bundleData = new Bundle();
                            bundleData.putInt(BUNDLE_DETAIL_TYPE, 0);
                            bundleData.putParcelable(BUNDLE_DETAIL_DATA, mUserCertificationInfo);
                            intentToDetail.putExtra(BUNDLE_DETAIL_TYPE, bundleData);
                            startActivity(intentToDetail);
                        } else {
                            Intent intent = new Intent(getActivity(), CertificationInputActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(BUNDLE_TYPE, 0);
                            intent.putExtra(BUNDLE_CERTIFICATION_TYPE, bundle);
                            startActivity(intent);
                        }
                    })
                    .item3ClickListener(() -> {// 企业认证
                        mCertificationAlertPopWindow.hide();
                        if (mUserCertificationInfo != null // 待审核
                                && mUserCertificationInfo.getId() != 0
                                && SendCertificationBean.ORG.equals(mUserCertificationInfo.getCertification_name())
                                && mUserCertificationInfo.getStatus() != UserCertificationInfo.CertifyStatusEnum.REJECTED.value) {

                            Intent intentToDetail = new Intent(getActivity(), CertificationDetailActivity.class);
                            Bundle bundleData = new Bundle();
                            bundleData.putInt(BUNDLE_DETAIL_TYPE, 1);
                            bundleData.putParcelable(BUNDLE_DETAIL_DATA, mUserCertificationInfo);
                            intentToDetail.putExtra(BUNDLE_DETAIL_TYPE, bundleData);
                            startActivity(intentToDetail);
                        } else {
                            Intent intent = new Intent(getActivity(), CertificationInputActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt(BUNDLE_TYPE, 1);
                            intent.putExtra(BUNDLE_CERTIFICATION_TYPE, bundle);
                            startActivity(intent);
                        }
                    })
                    .build();
        }
        if (mPayAlertPopWindow == null) {
            mPayAlertPopWindow = ActionPopupWindow.builder()
                    .item1Str(getString(R.string.info_publish_hint))
                    .item6Str(getString(R.string.info_publish_go_to_next))
                    .desStr(getString(R.string.info_publish_hint_pay, mPresenter.getSystemConfigBean().getNewsContribute().getPay_contribute(),
                            mPresenter.getGoldName()))
                    .bottomStr(getString(R.string.cancel))
                    .isOutsideTouch(true)
                    .isFocus(true)
                    .backgroundAlpha(1f)
                    .with(getActivity())
                    .bottomClickListener(() -> mPayAlertPopWindow.hide())
                    .item6ClickListener(() -> {
                        mPayAlertPopWindow.hide();
                        mPresenter.savePayTip(false);
                        startActivity(new Intent(getActivity(), EditeInfoDetailActivity.class));
                        closeActivity();
                    })
                    .build();
        }
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    public void closeActivity() {
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.animate_noting, R.anim.send_type_colse_fade_out);
    }

    private boolean checkVideoDraft(SendDynamicDataBean sendDynamicDataBean) {

        if (sendDynamicDataBean != null) {
            boolean hasVideo = FileUtils.isFileExists(sendDynamicDataBean.getVideoInfo().getPath());
//            boolean hasCover = FileUtils.isFileExists(sendDynamicDataBean.getVideoInfo().getCover());
            return hasVideo;
//            return hasCover && hasVideo;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        dismissPop(mCertificationAlertPopWindow);
        dismissPop(mPayAlertPopWindow);
        super.onDestroy();
    }

    private void initTypes() {

        SystemConfigBean systemConfigBean = SharePreferenceUtils.getObject(getContext(), SharePreferenceTagConfig
                .SHAREPREFERENCE_TAG_SYSTEM_BOOTSTRAPPERS);
        boolean unChecked = systemConfigBean != null  && systemConfigBean.getCheckin() != null && systemConfigBean.getCheckin().isStatus() && mType == SendDynamicDataBean.NORMAL_DYNAMIC;

        boolean qaOpend = systemConfigBean.getQuestionConfig().isStatus();
        if (mTopicBean != null) {
            mSelectDynamicParent.setPadding(getResources().getDimensionPixelOffset(R.dimen.spacing_small),
                    0, getResources().getDimensionPixelOffset(R.dimen.spacing_small)
                    , getResources().getDimensionPixelOffset(R.dimen.spacing_huge_100));
            mTypes.add(new ContentType(R.mipmap.ico_word, getString(R.string.send_words_dynamic)));
            mTypes.add(new ContentType(R.mipmap.ico_potoablum, getString(R.string.send_image_dynamic)));
            mTypes.add(new ContentType(R.mipmap.ico_video, getString(R.string.send_vidoe)));
        } else {
            mTypes.add(new ContentType(R.mipmap.ico_word, getString(R.string.send_words_dynamic)));
            mTypes.add(new ContentType(R.mipmap.ico_potoablum, getString(R.string.send_image_dynamic)));
            mTypes.add(new ContentType(R.mipmap.ico_contribute, getString(R.string.send_info)));
            if (unChecked) {
                mTypes.add(new ContentType(R.mipmap.ico_attendance, getString(R.string.check_in)));
            }
            if(qaOpend) {
                mTypes.add(new ContentType(R.mipmap.ico_question, getString(R.string.send_question)));
            }
            mTypes.add(new ContentType(R.mipmap.ico_fatie, getString(R.string.send_post)));
            mTypes.add(new ContentType(R.mipmap.ico_video, getString(R.string.send_vidoe)));

            }
    }

    class ContentType {
        int icoId;
        String name;

        public ContentType(int icoId, String name) {
            this.icoId = icoId;
            this.name = name;
        }
    }
}
