package com.zhiyicx.thinksnsplus.modules.information.publish.detail;

import android.content.Intent;
import android.os.Bundle;

import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.common.widget.popwindow.CustomPopupWindow;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.InfoDraftBean;
import com.zhiyicx.thinksnsplus.data.beans.InfoPublishBean;
import com.zhiyicx.thinksnsplus.modules.information.publish.addinfo.AddInfoActivity;
import com.zhiyicx.thinksnsplus.modules.markdown_editor.MarkdownFragment;

/**
 * @Author Jliuer
 * @Date 2018/01/18/9:33
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class EditeInfoDetailFragmentV2 extends MarkdownFragment<InfoDraftBean, EditeInfoDetailContract.Presenter>
        implements EditeInfoDetailContract.View {

    public static final String INFO_REFUSE = "info_refuse";
    public static InfoPublishBean mInfoPublishBean;

    /**
     * 提示信息弹窗
     */
    private ActionPopupWindow mInstructionsPopupWindow;

    /**
     * 取消提示选择弹框
     */
    private ActionPopupWindow mCanclePopupWindow;

    private boolean isRefuse;

    public static EditeInfoDetailFragmentV2 getInstance(Bundle bundle) {
        EditeInfoDetailFragmentV2 editeInfoDetailFragment = new EditeInfoDetailFragmentV2();
        editeInfoDetailFragment.setArguments(bundle);
        return editeInfoDetailFragment;
    }

    @Override
    public boolean needSetting() {
        return false;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.edit_info);
    }

    @Override
    protected String setRightTitle() {
        return getString(R.string.next);
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
    protected int setLeftImg() {
        return 0;
    }

    @Override
    protected void initBundleDataWhenOnCreate() {
        super.initBundleDataWhenOnCreate();
        if (getArguments() != null) {
            mInfoPublishBean = getArguments().getParcelable(INFO_REFUSE);
            if (mInfoPublishBean != null) {
                isRefuse = mInfoPublishBean.isRefuse();
            }
        }
    }

    @Override
    public void onAfterInitialLoad(boolean ready) {
        super.onAfterInitialLoad(ready);
        setDefalutContentPlaceHolder(getString(R.string.info_editor_default_content));
        setDefalutTitlePlaceHolder(getString(R.string.info_title_hint));
    }

    @Override
    protected boolean preHandlePublish() {
        if (mInfoPublishBean == null) {
            mInfoPublishBean = new InfoPublishBean();
            mInfoPublishBean.setMark(Long.parseLong(AppApplication.getmCurrentLoginAuth().getUser_id() + "" + System
                    .currentTimeMillis()));
        }
        return true;
    }

    @Override
    protected void handlePublish(String title, String markdwon, String noMarkdown, String html) {
        super.handlePublish(title, markdwon, noMarkdown, html);
        mInfoPublishBean.setContent(markdwon);
        mInfoPublishBean.setText_content(noMarkdown);
        if (mPresenter == null) {
            showSnackErrorMessage(getString(R.string.handle_fail));
            return;
        }
        try{
            mInfoPublishBean.setAmout(mPresenter.getSystemConfigBean().getNewsContribute().getPay_contribute());
        }catch (Exception ignored){}

        // 封面
        int cover;
        if (isRefuse) {
            cover = mInfoPublishBean.getCover();
        } else {
            cover = RegexUtils.getImageId(markdwon);
        }
        mInfoPublishBean.setCover(cover);
        mInfoPublishBean.setImage(cover < 0 ? null : (long) cover);

        mInfoPublishBean.setTitle(title);
        Intent intent = new Intent(getActivity(), AddInfoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void initEditWarningPop(String title, String markdown, String noMarkdown,String html) {
        if (mCanclePopupWindow != null) {
            mCanclePopupWindow.show();
            return;
        }
        mCanclePopupWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.dynamic_send_cancel_hint))
                .item2Str(getString(R.string.determine))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(0.8f)
                .with(getActivity())
                .item2ClickListener(() -> {
                    mInfoPublishBean = null;
                    mCanclePopupWindow.hide();
                    mActivity.finish();
                })
                .bottomClickListener(() -> mCanclePopupWindow.hide()).build();
        mCanclePopupWindow.show();
    }

    @Override
    protected void cancleEdit() {
        super.cancleEdit();
        mInfoPublishBean = null;

    }

    @Override
    protected void saveDraft(String title, String html, String noMarkdown) {
        super.saveDraft(title, html, noMarkdown);
        InfoDraftBean infoDraftBean = new InfoDraftBean();
        if (mInfoPublishBean == null) {
            mInfoPublishBean = new InfoPublishBean();
            mInfoPublishBean.setMark(Long.parseLong(AppApplication.getmCurrentLoginAuth().getUser_id() + "" + System
                    .currentTimeMillis()));
            mInfoPublishBean.setCreate_at(TimeUtils.getCurrenZeroTimeStr());
        }
        mInfoPublishBean.setContent(html);
        mInfoPublishBean.setTitle(title);
        mInfoPublishBean.setText_content(noMarkdown);
        infoDraftBean.setMark(mInfoPublishBean.getMark());
        infoDraftBean.setCreate_at(mInfoPublishBean.getCreate_at());
        infoDraftBean.setInfoPublishBean(mInfoPublishBean);
        mPresenter.saveDraft(infoDraftBean);
        mInfoPublishBean = null;
    }

    /**
     * 这里取消图片张数限制
     */
    @Override
    protected void initPhotoPopupWindow() {
//        if (mInsertedImages.size() + mFailedImages.size() >= 9) {
//            initInstructionsPop(getString(R.string.instructions), String.format(Locale.getDefault
//                    (), getString(R.string.choose_max_photos), 9));
//            return;
//        }
        super.initPhotoPopupWindow();
    }


    protected void initInstructionsPop(String title, String des) {
        if (mInstructionsPopupWindow != null) {
            mInstructionsPopupWindow.newBuilder().item1Str(title).desStr(des);
            mInstructionsPopupWindow.show();
            return;
        }
        mInstructionsPopupWindow = ActionPopupWindow.builder()
                .item1Str(title)
                .desStr(des)
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(CustomPopupWindow.POPUPWINDOW_ALPHA)
                .with(getActivity())
                .bottomClickListener(() -> {
                    mInstructionsPopupWindow.hide();
                })
                .build();
        mInstructionsPopupWindow.show();
    }

    @Override
    protected InfoDraftBean getDraftData() {
        mDraftBean = new InfoDraftBean();
        if (mInfoPublishBean != null) {
            mDraftBean.setCreate_at(mInfoPublishBean.getCreate_at());
            mDraftBean.setMark(mInfoPublishBean.getMark());
            mDraftBean.setInfoPublishBean(mInfoPublishBean);
            mDraftBean.setTitle(mInfoPublishBean.getTitle());
            mDraftBean.setHtml(isRefuse ? getHtml(mDraftBean.getTitle(),
                    pareseBody(mInfoPublishBean.getContent())) : mInfoPublishBean.getContent());
            if (!isRefuse) {
                onInputListener(1, 1);
            }
        }
        return mInfoPublishBean == null ? null : mDraftBean;
    }

    @Override
    protected void loadDraft(InfoDraftBean postDraftBean) {
        super.loadDraft(postDraftBean);
        mRichTextView.loadDraft("", mDraftBean.getHtml());
    }

    @Override
    protected void pareseBodyResult() {
        mDraftBean.setFailedImages(mFailedImages);
        mDraftBean.setInsertedImages(mInsertedImages);
        mDraftBean.setImages(mImages);
    }
}
