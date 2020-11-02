package com.zhiyicx.thinksnsplus.modules.guide;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.trycatch.mysnackbar.ScreenUtil;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoaderInterface;
import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.common.utils.ActivityHandler;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.common.utils.SharePreferenceUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.RealAdvertListBean;
import com.zhiyicx.thinksnsplus.modules.settings.aboutus.CustomWEBActivity;
import com.zhiyicx.thinksnsplus.utils.BannerImageLoaderUtil;
import com.zhiyicx.thinksnsplus.widget.popwindow.ProtrolPopWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @author Jliuer
 * @Date 18/06/19 17:30
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class GuideFragment extends TSFragment<GuideContract.Presenter> implements
        GuideContract.View, OnBannerListener, ViewPager.OnPageChangeListener, Banner.OnBannerTimeListener, ImageLoaderInterface.OnImageSourceReady  {
    private static final String SP_PROTROL = "sp_protrol";

    @BindView(R.id.guide_banner)
    Banner mGuideBanner;
    @BindView(R.id.guide_text)
    TextView mGuideText;
    int mPosition;

    /**
     * 广告是否结束
     */
    boolean isFinish;

    /**
     * 是否点击了广告
     */
    boolean isClick;

    /**
     * 是否第一次进入该页面
     */
    boolean isFirst = true;

    public static final String ADVERT = "advert";

    private List<RealAdvertListBean> mBootAdverts;

    private ProtrolPopWindow mProtrolPopWindow;

    private boolean mNotNeedShowProtrolPop = false;


    /**
     * Activity 手动调用处理
     *
     * @param intent
     */
    public void onNewIntent(Intent intent) {
        isClick = false;
        isFirst = false;
        if (isFinish || mPosition == mGuideBanner.getItemCount() - 1) {
            mPresenter.checkLogin();
        }
    }

    @Override
    protected boolean setUseSatusbar() {
        return true;
    }

    @Override
    protected boolean setUseStatusView() {
        return false;
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }


    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_guide_v2;
    }

    @Override
    protected void initView(View rootView) {
        mRootView.setVisibility(View.INVISIBLE);
        //判断全面屏类型的手机，是否启用的虚拟按键
        if (ScreenUtil.isNavigationBarExist(mActivity)) {
            int bottomNavagationHeight = DeviceUtils.getNavigationBarHeight(mActivity.getApplicationContext());

            ((FrameLayout.LayoutParams) mGuideText.getLayoutParams()).setMargins(
                    0
                    , bottomNavagationHeight + getResources().getDimensionPixelOffset(R.dimen.spacing_mid)
                    , getResources().getDimensionPixelOffset(R.dimen.spacing_normal)
                    , 0
            );
        } else {
            ((FrameLayout.LayoutParams) mGuideText.getLayoutParams()).setMargins(
                    0
                    , getResources().getDimensionPixelOffset(R.dimen.spacing_mid)
                    , getResources().getDimensionPixelOffset(R.dimen.spacing_normal)
                    , 0
            );
        }


        RxView.clicks(mGuideText).throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .compose(this.bindToLifecycle())
                .subscribe(aVoid -> mPresenter.checkLogin());
        mNotNeedShowProtrolPop = SharePreferenceUtils.getBoolean(getContext(), SP_PROTROL);

        if (!mNotNeedShowProtrolPop) {
            mRootView.post(this::showGoodsBuyLimitPop);
        } else {
            mPresenter.getBootAdvert();
        }
    }

    /**
     * 更新广告数据
     *
     * @param bootAdverts 引导页广告数据
     */
    @Override
    public void initAdvertData(List<RealAdvertListBean> bootAdverts) {
        mBootAdverts = bootAdverts;
        if (com.zhiyicx.common.BuildConfig.USE_ADVERT && mBootAdverts != null) {
            if (!mBootAdverts.isEmpty()) {
                List<String> urls = new ArrayList<>();
                for (RealAdvertListBean realAdvertListBean : mBootAdverts) {
                    if (realAdvertListBean == null) {
                        continue;
                    }
                    if (realAdvertListBean.getAdvertFormat() == null) {
                        continue;
                    }
                    if (realAdvertListBean.getAdvertFormat().getImage() == null) {
                        continue;
                    }
                    if (TextUtils.isEmpty(realAdvertListBean.getAdvertFormat().getImage().getBase64Image())) {
                        // 没有下载完成的广告不显示
                        continue;
                    }
                    if (!FileUtils.isFileExists(realAdvertListBean.getAdvertFormat().getImage().getBase64Image())) {
                        // 没有下载完成的广告不显示
                        continue;
                    }

                    urls.add(realAdvertListBean.getAdvertFormat().getImage().getBase64Image());
                }
                if (urls.isEmpty()) {
                    mBootAdverts = null;
                    return;
                }
                int time = mBootAdverts.get(0).getAdvertFormat().getImage().getDuration() * 1000;
                time = time > 0 ? time : 3000;
                mGuideText.setVisibility(View.VISIBLE);

                mGuideBanner.setBannerStyle(BannerConfig.NOT_INDICATOR);
                mGuideBanner.setImageLoader(new BannerImageLoaderUtil());
                mGuideBanner.isBase64Image(false);
                mGuideBanner.setImages(urls);
                mGuideBanner.isAutoPlay(true);
                mGuideBanner.isDownStopAutoPlay(false);
                mGuideBanner.setViewPagerIsScroll(false);
                mGuideBanner.setDelayTime(time);
                mGuideBanner.setTimeListener(this);
                mGuideBanner.setOnBannerListener(this);
                mGuideBanner.setOnPageChangeListener(this);
                mGuideBanner.setOnImageSourceReady(this);
                isFirst = false;
                startAdvert();
            }
        }
    }


    /**
     * 图片加载好了再显示页面
     * @param loadStatus
     */
    @Override
    public void onImageSourceRead(boolean loadStatus) {
        mRootView.setVisibility(View.VISIBLE);
    }

    /**
     * 启动广告
     */
    public void startAdvert() {
        mGuideBanner.start();
    }


    @Override
    protected void initData() {
        mPresenter.initConfig();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirst&& mNotNeedShowProtrolPop) {
            if (com.zhiyicx.common.BuildConfig.USE_ADVERT && (mBootAdverts != null && !mBootAdverts.isEmpty())) {
                startAdvert();
            } else {
                mPresenter.checkLogin();
            }
            isFirst = false;
        }
    }
    /**
     * 购买商品限制弹窗
     */
    private void showGoodsBuyLimitPop() {
        mProtrolPopWindow = ProtrolPopWindow.builder()
                .isFocus(false)
                .isOutsideTouch(false)
                .with(mActivity)
                .buildCenterPopWindowItem1ClickListener(new ProtrolPopWindow.CenterPopWindowItemClickListener() {
                    @Override
                    public void onRightClicked() {
                        agreeUserProtrol();
                    }

                    @Override
                    public void onLeftClicked() {
                        mProtrolPopWindow.getmBuilder()
                                .buildCenterPopWindowItem1ClickListener(new ProtrolPopWindow.CenterPopWindowItemClickListener() {
                                    @Override
                                    public void onRightClicked() {
                                        agreeUserProtrol();
                                    }

                                    @Override
                                    public void onLeftClicked() {
                                        mProtrolPopWindow.dismiss();
                                        ActivityHandler.getInstance().AppExit();
                                    }
                                })
                                .title(getString(R.string.protrol_title_tip))
                                .leftStr(getString(R.string.disagree_and_out))
                                .content(getString(R.string.disagree_protrol_tip, getString(R.string.app_name)))
                                .rightStr(getString(R.string.agree_and_go_on));
                        mProtrolPopWindow.updateContentText();
                        mProtrolPopWindow.updateLeftText();
                        mProtrolPopWindow.updateRightText();
                        mProtrolPopWindow.updateTitleText();
                    }
                })
                .build();
        mProtrolPopWindow.show();
    }

    /**
     * 同意用户协议
     */
    private void agreeUserProtrol() {
        mProtrolPopWindow.dismiss();
        mNotNeedShowProtrolPop = true;
        SharePreferenceUtils.saveBoolean(getContext(), SP_PROTROL, true);
        mPresenter.checkLogin();
    }

    @Override
    public void startActivity(Class aClass) {
        repleaseAdvert();
        if (mActivity != null && !isDetached() && isAdded()) {
            startActivity(new Intent(mActivity, aClass));
            mActivity.finish();
        }
    }

    @Override
    public Activity getCurrentActivity() {
        return mActivity;
    }


    private void repleaseAdvert() {
        if (!com.zhiyicx.common.BuildConfig.USE_ADVERT || mGuideBanner == null) {
            return;
        }
        mGuideBanner.setOnPageChangeListener(null);
        mGuideBanner.setTimeListener(null);
        mGuideBanner.stopAutoPlay();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mGuideBanner == null) {
            return;
        }
        mPosition = mGuideBanner.getCurrentItem();

        if (mPosition > 0) {
            int time = mBootAdverts.get(position - 1).getAdvertFormat().getImage().getDuration() * 1000;
            time = time > 0 ? time : position * 3000;
            mGuideBanner.setDelayTime(time);
            mGuideBanner.setTimeListener(this);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTimeTick(String time) {
        if (mGuideText == null) {
            return;
        }
        mGuideText.setText(time);
    }

    @Override
    public void onFinish() {
        isFinish = true;
        if (isClick) {
            return;
        }
        mPresenter.checkLogin();
    }

    @Override
    public void onBannerClick(int position) {

        isClick = true;
        if (isFinish) {
            return;
        }
        CustomWEBActivity.startToWEBActivity(getActivity(), mBootAdverts.get(position)
                        .getAdvertFormat().getImage().getLink(),
                mBootAdverts.get(position).getTitle(), ADVERT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.checkLogin();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dismissPop(mProtrolPopWindow);
        if (mGuideBanner != null) {
            mGuideBanner.releaseBanner();
        }
    }
}
