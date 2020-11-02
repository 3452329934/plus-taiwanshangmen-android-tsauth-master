package com.zhiyicx.thinksnsplus.modules.circle.publish.choose_circle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.common.utils.SkinUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.CircleInfo;
import com.zhiyicx.thinksnsplus.data.source.remote.CircleClient;
import com.zhiyicx.thinksnsplus.modules.circle.main.CircleMainActivity;
import com.zhiyicx.thinksnsplus.modules.circle.mine.joined.MyJoinedCircleFragment;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * @author Jliuer
 * @Date 2017/11/28/14:28
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ChooseCircleFragment extends MyJoinedCircleFragment {

    public static final String BUNDLE_CIRCLE = "circle";
    private static final int DEFAULT_COLUMN = 4;

    public static final int CHOOSE_CIRCLE = 1994;

    @BindView(R.id.ll_empty)
    LinearLayout mLlEmpty;
    @BindView(R.id.ll_container)
    View mLLContainer;
    @BindView(R.id.tv_tip)
    TextView mTvTip;
    @BindView(R.id.bt_do)
    Button mBtDo;

    public static ChooseCircleFragment newInstance(Bundle bundle) {
        ChooseCircleFragment fragment = new ChooseCircleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsNeedToolBar = true;
    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_post_publish_choose_circle;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.select_circle);
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
        mTvTip.setText(getString(R.string.not_find_joined_circle));
        mBtDo.setText(getString(R.string.join_circle));
    }

    @Override
    public void toCircleDetail(CircleInfo circleInfo) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_CIRCLE, circleInfo);
        intent.putExtras(bundle);
        getActivity().setResult(RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public CircleClient.MineCircleType getMineCircleType() {
        return CircleClient.MineCircleType.ALLOW;
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<CircleInfo> data, boolean isLoadMore) {
        if (data.isEmpty()) {
            hideRefreshState(isLoadMore);
            mLlEmpty.setVisibility(View.VISIBLE);
            mRvList.setVisibility(View.GONE);
            mLLContainer.setBackgroundColor(SkinUtils.getColor(R.color.white));
            return;
        }else {
            mLLContainer.setBackgroundColor(SkinUtils.getColor(R.color.bgColor));
        }
        mRvList.setVisibility(View.VISIBLE);
        mLlEmpty.setVisibility(View.GONE);
        super.onNetResponseSuccess(data, isLoadMore);
    }

    @OnClick(R.id.bt_do)
    public void onViewClicked() {
        startActivity(new Intent(getActivity(), CircleMainActivity.class));
        mActivity.finish();
    }

}
