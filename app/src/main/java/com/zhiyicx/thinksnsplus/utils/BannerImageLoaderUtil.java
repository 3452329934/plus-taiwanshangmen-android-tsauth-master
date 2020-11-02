package com.zhiyicx.thinksnsplus.utils;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zhiyicx.baseproject.widget.imageview.FilterImageView;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.thinksnsplus.R;

public class BannerImageLoaderUtil extends com.youth.banner.loader.ImageLoader {

    private static final long serialVersionUID = 4346287432534848693L;

    @Override
    public void displayImage(Context context, Object path, FilterImageView imageView, OnImageSourceReady onImageSourceReady) {
        String url = (String) path;
        Glide.with(context)
                .load(url)
                .error(R.drawable.shape_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        if (onImageSourceReady != null) {
                            onImageSourceReady.onImageSourceRead(false);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        if (onImageSourceReady != null) {
                            onImageSourceReady.onImageSourceRead(true);
                        }
                        return false;
                    }
                })
                .into(imageView);
    }

    @Override
    public void displayImage(Context context, String base64, FilterImageView imageView) {
        byte[] decodedString = ConvertUtils.file2Bytes(base64);
        if (decodedString == null) {
            return;
        }
        Glide.with(context)
                .load(decodedString)
                .error(R.drawable.shape_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }
}