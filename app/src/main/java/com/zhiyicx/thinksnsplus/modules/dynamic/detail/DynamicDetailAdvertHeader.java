package com.zhiyicx.thinksnsplus.modules.dynamic.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhiyicx.baseproject.widget.imageview.FilterImageView;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.RealAdvertListBean;

import java.util.List;

/**
 * @Author Jliuer
 * @Date 2017/05/17/17:32
 * @Email Jliuer@aliyun.com
 * @Description 详情广告
 */
public class DynamicDetailAdvertHeader {

    private View mRootView;
    private LinearLayout mAdvertContainer;
    private LinearLayout mLLAdvert;
    private View mLLAdvertTag;
    private TextView mTitle;
    private Context mContext;
    private List<RealAdvertListBean> mAdvertListBeans;

    private OnItemClickListener mOnItemClickListener;

    public DynamicDetailAdvertHeader(Context context) {
        this(context, LayoutInflater.from(context).inflate(R.layout.advert_details, null));
    }

    public DynamicDetailAdvertHeader(Context context, View rootView) {
        mContext = context;
        mRootView = rootView;
        mTitle = (TextView) mRootView.findViewById(R.id.tv_advert_title);
        mAdvertContainer = (LinearLayout) mRootView.findViewById(R.id.fl_advert_container);
        mLLAdvertTag = mRootView.findViewById(R.id.ll_advert_tag);
        mLLAdvert = (LinearLayout) mRootView.findViewById(R.id.ll_advert);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setTitle(String title) {
        mTitle.setText(title);
    }

    public void setAdverts(List<RealAdvertListBean> adverts) {
        mAdvertListBeans = adverts;
        adverts = adverts.subList(0, adverts.size() >= 3 ? 3 : adverts.size());
        for (int i = 0; i < adverts.size(); i++) {
            FilterImageView imageView = new FilterImageView(mContext);
            imageView.setImageResource(R.drawable.shape_default_image);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            //图片比例要求与后台一致1020x270(单张)502x270(两张)340x270(三张)
            int imageWidth,imageHight;
            float scale;
            if (adverts.size() == 1){
                imageWidth = DeviceUtils.getScreenWidth(mContext);
                scale = (float)1020/imageWidth;
            }else if (adverts.size() == 2 ){
                imageWidth = DeviceUtils.getScreenWidth(mContext)/2;
                scale = (float)502/imageWidth;
            }else {
                imageWidth = DeviceUtils.getScreenWidth(mContext)/3;
                scale = (float) 340/imageWidth;
            }
            imageHight = (int) (270 * scale);
            LinearLayout.LayoutParams params  = new LinearLayout.LayoutParams(0,imageHight);
//            params.width = imageWidth;
            params.weight = 1;
            imageView.setLayoutParams(params);
            mAdvertContainer.addView(imageView);
            final int position = i;
            final String url = adverts.get(i).getAdvertFormat().getImage().getImage();
            final String link = adverts.get(i).getAdvertFormat().getImage().getLink();
            Glide.with(mContext)
                    .load(url)
                    .override(imageWidth,imageHight)
                    .placeholder(R.drawable.shape_default_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.shape_default_image)
                    .into(imageView);
//            AppApplication.AppComponentHolder.getAppComponent().imageLoader().loadImage(BaseApplication.getContext(), GlideImageConfig.builder()
//                    .url(url)
//                    .placeholder(R.drawable.shape_default_image)
//                    .errorPic(R.drawable.shape_default_image)
//                    .imagerView(imageView)
//                    .build());
            imageView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClik(v, position, link);
                }
            });
        }
    }

    public List<RealAdvertListBean> getAdvertListBeans() {
        return mAdvertListBeans;
    }

    public void hideAdvert() {
        mRootView.setVisibility(View.GONE);
    }

    public void showAdvert() {
        mRootView.setVisibility(View.VISIBLE);
    }

    public void setHeight(int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, height);
        int space= mContext.getResources().getDimensionPixelOffset(R.dimen.spacing_normal);
        params.setMargins(space, space, space, space);
        mAdvertContainer.setLayoutParams(params);
    }

    public LinearLayout getAdvertContainer() {
        return mAdvertContainer;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setAdvertTagVisible(int visible) {
        mLLAdvertTag.setVisibility(visible);
    }

    public interface OnItemClickListener {
        void onItemClik(View v, int position, String url);
    }
}
