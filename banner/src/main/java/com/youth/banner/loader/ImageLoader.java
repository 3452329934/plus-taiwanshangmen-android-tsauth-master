package com.youth.banner.loader;

import android.content.Context;
import android.widget.ImageView;

import com.zhiyicx.baseproject.widget.imageview.FilterImageView;


public abstract class ImageLoader implements ImageLoaderInterface<FilterImageView> {

    @Override
    public FilterImageView createImageView(Context context) {
        return new FilterImageView(context);
    }

}
