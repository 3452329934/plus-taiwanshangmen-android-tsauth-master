package com.zhiyicx.thinksnsplus.modules.gallery;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.signature.EmptySignature;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * @author Jliuer
 * @Date 18/07/13 17:58
 * @Email Jliuer@aliyun.com
 * @Description 判断是否有缓存
 */
public class GlideCahceUtil implements Key {

    private final String id;
    private final Key signature;

    public GlideCahceUtil(String id, Key signature) {
        this.id = id;
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GlideCahceUtil that = (GlideCahceUtil) o;

        return id.equals(that.id) && signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + signature.hashCode();
        return result;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) throws UnsupportedEncodingException {
        messageDigest.update(id.getBytes(STRING_CHARSET_NAME));
        signature.updateDiskCacheKey(messageDigest);
    }

    public static boolean hasCache(Context context, String url) {
        File file = DiskLruCacheWrapper.get(Glide.getPhotoCacheDir(context), 250 * 1024 * 1024).
                get(new GlideCahceUtil(url, EmptySignature.obtain()));
        return file != null;
    }
}