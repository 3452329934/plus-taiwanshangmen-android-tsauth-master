package com.zhiyicx.thinksnsplus.modules.information.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.baseproject.base.BaseListBean;
import com.zhiyicx.baseproject.config.ImageZipConfig;
import com.zhiyicx.common.base.BaseApplication;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.RegexUtils;
import com.zhiyicx.common.utils.SkinUtils;
import com.zhiyicx.common.utils.TimeUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.InfoListDataBean;
import com.zhiyicx.thinksnsplus.utils.ImageUtils;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.concurrent.TimeUnit;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

public abstract class InfoListItemThreePic implements ItemViewDelegate<BaseListBean> {

    private boolean mIsShowContent;

    public InfoListItemThreePic(boolean isShowContent) {
        this.mIsShowContent = isShowContent;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_info_three_pic;
    }

    @Override
    public boolean isForViewType(BaseListBean item, int position) {
        boolean isInfo = item instanceof InfoListDataBean;
        if (isInfo) {
            return ((InfoListDataBean) item).getImages() != null && ((InfoListDataBean) item).getImages().size() >= 3;
        } else {
            return false;
        }
    }

    @Override
    public void convert(ViewHolder holder, BaseListBean baseListBean, BaseListBean lastT,
                        final int position, int itemCounts) {
        final InfoListDataBean realData = (InfoListDataBean) baseListBean;
        final TextView title = holder.getView(R.id.item_info_title);
        final ImageView imageView1 = holder.getView(R.id.iv_pic_one);
        final ImageView imageView2 = holder.getView(R.id.iv_pic_two);
        final ImageView imageView3 = holder.getView(R.id.iv_pic_three);
        imageView1.setOnClickListener(v -> holder.getConvertView().performClick());
        imageView2.setOnClickListener(v -> holder.getConvertView().performClick());
        imageView3.setOnClickListener(v -> holder.getConvertView().performClick());
        // 记录点击过后颜色
        if (AppApplication.sOverRead.contains(realData.getId())) {
            title.setTextColor(SkinUtils.getColor(R.color.normal_for_assist_text));
        }

        title.setText(realData.getTitle());

        touchEvent(holder, position, realData, title, imageView1);
        touchEvent(holder, position, realData, title, imageView2);
        touchEvent(holder, position, realData, title, imageView3);
        // 被驳回和投稿中只显示内容
        holder.setVisible(R.id.ll_info, mIsShowContent ? View.GONE : View.VISIBLE);
        holder.setVisible(R.id.tv_info_content, mIsShowContent ? View.VISIBLE : View.GONE);
        String content = realData.getText_content();
        if (TextUtils.isEmpty(content)) {
            content = RegexUtils.replaceImageId(MarkdownConfig.IMAGE_FORMAT, realData.getContent());
            content = content.replaceAll(MarkdownConfig.NORMAL_FORMAT, "");
        }
        String summaryContent = realData.getSubject();
        content = mIsShowContent ? content : summaryContent;
        holder.setText(R.id.tv_info_content, content);
        // 投稿来源，浏览数，时间
        String from = title.getContext().getString(R.string
                .info_publish_original).equals(realData.getFrom()) ?
                realData.getAuthor() : realData.getFrom();
        String infoData = String.format(title.getContext().getString(R.string.info_list_count)
                , from, ConvertUtils.numberConvert(realData.getHits()), TimeUtils.getTimeFriendlyNormal(realData
                        .getCreated_at()));
        holder.setText(R.id.item_info_timeform, infoData);

        loadImage(ImageUtils.imagePathConvertV2(realData.getImages().get(0).getFile_id(), 0, 0,
                ImageZipConfig.IMAGE_80_ZIP), imageView1);
        loadImage(ImageUtils.imagePathConvertV2(realData.getImages().get(1).getFile_id(), 0, 0,
                ImageZipConfig.IMAGE_80_ZIP), imageView2);
        loadImage(ImageUtils.imagePathConvertV2(realData.getImages().get(2).getFile_id(), 0, 0,
                ImageZipConfig.IMAGE_80_ZIP), imageView3);
        // 来自单独分开
        String category = realData.getCategory() == null || (realData.getCategory() != null && realData.getInfo_type() != null && realData
                .getInfo_type() != -1) ? "" : realData.getCategory().getName();
        holder.setVisible(R.id.tv_from_channel, category.isEmpty() ? View.GONE : View.VISIBLE);
        holder.setText(R.id.tv_from_channel, category);
        // 是否置顶
        holder.setVisible(R.id.tv_top_flag, realData.isTop() ? View.VISIBLE : View.GONE);
    }

    private void loadImage(String url, ImageView imageView) {
        Glide.with(BaseApplication.getContext())
                .load(url)
                .placeholder(R.drawable.shape_default_image)
                .error(R.drawable.shape_default_image)
                .into(imageView);
    }

    private void touchEvent(ViewHolder holder, int position, InfoListDataBean realData, TextView title, ImageView imageView) {
        RxView.longClicks(holder.itemView)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> itemLongClick(position, imageView, title, realData));
        RxView.clicks(holder.itemView)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> itemClick(position, imageView, title, realData));
    }

    public abstract void itemClick(int position, ImageView imageView, TextView title,
                                   InfoListDataBean realData);

    public void itemLongClick(int position, ImageView imageView, TextView title,
                              InfoListDataBean realData) {
    }

}