package com.zhiyicx.thinksnsplus.modules.circle.detailv2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.zhiyi.richtexteditorlib.view.dialogs.LinkDialog;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.TouristConfig;
import com.zhiyicx.baseproject.impl.photoselector.ImageBean;
import com.zhiyicx.baseproject.impl.photoselector.PhotoSelectorImpl;
import com.zhiyicx.baseproject.impl.share.ShareModule;
import com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow;
import com.zhiyicx.baseproject.widget.popwindow.PayPopWindow;
import com.zhiyicx.common.BuildConfig;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.UIUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.AnimationRectBean;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.beans.CircleJoinedBean;
import com.zhiyicx.thinksnsplus.data.beans.CircleMembers;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostCommentBean;
import com.zhiyicx.thinksnsplus.data.beans.CirclePostListBean;
import com.zhiyicx.thinksnsplus.data.beans.UserInfoBean;
import com.zhiyicx.thinksnsplus.data.beans.circle.CircleZipBean;
import com.zhiyicx.thinksnsplus.data.beans.report.ReportResourceBean;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseCircleRepository;
import com.zhiyicx.thinksnsplus.i.OnUserInfoClickListener;
import com.zhiyicx.thinksnsplus.modules.aaaat.AtUserActivity;
import com.zhiyicx.thinksnsplus.modules.aaaat.AtUserListFragment;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListBaseItem;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForEightImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForFiveImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForFourImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForNineImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForOneImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForSevenImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForSixImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForThreeImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForTwoImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.adapter.CirclePostListItemForZeroImage;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.post.CirclePostDetailActivity;
import com.zhiyicx.thinksnsplus.modules.circle.pre.PreCircleActivity;
import com.zhiyicx.thinksnsplus.modules.gallery.GalleryActivity;
import com.zhiyicx.thinksnsplus.modules.personal_center.PersonalCenterFragment;
import com.zhiyicx.thinksnsplus.modules.report.ReportActivity;
import com.zhiyicx.thinksnsplus.modules.report.ReportType;
import com.zhiyicx.thinksnsplus.modules.wallet.sticktop.StickTopFragment;
import com.zhiyicx.thinksnsplus.widget.comment.CirclePostListCommentView;
import com.zhiyicx.thinksnsplus.widget.comment.CirclePostNoPullRecyclerView;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.zhiyicx.baseproject.widget.popwindow.ActionPopupWindow.POPUPWINDOW_ALPHA;
import static com.zhiyicx.thinksnsplus.modules.dynamic.list.DynamicFragment.ITEM_SPACING;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/12/6
 * @Contact master.jungle68@gmail.com
 */
public class BaseCircleDetailFragment extends TSListFragment<CircleDetailContract.Presenter, CirclePostListBean>
        implements CircleDetailContract.View, CirclePostListBaseItem.OnReSendClickListener,
        CirclePostNoPullRecyclerView.OnCommentStateClickListener<CirclePostCommentBean>,
        CirclePostListCommentView.OnCommentClickListener,
        CirclePostListBaseItem.OnMenuItemClickLisitener, CirclePostListBaseItem.OnImageClickListener, OnUserInfoClickListener,
        CirclePostListCommentView.OnMoreCommentClickListener, MultiItemTypeAdapter.OnItemClickListener
        , PhotoSelectorImpl.IPhotoBackListener, CirclePostListBaseItem.OnPostFromClickListener {

    public static final String CIRCLE_ID = "circle_id";
    public static final String CIRCLE = "circle";
    public static final String CIRCLE_TYPE = "circle_type";

    @Inject
    CircleDetailPresenter mCircleDetailPresenter;

    private ActionPopupWindow mDeletCommentPopWindow;
    private ActionPopupWindow mReSendCommentPopWindow;

    private ActionPopupWindow mOtherPostPopWindow;
    private ActionPopupWindow mMyPostPopWindow;
    private PayPopWindow mPayPopWindow;

    private int mCurrentPostion;// 当前评论的动态位置
    private long mReplyToUserId;// 被评论者的 id

    protected BaseCircleRepository.CircleMinePostType mCircleMinePostType = BaseCircleRepository.CircleMinePostType.PUBLISH;
    protected CircleZipBean mCircleZipBean;
    private Subscription showComment;
    private CirclePostCommentBean mCirclePostCommentBean;


    public static BaseCircleDetailFragment newInstance(BaseCircleRepository.CircleMinePostType circleMinePostType) {
        BaseCircleDetailFragment circleDetailFragment = new BaseCircleDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CIRCLE_TYPE, circleMinePostType);
        circleDetailFragment.setArguments(bundle);
        return circleDetailFragment;
    }

    public static BaseCircleDetailFragment newInstance(BaseCircleRepository.CircleMinePostType circleMinePostType,
                                                       Long id) {
        BaseCircleDetailFragment circleDetailFragment = new BaseCircleDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(CircleDetailFragment.CIRCLE_ID, id);
        bundle.putSerializable(CIRCLE_TYPE, circleMinePostType);
        circleDetailFragment.setArguments(bundle);
        return circleDetailFragment;
    }

    @Override
    protected boolean setUseInputCommentView() {
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCircleMinePostType = (BaseCircleRepository.CircleMinePostType) getArguments().getSerializable(CIRCLE_TYPE);
        }
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_base_circle_detail;
    }

    @Override
    protected boolean setUseSatusbar() {
        return false;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    /**
     * 点击 来自 xxx ，可以跳转到相应圈子
     *
     * @return
     */
    protected boolean canGotoCircle() {
        return true;
    }

    @Override
    protected float getItemDecorationSpacing() {
        return ITEM_SPACING;
    }

    @Override
    public CircleZipBean getCircleZipBean() {
        return mCircleZipBean;
    }

    @Override
    public void scrollToTop() {
        mRvList.scrollToPosition(0);
    }

    @Override
    public Long getCircleId() {
        if (getArguments() == null || getArguments().getLong(CIRCLE_ID) == 0) {
            return null;
        }
        return getArguments().getLong(CIRCLE_ID);
    }

    @Override
    public void allDataReady(CircleZipBean circleZipBean) {
        mCircleZipBean = circleZipBean;
        closeLoadingView();
    }

    @Override
    public String getType() {
        return "";
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        MultiItemTypeAdapter adapter = new MultiItemTypeAdapter<>(getContext(), mListDatas);
        setAdapter(adapter, new CirclePostListItemForZeroImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForOneImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForTwoImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForThreeImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForFourImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForFiveImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForSixImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForSevenImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForEightImage(getContext()));
        setAdapter(adapter, new CirclePostListItemForNineImage(getContext()));
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);


        initDagger();
    }

    @Override
    protected boolean setUseShadowView() {
        return true;
    }

    @Override
    protected void onShadowViewClick() {
        closeInputView();
    }

    protected void initDagger() {
        Observable.create(subscriber -> {

            DaggerCircleDetailComponent
                    .builder()
                    .appComponent(AppApplication.AppComponentHolder.getAppComponent())
                    .circleDetailPresenterModule(new CircleDetailPresenterModule(BaseCircleDetailFragment.this))
                    .shareModule(new ShareModule(mActivity))
                    .build()
                    .inject(BaseCircleDetailFragment.this);

            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io())
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

    protected void closeInputView() {
        if (mIlvComment.getVisibility() == View.VISIBLE) {
            mIlvComment.setVisibility(View.GONE);
            DeviceUtils.hideSoftKeyboard(mActivity, mIlvComment.getEtContent());
        }
        mVShadow.setVisibility(View.GONE);
    }

    @Override
    public void onUserInfoClick(UserInfoBean userInfoBean) {
        PersonalCenterFragment.startToPersonalCenter(getContext(), userInfoBean);
    }

    @Override
    public void onCommentStateClick(CirclePostCommentBean dynamicCommentBean, int position) {
        initReSendCommentPopupWindow(dynamicCommentBean, mListDatas.get(mPresenter.getCurrenPosiotnInDataList((long) dynamicCommentBean.getPost_id
                ())).getId());
        mReSendCommentPopWindow.show();
    }

    @Override
    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
        CirclePostDetailActivity.startCirclePostDetailActivity(getActivity(), mListDatas.get(position)
                .getGroup_id(), mListDatas.get(position).getId(), false, canGotoCircle());
    }

    @Override
    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
        return false;
    }

    @Override
    public void onMoreCommentClick(View view, CirclePostListBean dynamicBean) {
        int position = mPresenter.getCurrenPosiotnInDataList(dynamicBean.getId());
        CirclePostDetailActivity.startCirclePostDetailActivity(getActivity(), mListDatas.get(position)
                .getGroup_id(), mListDatas.get(position).getId(), true, canGotoCircle());
    }

    @Override
    public boolean isFromMine() {
        return false;
    }

    @Override
    public boolean isOutsideSerach() {
        return true;
    }

    @Override
    public void onCommentUserInfoClick(UserInfoBean userInfoBean) {
        onUserInfoClick(userInfoBean);
    }

    @Override
    public void onCommentContentClick(CirclePostListBean dynamicBean, int position) {
        mCurrentPostion = mPresenter.getCurrenPosiotnInDataList(dynamicBean.getId());
        CircleInfo mCircleInfo = dynamicBean.getGroup();
        boolean isJoined = mCircleInfo.getJoined() != null && mCircleInfo.getJoined().getAudit() == CircleJoinedBean.AuditStatus.PASS.value;

        boolean isBlackList = isJoined && CircleMembers.BLACKLIST.equals(mCircleInfo.getJoined().getRole());

        if (dynamicBean.getComments().get(position).getUser_id() == AppApplication.getmCurrentLoginAuth().getUser_id()) {
            initDeletCommentPopWindow(dynamicBean, mCurrentPostion, position, isBlackList);
            mDeletCommentPopWindow.show();
        } else {
            if (isBlackList) {
                showAuditTipPopupWindow(getString(R.string.circle_member_added_blacklist));
                return;
            }
            if (!isJoined) {
                showAuditTipPopupWindow(getString(R.string.circle_member_comment_join));
                return;
            }
            showCommentView();
            mReplyToUserId = dynamicBean.getComments().get(position).getUser_id();
            String contentHint = getString(R.string.default_input_hint);
            if (dynamicBean.getComments().get(position).getReply_to_user_id() != dynamicBean.getUser_id()) {
                contentHint = getString(R.string.reply, dynamicBean.getComments().get(position).getCommentUser().getName());
            }
            mIlvComment.setEtContentHint(contentHint);
        }
    }

    @Override
    public void onCommentContentLongClick(CirclePostListBean dynamicBean, int position) {

        if (mPresenter.handleTouristControl()) {
            return;
        }
        CircleInfo mCircleInfo = dynamicBean.getGroup();
        boolean isJoined = mCircleInfo.getJoined() != null && mCircleInfo.getJoined().getAudit() == CircleJoinedBean.AuditStatus.PASS.value;

        boolean isBlackList = isJoined && CircleMembers.BLACKLIST.equals(mCircleInfo.getJoined().getRole());

        if (isBlackList) {
            showAuditTipPopupWindow(getString(R.string.circle_member_added_blacklist));
            return;
        }

        mCurrentPostion = mPresenter.getCurrenPosiotnInDataList(dynamicBean.getId());

        boolean isNormalMember = isJoined && CircleMembers.MEMBER.equals(mCircleInfo.getJoined().getRole());
        boolean isManager = false;
        if (mCircleInfo.getJoined() != null) {
            isManager = CircleMembers.FOUNDER.equals(mCircleInfo.getJoined().getRole()) ||
                    CircleMembers.ADMINISTRATOR.equals(mCircleInfo.getJoined().getRole());
        }

        boolean isMine = dynamicBean.getComments().get(position).getUser_id() == AppApplication.getMyUserIdWithdefault();

        // 举报
        if (!isMine && isNormalMember) {
            ReportActivity.startReportActivity(mActivity, new ReportResourceBean(dynamicBean.getComments().get
                    (position).getCommentUser(), dynamicBean.getComments().get
                    (position).getId().toString(),
                    null, "", dynamicBean.getComments().get(position).getContent(), ReportType.CIRCLE_COMMENT));

        } else {
            // 删除
            initDeletCommentPopWindow(dynamicBean, mCurrentPostion, position, !isMine);
            mDeletCommentPopWindow.show();
        }
    }

    protected void showCommentView() {
        // 评论
        mIlvComment.setVisibility(View.VISIBLE);
        mIlvComment.setSendButtonVisiable(true);
        mIlvComment.getFocus();
        mVShadow.setVisibility(View.VISIBLE);
        DeviceUtils.showSoftKeyboard(getActivity(), mIlvComment.getEtContent());
    }

    @Override
    public void onSendClick(View v, String text) {
        DeviceUtils.hideSoftKeyboard(getContext(), v);
        mIlvComment.setVisibility(View.GONE);
        mVShadow.setVisibility(View.GONE);
        mPresenter.sendComment(mCurrentPostion, mReplyToUserId, text);
    }

    @Override
    public void onAtTrigger() {
        AtUserActivity.startAtUserActivity(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AtUserListFragment.REQUES_USER) {
            // @ 选人返回
            if (data != null && data.getExtras() != null) {
                UserInfoBean userInfoBean = data.getExtras().getParcelable(AtUserListFragment.AT_USER);
                if (userInfoBean != null) {
                    mIlvComment.appendAt(userInfoBean.getName());
                }
            }
            showComment = Observable.timer(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> showCommentView());
        }
        if (requestCode == StickTopFragment.PINNED) {
            if (mCirclePostCommentBean != null) {
                mCirclePostCommentBean.setPinned(true);
                refreshData();
            }
        }
    }

    @Override
    public void getPhotoSuccess(List<ImageBean> photoList) {

    }

    @Override
    public void getPhotoFailure(String errorMsg) {

    }

    @Override
    public void loadAllError() {
        setLoadViewHolderImag(R.mipmap.img_default_internet);
        showLoadViewLoadError();
    }

    @Override
    public void updateCircleInfo(CircleInfo circleInfo, boolean needFinish) {

    }

    @Override
    public void onPostFromClick(int position) {
        CircleInfo circleInfo = mListDatas.get(position).getGroup();
        boolean isClosedCircle = CircleInfo.CirclePayMode.PAID.value.equals(circleInfo.getMode())
                || CircleInfo.CirclePayMode.PRIVATE.value.equals(circleInfo.getMode());
        boolean isJoined = circleInfo.getJoined() != null && circleInfo.getJoined().getAudit() == CircleJoinedBean.AuditStatus.PASS.value;

        if (isClosedCircle && !isJoined) {
            PreCircleActivity.startPreCircleActivity(mActivity, circleInfo.getId());
        } else {
            CircleDetailActivity.startCircleDetailActivity(mActivity, circleInfo.getId());
        }
    }

    @Override
    public void onImageClick(ViewHolder holder, CirclePostListBean circlePostListBean, int position) {
        if (!TouristConfig.DYNAMIC_BIG_PHOTO_CAN_LOOK && mPresenter.handleTouristControl()) {
            return;
        }
        List<CirclePostListBean.ImagesBean> task = circlePostListBean.getImages();
        List<ImageBean> imageBeanList = new ArrayList<>();
        ArrayList<AnimationRectBean> animationRectBeanArrayList
                = new ArrayList<>();
        for (int i = 0; i < task.size(); i++) {
            if (i >= 9) {
                continue;
            }
            int id = UIUtils.getResourceByName("siv_" + i, "id", getContext());
            ImageView imageView = holder.getView(id);
            ImageBean imageBean = new ImageBean();
            imageBean.setStorage_id(task.get(i).getFile_id());
            imageBean.setListCacheUrl(task.get(i).getGlideUrl());
            imageBean.setWidth(task.get(i).getWidth());
            imageBean.setHeight(task.get(i).getHeight());
            imageBean.setImgMimeType(task.get(i).getImgMimeType());
            imageBeanList.add(imageBean);
            AnimationRectBean rect = AnimationRectBean.buildFromImageView(imageView);
            animationRectBeanArrayList.add(rect);
        }

        GalleryActivity.startToGallery(getContext(), position, imageBeanList,
                animationRectBeanArrayList);
    }

    @Override
    public void onMenuItemClick(View view, int dataPosition, int viewPosition) {

        dataPosition -= mHeaderAndFooterWrapper.getHeadersCount();// 减去 header
        mCurrentPostion = dataPosition;
        CircleInfo mCircleInfo = mListDatas.get(dataPosition).getGroup();
        boolean isClosedCircle = CircleInfo.CirclePayMode.PAID.value.equals(mCircleInfo.getMode())
                || CircleInfo.CirclePayMode.PRIVATE.value.equals(mCircleInfo.getMode());
        boolean isJoined = mCircleInfo.getJoined() != null && mCircleInfo.getJoined().getAudit() == CircleJoinedBean.AuditStatus.PASS.value;

        boolean isBlackList = isJoined && CircleMembers.BLACKLIST.equals(mCircleInfo.getJoined().getRole());
        boolean canNotDeal;
        switch (viewPosition) { // 0 1 2 3 代表 view item 位置
            // 喜欢
            case 0:
                if (isClosedCircle && !isJoined) {
                    showAuditTipPopupWindow(getString(R.string.circle_member_like_join));
                    return;
                }
                if (isBlackList) {
                    showAuditTipPopupWindow(getString(R.string.circle_member_added_blacklist));
                    return;
                }
                // 还未发送成功的动态列表不查看详情
                canNotDeal = (!TouristConfig.DYNAMIC_CAN_DIGG && mPresenter.handleTouristControl()) ||
                        mListDatas.get(dataPosition).getId() == null || mListDatas.get
                        (dataPosition).getId() == 0;
                if (canNotDeal) {
                    return;
                }
                handleLike(dataPosition);
                break;
            // 评论
            case 1:
                if (isBlackList) {
                    showAuditTipPopupWindow(getString(R.string.circle_member_added_blacklist));
                    return;
                }
                if (!isJoined) {
                    showAuditTipPopupWindow(getString(R.string.circle_member_comment_join));
                    return;
                }
                canNotDeal = (!TouristConfig.DYNAMIC_CAN_COMMENT && mPresenter.handleTouristControl()) ||
                        mListDatas.get(dataPosition).getId() == null || mListDatas.get
                        (dataPosition).getId() == 0;
                if (canNotDeal) {
                    return;
                }
                showCommentView();
                mIlvComment.setEtContentHint(getString(R.string.default_input_hint));
                mCurrentPostion = dataPosition;
                mReplyToUserId = 0;// 0 代表评论动态
                break;

            case 2: // 浏览
                onItemClick(null, null, (dataPosition)); // 加上 header
                break;

            case 3: // 更多
                Bitmap shareBitMap = null;
                try {
                    ImageView imageView = (ImageView) layoutManager.findViewByPosition
                            (dataPosition + mHeaderAndFooterWrapper.getHeadersCount()).findViewById(R.id.siv_0);
                    shareBitMap = ConvertUtils.drawable2BitmapWithWhiteBg(getContext(), imageView
                            .getDrawable(), R.mipmap.icon);
                } catch (Exception e) {
                    LogUtils.d("have't image");
                }
                int userId = mListDatas.get(dataPosition).getUser_id().intValue();
                int currentId = (int) AppApplication.getMyUserIdWithdefault();
                if (userId == currentId) {
                    initMyPostPopupWindow(mListDatas.get(dataPosition), dataPosition, mListDatas.get(dataPosition)
                            .hasCollected(), shareBitMap);
                } else {
                    if (isBlackList) {
                        showAuditTipPopupWindow(getString(R.string.circle_member_added_blacklist));
                        return;
                    }
                    if (isClosedCircle && !isJoined) {
                        showAuditTipPopupWindow(getString(R.string.circle_member_handle_join));
                        return;
                    }
                    initOtherDynamicPopupWindow(mListDatas.get(dataPosition), dataPosition, mListDatas.get(dataPosition)
                            .hasCollected(), shareBitMap);
                }
                break;
            default:
                onItemClick(null, null, (dataPosition)); // 加上 header
        }
    }

    @Override
    public void onReSendClick(int position) {

    }

    /**
     * 初始化重发评论选择弹框
     */
    private void initReSendCommentPopupWindow(final CirclePostCommentBean commentBean, final long feed_id) {
        mReSendCommentPopWindow = ActionPopupWindow.builder()
                .item1Str(getString(R.string.dynamic_list_resend_comment))
                .item1Color(ContextCompat.getColor(getContext(), R.color.themeColor))
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(getActivity())
                .item1ClickListener(() -> {
                    mReSendCommentPopWindow.hide();
                    //mCurrentPostion, mReplyToUserId, text
                    mPresenter.reSendComment(commentBean, feed_id);
                })
                .bottomClickListener(() -> mReSendCommentPopWindow.hide())
                .build();
    }

    /**
     * 初始化评论删除选择弹框
     *
     * @param circlePostListBean curent dynamic
     * @param dynamicPositon     dynamic comment position
     * @param commentPosition    current comment position
     */
    private void initDeletCommentPopWindow(final CirclePostListBean circlePostListBean, final int dynamicPositon, final int commentPosition,
                                           boolean isBlackList) {
        boolean sourceIsMine = AppApplication.getMyUserIdWithdefault() == circlePostListBean.getUser_id();
        mDeletCommentPopWindow = ActionPopupWindow.builder()
                .item2Str(getString(R.string.dynamic_list_delete_comment))
                .item1Str(!isBlackList && BuildConfig.USE_TOLL && circlePostListBean
                        .getComments().get(commentPosition).getId() != -1L && !circlePostListBean
                        .getComments().get(commentPosition).getPinned() ? getString(
                        sourceIsMine ? R.string.dynamic_list_stick_top_comment : R.string.dynamic_list_top_comment) : null)
                .bottomStr(getString(R.string.cancel))
                .isOutsideTouch(true)
                .isFocus(true)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .with(mActivity)
                .item1ClickListener(() -> {
                    mDeletCommentPopWindow.hide();
                    showBottomView(true);
                    mCirclePostCommentBean = circlePostListBean.getComments().get(commentPosition);
                    StickTopFragment.startSticTopActivity(this, StickTopFragment.TYPE_POST, circlePostListBean.getId(), mCirclePostCommentBean.getId(), sourceIsMine);

                })
                .item2ClickListener(() -> {
                    mDeletCommentPopWindow.hide();
                    showDeleteTipPopupWindow(getString(R.string.delete_comment), () -> {
                        mPresenter.deleteComment(circlePostListBean, dynamicPositon, circlePostListBean.getComments().get(commentPosition).getId(),
                                commentPosition);
                        showBottomView(true);
                    }, true);
                })
                .bottomClickListener(() -> mDeletCommentPopWindow.hide())
                .build();
    }

    /**
     * 初始化我的帖子操作弹窗
     *
     * @param circlePostListBean curent post
     * @param position           curent post postion
     */
    private void initMyPostPopupWindow(final CirclePostListBean circlePostListBean, final int position, boolean isCollected,
                                       final Bitmap shareBitMap) {

        CircleInfo mCircleInfo = circlePostListBean.getGroup();
        boolean isClosedCircle = CircleInfo.CirclePayMode.PAID.value.equals(mCircleInfo.getMode())
                || CircleInfo.CirclePayMode.PRIVATE.value.equals(mCircleInfo.getMode());
        boolean isPinned = circlePostListBean.getPinned() || BaseCircleRepository.CircleMinePostType.HAD_PINNED == mCircleMinePostType;
        boolean isBlackList = mCircleInfo.getJoined() != null && CircleMembers.BLACKLIST.equals(mCircleInfo.getJoined().getRole());
        if (!isBlackList) {
            mPresenter.sharePost(circlePostListBean, shareBitMap);
        } else {
            mMyPostPopWindow = ActionPopupWindow.builder()
                    .item1Str(getString(R.string.empty))
                    .item2Str(getString(R.string.empty))
                    .item3Str(null)
                    .item5Str(getString(R.string.delete_post))
                    .bottomStr(getString(R.string.cancel))
                    .isOutsideTouch(true)
                    .isFocus(true)
                    .backgroundAlpha(POPUPWINDOW_ALPHA)
                    .with(mActivity)
                    .item2ClickListener(() -> {
                        // 收藏
                        handleCollect(position);
                        mMyPostPopWindow.hide();
                        showBottomView(true);
                    })
                    .item3ClickListener(() -> {
                        // 置顶
                        mMyPostPopWindow.hide();
                        showBottomView(true);
                        StickTopFragment.startSticTopActivity(mActivity, StickTopFragment.TYPE_POST, circlePostListBean.getId());
                    })
                    .item4ClickListener(() -> {
                        // 管理员置顶操作
                        if (isPinned) {
                            mPresenter.undoTopPost(circlePostListBean.getId(), position);
                        } else {
                            managerStickTop(circlePostListBean.getId(), position);
                        }
                        mMyPostPopWindow.hide();
                        showBottomView(true);
                    })
                    .item5ClickListener(() -> {
                        // 删除
                        mMyPostPopWindow.hide();
                        showDeleteTipPopupWindow(getString(R.string.delete_post), true, circlePostListBean, position);
                        showBottomView(true);
                    })
                    .item1ClickListener(() -> {
                        // 分享
                        mPresenter.sharePost(circlePostListBean, shareBitMap);
                        mMyPostPopWindow.hide();
                    })
                    .bottomClickListener(() -> {
                        //取消
                        mMyPostPopWindow.hide();
                        showBottomView(true);
                    })
                    .build();
        }
    }

    @Override
    public void showBottomView(boolean isShow) {
        if (isShow) {
            mVShadow.setVisibility(View.GONE);
            mIlvComment.setVisibility(View.GONE);
            mIlvComment.clearFocus();
            mIlvComment.setSendButtonVisiable(false);
            DeviceUtils.hideSoftKeyboard(getActivity(), mIlvComment.getEtContent());
        } else {
            mVShadow.setVisibility(View.VISIBLE);
            mIlvComment.setVisibility(View.VISIBLE);
            mIlvComment.getFocus();
            mIlvComment.setSendButtonVisiable(true);
            DeviceUtils.showSoftKeyboard(getActivity(), mIlvComment.getEtContent());
        }
    }

    /**
     * 喜欢
     *
     * @param dataPosition
     */
    private void handleLike(int dataPosition) {
        // 先更新界面，再后台处理
        mListDatas.get(dataPosition).setLiked(!mListDatas.get(dataPosition).hasLiked());
        mListDatas.get(dataPosition).setLikes_count(!mListDatas.get(dataPosition).hasLiked() ?
                mListDatas.get(dataPosition).getLikes_count() - 1 : mListDatas.get(dataPosition).getLikes_count() + 1);
        refreshData(dataPosition);
        mPresenter.handleLike(mListDatas.get(dataPosition).hasLiked(), mListDatas.get(dataPosition).getId(), dataPosition);
    }

    /**
     * 初始化他人动态操作选择弹框
     *
     * @param circlePostListBean curent dynamic
     */
    private void initOtherDynamicPopupWindow(final CirclePostListBean circlePostListBean, int position, boolean isCollected, final
    Bitmap shareBitmap) {
        mPresenter.sharePost(circlePostListBean, shareBitmap);
    }

    /**
     * 收藏
     *
     * @param dataPosition
     */
    @Override
    public void handleCollect(int dataPosition) {
        // 先更新界面，再后台处理
        mPresenter.handleCollect(mListDatas.get(dataPosition));
        boolean is_collection = mListDatas.get(dataPosition).hasCollected();// 旧状态
        mListDatas.get(dataPosition).setCollected(!is_collection);
        refreshData(dataPosition);
    }

    @Override
    public void handleExcellent(int dataPosition) {
        // 先更新界面，再后台处理
        mPresenter.handleExcellent(mListDatas.get(dataPosition));
        boolean is_excellent = mListDatas.get(dataPosition).getExcellent_at() != null;
        mListDatas.get(dataPosition).setExcellent_at(is_excellent ? null : mListDatas.get(dataPosition).getCreated_at());
        refreshData(dataPosition);
    }

    protected void setAdapter(MultiItemTypeAdapter adapter, CirclePostListBaseItem circlePostListBaseItem) {
        circlePostListBaseItem.setOnImageClickListener(this);
        circlePostListBaseItem.setOnUserInfoClickListener(this);
        circlePostListBaseItem.setOnMenuItemClickLisitener(this);
        circlePostListBaseItem.setOnReSendClickListener(this);
        circlePostListBaseItem.setOnMoreCommentClickListener(this);
        circlePostListBaseItem.setOnCommentClickListener(this);
        circlePostListBaseItem.setOnCommentStateClickListener(this);
        circlePostListBaseItem.setOnPostFromClickListener(this);
        circlePostListBaseItem.setShowPostExcelentTag(!BaseCircleRepository.CircleMinePostType.EXCELLENT.equals(mCircleMinePostType));
        circlePostListBaseItem.setShowPostFrom(showPostFrom());
        circlePostListBaseItem.setShowCommentList(showCommentList());
        circlePostListBaseItem.setShowToolMenu(showToolMenu());
        adapter.addItemViewDelegate(circlePostListBaseItem);
    }

    /**
     * 是否显示帖子来原
     *
     * @return
     */
    protected boolean showPostFrom() {
        return false;
    }

    protected boolean showToolMenu() {
        return true;
    }

    protected boolean showCommentList() {
        return true;
    }

    @Override
    public boolean isNeedHeaderInfo() {
        return false;
    }

    @Override
    public BaseCircleRepository.CircleMinePostType getCircleMinePostType() {
        return mCircleMinePostType;
    }

    @Override
    public String getSearchInput() {
        return "";
    }

    @Override
    public CircleInfo getCircleInfo() {
        return null;
    }

    /**
     * @param context
     * @param circleInfo
     * @param titleStrRes 标题
     * @param item1StrRes 第一个按钮
     * @param item2StrRes 第二个按钮
     * @param moneyStr    金额
     * @param descrStr    描述文字
     */
    protected void initPayPopWindow(Activity context, CircleInfo circleInfo,
                                    String titleStrRes,
                                    String item1StrRes,
                                    String item2StrRes,
                                    String moneyStr,
                                    String descrStr) {

        mPayPopWindow = PayPopWindow.builder()
                .with(context)
                .isWrap(true)
                .isFocus(true)
                .isOutsideTouch(true)
                .buildLinksColor1(R.color.themeColor)
                .buildLinksColor2(R.color.important_for_content)
                .contentView(R.layout.ppw_for_center)
                .backgroundAlpha(POPUPWINDOW_ALPHA)
                .buildDescrStr(descrStr)
                .buildLinksStr(context.getString(R.string.buy_pay_member))
                .buildTitleStr(titleStrRes)
                .buildItem1Str(item1StrRes)
                .buildItem2Str(item2StrRes)
                .buildMoneyStr(moneyStr)
                .buildCenterPopWindowItem1ClickListener(() -> {
                    mPresenter.dealCircleJoinOrExit(circleInfo,null);
                    mPayPopWindow.hide();
                })
                .buildCenterPopWindowItem2ClickListener(() -> mPayPopWindow.hide())
                .buildCenterPopWindowLinkClickListener(new PayPopWindow
                        .CenterPopWindowLinkClickListener() {
                    @Override
                    public void onLongClick() {

                    }

                    @Override
                    public void onClicked() {

                    }
                })
                .build();
        mPayPopWindow.show();

    }

    @Override
    protected Long getMaxId(@NotNull List<CirclePostListBean> data) {
        return (long) mListDatas.size();

    }

    @Override
    public void managerStickTop(Long id, int position) {
        LinkDialog dialog = createLinkDialog();
        dialog.setListener(new LinkDialog.OnDialogClickListener() {
            @Override
            public void onConfirmButtonClick(String name, String url) {
                if (TextUtils.isEmpty(url)) {
                    dialog.setErrorMessage(getString(R.string.post_apply_top_days));
                    return;
                }
                int day = Integer.valueOf(url);
                if (day > 0 && day <= 30) {
                    mPresenter.stickTopPost(id, position, day);
                    dialog.dismiss();
                } else {
                    dialog.setErrorMessage(getString(R.string.post_apply_top_days));
                }

            }

            @Override
            public void onCancelButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getFragmentManager(), LinkDialog.Tag);
    }

    protected void showDeleteTipPopupWindow(String tipStr,
                                            boolean createEveryTime, final CirclePostListBean circlePostListBean,
                                            int position) {
        super.showDeleteTipPopupWindow(tipStr, () -> mPresenter.deletePost(circlePostListBean, position), createEveryTime);
    }

    private LinkDialog createLinkDialog() {
        return LinkDialog.createLinkDialog()
                .setUrlHinit(getString(R.string.post_apply_top_days))
                .setTitleStr(getString(R.string.set_post_apply_top_days))
                .setNameVisible(false)
                .setNeedNumFomatFilter(true);
    }

    @Override
    public void onDestroyView() {
        dismissPop(mDeletCommentPopWindow);
        dismissPop(mReSendCommentPopWindow);
        dismissPop(mOtherPostPopWindow);
        dismissPop(mMyPostPopWindow);
        dismissPop(mPayPopWindow);
        if (showComment != null && !showComment.isUnsubscribed()) {
            showComment.unsubscribe();
        }
        super.onDestroyView();
    }

    @Override
    public void showDeleteTipPopupWindow(CirclePostListBean circlePostListBean) {
        showDeleteTipPopupWindow(getString(R.string.delete_post), true, circlePostListBean, 0);
    }
}
