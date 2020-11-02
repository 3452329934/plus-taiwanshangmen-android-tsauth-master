package com.zhiyicx.thinksnsplus.modules.dynamic.list;

import com.zhiyicx.baseproject.config.TouristConfig;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.share.Share;
import com.zhiyicx.common.base.BaseFragment;
import com.zhiyicx.common.dagger.scope.FragmentScoped;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.data.beans.SendDynamicDataBean;
import com.zhiyicx.thinksnsplus.data.beans.report.ReportResourceBean;
import com.zhiyicx.thinksnsplus.data.source.local.AllAdvertListBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicCommentBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.DynamicDetailBeanV2GreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.SendDynamicDataBeanV2GreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.local.TopDynamicBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseDynamicRepository;
import com.zhiyicx.thinksnsplus.modules.chat.private_letter.ChooseFriendActivity;
import com.zhiyicx.thinksnsplus.modules.dynamic.send.SendDynamicActivity;
import com.zhiyicx.thinksnsplus.modules.report.ReportActivity;
import com.zhiyicx.thinksnsplus.modules.report.ReportType;
import com.zhiyicx.thinksnsplus.modules.wallet.sticktop.StickTopFragment;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhiyicx.thinksnsplus.widget.popwindow.LetterPopWindow;

import javax.inject.Inject;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/2/13
 * @Contact master.jungle68@gmail.com
 */
@FragmentScoped
public class DynamicPresenter extends BaseDynamicPresenter<DynamicContract.View<DynamicContract.Presenter>, DynamicContract.Presenter> {

    @Inject
    public DynamicPresenter(DynamicContract.View rootView,
                            AllAdvertListBeanGreenDaoImpl allAdvertListBeanGreenDao,
                            DynamicDetailBeanV2GreenDaoImpl dynamicDetailBeanV2GreenDao,
                            DynamicCommentBeanGreenDaoImpl dynamicCommentBeanGreenDao,
                            SendDynamicDataBeanV2GreenDaoImpl sendDynamicDataBeanV2GreenDao,
                            TopDynamicBeanGreenDaoImpl topDynamicBeanGreenDao,
                            BaseDynamicRepository baseDynamicRepository) {
        super(rootView, allAdvertListBeanGreenDao, dynamicDetailBeanV2GreenDao,
                dynamicCommentBeanGreenDao, sendDynamicDataBeanV2GreenDao,
                topDynamicBeanGreenDao, baseDynamicRepository);
    }

    @Override
    public void onStart(Share share) {
        super.onStart(share);
        String content = "";
        String dynamicType = "";
        boolean hasImage, hasVideo;
        Letter letter;
        switch (share) {
            case FORWARD:
                if (handleTouristControl() || mShareDynamic == null) {
                    return;
                }
                hasImage = mShareDynamic.getImages() != null && !mShareDynamic.getImages().isEmpty();
                hasVideo = mShareDynamic.getVideo() != null;
                if (!hasImage && !hasVideo) {
                    dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_WORD;
                    content = mShareDynamic.getFriendlyContent();
                }
                if (hasImage) {
                    dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_IMAGE;
                    content = LetterPopWindow.PIC;

                }
                if (hasVideo) {
                    dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_VIDEO;
                    content = LetterPopWindow.VIDEO;
                }
                letter = new Letter(mShareDynamic.getUserInfoBean().getName(), content, TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC);
                letter.setId(mShareDynamic.getId() + "");
                letter.setDynamic_type(dynamicType);

                SendDynamicDataBean sendWordsDynamicDataBean = new SendDynamicDataBean();
                sendWordsDynamicDataBean.setDynamicBelong(SendDynamicDataBean.NORMAL_DYNAMIC);
                sendWordsDynamicDataBean.setDynamicType(SendDynamicDataBean.TEXT_ONLY_DYNAMIC);
                SendDynamicActivity.startToSendDynamicActivity(((BaseFragment) mRootView).getActivity(), sendWordsDynamicDataBean,letter);
                break;
            case REPORT:
                if (handleTouristControl() || mShareDynamic == null) {
                    return;
                }
                String img = "";
                if (mShareDynamic.getImages() != null && !mShareDynamic.getImages().isEmpty()) {
                    img = ImageUtils.imagePathConvertV2(mShareDynamic.getImages().get(0)
                                    .getFile(), mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.report_resource_img),
                            mContext.getResources()
                                    .getDimensionPixelOffset(R.dimen.report_resource_img),
                            100);
                }
                ReportResourceBean reportResourceBean = new ReportResourceBean(mShareDynamic
                        .getUserInfoBean(), String.valueOf(mShareDynamic
                        .getId()),
                        "", img, mShareDynamic.getFeed_content(), ReportType.DYNAMIC);
                reportResourceBean.setDesCanlook(mShareDynamic.getPaid_node() == null ||
                        mShareDynamic
                                .getPaid_node().isPaid());
                ReportActivity.startReportActivity(((BaseFragment) mRootView).getActivity(), reportResourceBean);
                mRootView.showBottomView(true);
                break;
            case COLLECT:
                if (!TouristConfig.DYNAMIC_CAN_COLLECT && handleTouristControl
                        ()) {
                    return;
                }
                handleCollect(mShareDynamic);
                mRootView.showBottomView(true);
                break;
            case LETTER:
                if (handleTouristControl() || mShareDynamic == null) {
                    return;
                }
                hasImage = mShareDynamic.getImages() != null && !mShareDynamic.getImages().isEmpty();
                hasVideo = mShareDynamic.getVideo() != null;
                if (!hasImage && !hasVideo) {
                    dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_WORD;
                    content = mShareDynamic.getFriendlyContent();
                }
                if (hasImage) {
                    dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_IMAGE;
                    content = LetterPopWindow.PIC;

                }
                if (hasVideo) {
                    dynamicType = TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC_TYPE_VIDEO;
                    content = LetterPopWindow.VIDEO;
                }
                letter = new Letter(mShareDynamic.getUserInfoBean().getName(), content, TSEMConstants.BUNDLE_CHAT_MESSAGE_LETTER_TYPE_DYNAMIC);
                letter.setId(mShareDynamic.getId() + "");
                letter.setDynamic_type(dynamicType);
                ChooseFriendActivity.startChooseFriendActivity(((BaseFragment) mRootView).getActivity(), letter);
                break;
            case DELETE:
                if (handleTouristControl() || mShareDynamic == null) {
                    return;
                }
                mRootView.showDeleteTipPopupWindow(mShareDynamic);
                break;
            case STICKTOP:
                if (handleTouristControl() || mShareDynamic == null) {
                    return;
                }
                StickTopFragment.startSticTopActivity(((BaseFragment) mRootView).getActivity(), StickTopFragment
                        .TYPE_DYNAMIC, mShareDynamic.getId());
                break;
            default:
        }
    }
}