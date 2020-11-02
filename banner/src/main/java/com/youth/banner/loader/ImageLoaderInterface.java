package com.youth.banner.loader;

import android.content.Context;
import android.view.View;

import java.io.Serializable;


public interface ImageLoaderInterface<T extends View> extends Serializable {
    void displayImage(Context context, Object path, T imageView, OnImageSourceReady onImageSourceReady);
    void displayImage(Context context, String base64, T imageView);

    T createImageView(Context context);

    interface  OnImageSourceReady{

        void onImageSourceRead(boolean loadStatus);

    }
}
