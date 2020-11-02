package com.zhiyicx.thinksnsplus.modules.chat.location.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.amap.api.services.core.PoiItem;
import com.jakewharton.rxbinding.view.RxView;
import com.zhiyicx.common.utils.ColorPhrase;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.modules.circle.create.location.CircleLocationFragment;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;
import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;

/**
 * @Author Jliuer
 * @Date 2018/06/13/17:17
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchLocationFragment extends CircleLocationFragment {

    @Override
    protected boolean isNeedRequestNetDataWhenCacheDataNull() {
        return false;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mTvNoLocation.setVisibility(View.GONE);
    }

    @Override
    protected void convertData(ViewHolder holder, PoiItem poiItem) {
        String keyWord = mTvSearch.getText().toString();
        String title = poiItem.getTitle();
        title = title.replaceFirst(keyWord, "<" + keyWord + ">");
        CharSequence titleString = ColorPhrase.from(title).withSeparator("<>")
                .innerColor(ContextCompat.getColor(mActivity, R.color.send_location))
                .outerColor(ContextCompat.getColor(mActivity, R.color.important_for_content))
                .format();
        holder.setText(R.id.tv_location_name, titleString);
        holder.setText(R.id.tv_location_address, poiItem.getSnippet());
        RxView.clicks(holder.itemView)
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(BUNDLE_DATA, poiItem);
                    intent.putExtras(bundle);
                    getActivity().setResult(RESULT_OK, intent);
                    getActivity().finish();
                });
    }

    @Override
    protected void requestCacheData(Long maxId, boolean isLoadMore) {
    }

    @Override
    protected boolean isNeedRefreshAnimation() {
        return false;
    }
}
