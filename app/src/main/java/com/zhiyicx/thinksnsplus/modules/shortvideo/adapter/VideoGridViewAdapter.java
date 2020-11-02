package com.zhiyicx.thinksnsplus.modules.shortvideo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhiyicx.thinksnsplus.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zycx.shortvideo.media.VideoInfo;
import com.zycx.shortvideo.utils.DateUtil;

import java.util.List;

/**
 * @Author Jliuer
 * @Date 2018/03/28/14:08
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class VideoGridViewAdapter extends CommonAdapter<VideoInfo> {

    public VideoGridViewAdapter(Context context, int layoutId, List<VideoInfo> datas) {
        super(context, layoutId, datas);
    }

    /**
     * @param holder
     * @param video
     * @param position
     * @link https://blog.csdn.net/u012947056/article/details/78508986
     * DiskCacheStrategy.RESULT && DiskCacheStrategy.SOURCE
     */
    @Override
    protected void convert(ViewHolder holder, VideoInfo video, int position) {
        ImageView imageView = holder.getView(R.id.iv_cover);
        if (!TextUtils.isEmpty(video.getPath())) {
            holder.setVisible(R.id.tv_duration, View.VISIBLE);
            holder.setText(R.id.tv_duration, DateUtil.convertSecondsToTime(video.getDuration() / 1000));
        } else {
            holder.setVisible(R.id.tv_duration, View.GONE);
        }
        Glide.with(mContext)
                .load(video.getPath())
                .placeholder(R.drawable.shape_default_image)
                .error(R.mipmap.pic_shootvideo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(imageView);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        if (holder != null) {
            Glide.clear(holder.getImageViwe(R.id.iv_cover));
        }
        super.onViewRecycled(holder);
    }
}
