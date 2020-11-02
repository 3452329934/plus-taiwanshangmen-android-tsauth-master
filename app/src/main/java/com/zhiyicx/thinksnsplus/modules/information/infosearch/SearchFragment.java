package com.zhiyicx.thinksnsplus.modules.information.infosearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.config.TouristConfig;
import com.zhiyicx.baseproject.widget.edittext.DeleteEditText;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.common.utils.FileUtils;
import com.zhiyicx.common.utils.recycleviewdecoration.CustomLinearDecoration;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.data.beans.InfoListDataBean;
import com.zhiyicx.thinksnsplus.modules.information.adapter.InfoListItem;
import com.zhiyicx.thinksnsplus.modules.information.adapter.InfoListItemThreePic;
import com.zhiyicx.thinksnsplus.modules.information.infodetails.InfoDetailsActivity;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.functions.Action1;

import static com.zhiyicx.thinksnsplus.modules.information.infodetails.InfoDetailsFragment.BUNDLE_INFO;


/**
 * @Author Jliuer
 * @Date 2017/03/06
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class SearchFragment extends TSListFragment<SearchContract.Presenter, InfoListDataBean>
        implements SearchContract.View {

    @BindView(R.id.fragment_search_back)
    ImageView mFragmentInfoSearchBack;
    @BindView(R.id.fragment_info_search_edittext)
    DeleteEditText mFragmentInfoSearchEdittext;
    @BindView(R.id.fragment_search_cancle)
    TextView mFragmentInfoSearchCancle;
    @BindView(R.id.fragment_search_container)
    RelativeLayout mFragmentInfoSearchContainer;

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_info_search;
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    @Override
    protected void musicWindowsStatus(boolean isShow) {
        super.musicWindowsStatus(isShow);
        if (isShow) {
            int rightX = ConvertUtils.dp2px(getContext(), 44) * 3 / 4 + ConvertUtils.dp2px(getContext(), 15);
            mFragmentInfoSearchContainer.setPadding(0, 0, rightX, 0);
        }
    }

    @Override
    protected RecyclerView.ItemDecoration getItemDecoration() {
        return new CustomLinearDecoration(0, getResources().getDimensionPixelSize(R.dimen
                .divider_line), 0, 0, ContextCompat.getDrawable(getContext(), R.drawable
                .shape_recyclerview_grey_divider));
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        setEmptyViewVisiable(false);
        mFragmentInfoSearchContainer.setVisibility(searchContainerVisibility() ? View.VISIBLE : View.GONE);
        if (searchContainerVisibility()) {
            RxTextView.afterTextChangeEvents(mFragmentInfoSearchEdittext)
                    .compose(this.bindToLifecycle())
                    .subscribe(textViewAfterTextChangeEvent -> {
                        if (TextUtils.isEmpty(textViewAfterTextChangeEvent.editable())) {
                            mPresenter.getRecommendInfo();
                        }

                    });
        }
        mFragmentInfoSearchEdittext.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        requestNetData(0L, false);
                        return true;
                    }
                    return false;
                });
    }

    @Override
    protected boolean showToolbar() {
        return false;
    }

    @OnClick({R.id.fragment_search_back, R.id.fragment_search_cancle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment_search_back:
                getActivity().finish();
                break;
            case R.id.fragment_search_cancle:
                getActivity().finish();
                break;
            default:
        }

    }

    @Override
    protected MultiItemTypeAdapter getAdapter() {
        MultiItemTypeAdapter adapter = new MultiItemTypeAdapter(getActivity(), mListDatas);
        adapter.addItemViewDelegate(new InfoListItem(false) {
            @Override
            public void itemClick(int position, ImageView imageView, TextView title, InfoListDataBean realData) {
                toDetail(imageView, title, realData);
            }
        });
        adapter.addItemViewDelegate(new InfoListItemThreePic(false) {
            @Override
            public void itemClick(int position, ImageView imageView, TextView title, InfoListDataBean realData) {
                toDetail(imageView, title, realData);
            }
        });
        return adapter;
    }

    private void toDetail(ImageView imageView, TextView title, InfoListDataBean realData) {
        if (TouristConfig.INFO_DETAIL_CAN_LOOK || !mPresenter.handleTouristControl()) {
            if (!AppApplication.sOverRead.contains(realData.getId())) {
                AppApplication.sOverRead.add(realData.getId().intValue());
            }
            FileUtils.saveBitmapToFile(getActivity(), ConvertUtils.drawable2BitmapWithWhiteBg(getContext()
                    , imageView.getDrawable(), R.mipmap.icon), "info_share.jpg");
            title.setTextColor(getResources()
                    .getColor(R.color.normal_for_assist_text));
            Intent intent = new Intent(getActivity(), InfoDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(BUNDLE_INFO, realData);
            intent.putExtra(BUNDLE_INFO, bundle);
            startActivity(intent);
        }
    }

    @Override
    public String getKeyWords() {
        return mFragmentInfoSearchEdittext.getText().toString();
    }

    @Override
    protected Long getMaxId(@NotNull List<InfoListDataBean> data) {
        return data.get(data.size() - 1).getId();
    }

    @Override
    protected void requestCacheData(Long maxId, boolean isLoadMore) {
        mRefreshlayout.setEnableLoadMore(false);
    }

    protected boolean searchContainerVisibility() {
        return true;
    }
}
