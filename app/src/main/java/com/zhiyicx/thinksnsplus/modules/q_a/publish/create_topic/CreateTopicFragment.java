package com.zhiyicx.thinksnsplus.modules.q_a.publish.create_topic;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trycatch.mysnackbar.Prompt;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.impl.photoselector.DaggerPhotoSelectorImplComponent;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSelectorImpl;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSeletorImplModule;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.config.ConstantConfig;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.widget.UserInfoInroduceInputView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;

/**
 * @Author Jliuer
 * @Date 2017/09/15/9:49
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CreateTopicFragment extends TSFragment<CreateTopicContract.Presenter>
        implements CreateTopicContract.View, PhotoSelectorImpl.IPhotoBackListener {

    @BindView(R.id.et_topic_title)
    UserInfoInroduceInputView mEtTopicTitle;
    @BindView(R.id.et_topic_desc)
    UserInfoInroduceInputView mEtTopicDesc;
    @BindView(R.id.tv_choose_topic_cover)
    ImageView mChooseTopicCover;

    private ActionPopupWindow mPhotoPopupWindow;
    private PhotoSelectorImpl mPhotoSelector;

    public static CreateTopicFragment getInstance() {
        return new CreateTopicFragment();
    }

    @Override
    protected String setRightTitle() {
        return getString(R.string.submit);
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.create_topic);
    }

    @Override
    protected void setRightClick() {
        super.setRightClick();
        mPresenter.createTopic(mEtTopicTitle.getInputContent(), mEtTopicDesc.getInputContent());
    }

    @Override
    protected void snackViewDismissWhenTimeOut(Prompt prompt) {
        super.snackViewDismissWhenTimeOut(prompt);
        if (prompt == Prompt.SUCCESS && getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    protected void initView(View rootView) {
        Observable.combineLatest(RxTextView.textChanges(mEtTopicTitle.getEtContent()),
                RxTextView.textChanges(mEtTopicDesc.getEtContent()),
                (charSequence, charSequence2) -> charSequence.length() * charSequence2.length() > 0)
                .subscribe(aBoolean -> mToolbarRight.setEnabled(aBoolean));

        RxView.clicks(mChooseTopicCover)
                .throttleFirst(ConstantConfig.JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> initPhotoPopupWindow());
    }

    @Override
    protected boolean usePermisson() {
        return true;
    }

    @Override
    protected void initData() {
        mPhotoSelector = DaggerPhotoSelectorImplComponent
                .builder()
                .photoSeletorImplModule(new PhotoSeletorImplModule(this, this, PhotoSelectorImpl
                        .NO_CRAFT))
                .build().photoSelectorImpl();
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_createtopic;
    }

    /**
     * 初始化图片选择弹框
     */
    private void initPhotoPopupWindow() {
        DeviceUtils.hideSoftKeyboard(getActivity(), getActivity().getWindow().getDecorView());
        if (mPhotoPopupWindow != null) {
            mPhotoPopupWindow.show();
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
        mPhotoPopupWindow.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPhotoSelector.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void getPhotoSuccess(List<ImageBean> photoList) {
        if (photoList.isEmpty()) {
            return;
        }
    }

    @Override
    public void getPhotoFailure(String errorMsg) {

    }
}
