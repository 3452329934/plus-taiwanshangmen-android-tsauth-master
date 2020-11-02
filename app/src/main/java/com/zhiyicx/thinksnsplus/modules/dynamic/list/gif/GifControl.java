package com.zhiyicx.thinksnsplus.modules.dynamic.list.gif;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.zhiyicx.baseproject.widget.imageview.FilterImageView;
import com.zhiyicx.common.utils.ActivityHandler;
import com.zhiyicx.common.utils.log.LogUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.jzvd.JZUtils;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * ThinkSNS Plus
 * Copyright (c) 2019 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author icepring
 * @Date 2019/02/20/10:34
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class GifControl {

    /**
     * 至少 80% 可见的时候，才给播放
     */
    private static final float MIN_VISIBLE_LIMIT = 0.8f;

    private WeakReference<FilterImageView> mImageViewWeakReference;
    private FilterImageView mImageView;
    private Drawable mDrawable;

    private List<FilterImageView> mImageViews;

    private GifViewHolder mViewHolder;

    private Rect mRect;
    private Subscription playNextGif;
    private int gifLoadedCount;
    private Subscription subscription;

    private GifControl() {
        mRect = new Rect();
        mImageViews = new ArrayList<>();
    }

    /**
     * IllegalArgumentException 重试
     * @return
     */
    public boolean play() {
        subscription = Observable.just(1)
                .flatMap((Func1<Integer, Observable<Drawable>>) drawable -> {
                    if (mDrawable == null) {
                        findGifView();
                    }
                    return Observable.just(mDrawable);
                })
                .filter(drawable -> {
                    boolean notnull = drawable != null && mImageView != null;
                    boolean visible = notnull && mImageView.getGlobalVisibleRect(Instance.c.mRect)
                            && (1.0f * Instance.c.mRect.height() / mImageView.getHeight() >= MIN_VISIBLE_LIMIT);
                    if (notnull && !visible) {
                        if (drawable instanceof GifDrawable) {
                            ((GifDrawable) drawable).stop();
                        }
                    }
                    // 判断是否可见
                    boolean canpaly = notnull && visible;

                    // 可见补充条件，
                    if (canpaly) {
                        Activity current = ActivityHandler.getInstance().currentActivity();
                        Activity view = JZUtils.scanForActivity(mImageView.getContext());
                        canpaly = view == current;
                    }

                    if (!canpaly) {
                        throw new NullPointerException("not found visible gif");
                    }
                    return true;
                })
                .flatMap((Func1<Drawable, Observable<GifDrawable>>) drawable -> {
                    if (!(drawable instanceof GifDrawable)) {
                        // 不是 gif ,重试
                        findGifView();
                        throw new IllegalArgumentException("not gifdrawable");
                    }

                    Class<?> clazz = drawable.getClass();
                    Field[] fs = clazz.getDeclaredFields();
                    for (Field f : fs) {
                        f.setAccessible(true);
                        if ("frameLoader".equals(f.getName())) {
                            Object oc = null;
                            try {
                                oc = f.get(drawable);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            Class<?> load = f.getType();
                            Field[] loadfs = load.getDeclaredFields();
                            for (Field loadf : loadfs) {
                                loadf.setAccessible(true);
                                if ("current".equals(loadf.getName())) {
                                    try {
                                        if (loadf.get(oc) == null) {
                                            // gif 没准备好,重试
                                            throw new IllegalArgumentException("not ready to play");
                                        }
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    return Observable.just((GifDrawable) drawable);
                })
                .retryWhen(observable -> observable.flatMap((Func1<Throwable, Observable<?>>) throwable -> {
                    if (throwable instanceof IllegalArgumentException) {
                        LogUtils.d("retry::" + throwable);
                        return Observable.timer(4, TimeUnit.SECONDS);
                    }
                    return Observable.error(throwable);
                }))
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(currentPlayGif -> {
                    currentPlayGif.setLoopCount(mImageViews.size() > 1 ? 1 : -1);
                    GifDecoder decoder = currentPlayGif.getDecoder();
                    mImageView.hideGifTag();
                    currentPlayGif.start();
                    int duration = 0;
                    for (int i = 0; i < currentPlayGif.getFrameCount(); i++) {
                        duration += decoder.getDelay(i);
                    }
                    if (playNextGif != null && !playNextGif.isUnsubscribed()) {
                        playNextGif.unsubscribe();
                    }
                    playNextGif = Observable.timer(duration, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(aLong -> {
                                if (mImageViews.isEmpty()) {
                                    throw new NullPointerException("not found gif and replace resources");
                                }
                                int p = mImageViews.indexOf(mImageView);
                                findGifView();
                                int next = p + 1 >= mImageViews.size() ? 0 : p + 1;
                                mImageView = mImageViews.get(next);
                                mDrawable = mImageView.getDrawable();
                                play();
                            }, throwable -> {
                                if (mImageView != null) {
                                    mImageView.setIshowGifTag(true);
                                }
                                replaceResource();
                            });
                }, throwable -> {
                    LogUtils.d(throwable);
                    if (mImageView != null) {
                        mImageView.setIshowGifTag(true);
                    }
                    replaceResource();
                });
        return false;
    }

    public void stop() {
        replaceResource();
    }

    private void replaceResource() {
        Instance.c.mDrawable = null;
        Instance.c.mImageViews.clear();
    }

    private static class Instance {
        static GifControl c = new GifControl();
    }

    public static GifControl getInstance(GifViewHolder holder) {
        Instance.c.gifLoadedCount = 0;
        if (Instance.c.mDrawable instanceof GifDrawable) {
            ((GifDrawable) Instance.c.mDrawable).stop();
        }
        if (Instance.c.subscription != null && !Instance.c.subscription.isUnsubscribed()) {
            Instance.c.subscription.unsubscribe();
        }
        Instance.c.mViewHolder = holder;
        return Instance.c;
    }

    public boolean findGifView() {
        if (mImageViews.size() > 1 && mImageView != null) {
            mImageView.setIshowGifTag(true);
        }
        GifViewHolder holder = Instance.c.mViewHolder;
        if (null == holder) {
            return false;
        }
        if (Instance.c.gifLoadedCount == holder.getGifViews().size()) {
            return Instance.c.gifLoadedCount != 0;
        }

        List<FilterImageView> find = new ArrayList<>();
        boolean canpaly = false;
        for (View v : holder.getGifViews()) {
            if (!(v instanceof FilterImageView)) {
                continue;
            }
            FilterImageView imageView = (FilterImageView) v;
            Drawable drawable = imageView.getDrawable();
            if (v.getGlobalVisibleRect(Instance.c.mRect) && (1.0f * Instance.c.mRect.height() / v.getHeight() >= MIN_VISIBLE_LIMIT)) {
                if (drawable instanceof GifDrawable) {
                    find.add(imageView);
                }
                canpaly = true;
            }
        }
        if (!find.isEmpty()) {
            Instance.c.mImageViews.clear();
            Instance.c.mImageViews.addAll(find);
            Instance.c.gifLoadedCount = find.size();
        }
        if (!Instance.c.mImageViews.isEmpty()) {
            if (Instance.c.mImageView != null && Instance.c.mImageView != Instance.c.mImageViews.get(0)) {
                Instance.c.mImageView.setIshowGifTag(true);
                if (Instance.c.mImageView.getDrawable() instanceof GifDrawable) {
                    ((GifDrawable) Instance.c.mImageView.getDrawable()).stop();
                }
            }
            Instance.c.mImageView = new WeakReference<>(Instance.c.mImageViews.get(0)).get();
            Instance.c.mDrawable = Instance.c.mImageViews.get(0).getDrawable();
        }
        return canpaly;
    }

    public Drawable getCurrentPlayGif() {
        return mDrawable;
    }

    public boolean hasGifPlaying() {
        boolean result = false;
        if (Instance.c.mDrawable instanceof GifDrawable) {
            result = ((GifDrawable) Instance.c.mDrawable).isRunning();
        }
        return result;
    }

    public interface OnCompleteListener {
        void onComplete();
    }

    public interface OnErrorListener {
        void onError();
    }

    public static class GifViewHolder {
        private List<View> mGifViews;

        public GifViewHolder(List<View> gifViews) {
            mGifViews = new ArrayList<>(gifViews);
        }

        public GifViewHolder() {
            mGifViews = new ArrayList<>();
        }

        public void addGifView(View v) {
            if (mGifViews.contains(v)) {
                return;
            }
            mGifViews.add(v);
        }

        public List<View> getGifViews() {
            return mGifViews;
        }
    }
}
