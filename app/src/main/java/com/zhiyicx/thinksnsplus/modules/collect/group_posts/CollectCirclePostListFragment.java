package com.zhiyicx.thinksnsplus.modules.collect.group_posts;

import android.os.Bundle;

import com.zhiyicx.thinksnsplus.data.beans.CirclePostListBean;
import com.zhiyicx.thinksnsplus.data.beans.MusicAlbumListBean;
import com.zhiyicx.thinksnsplus.data.source.repository.BaseCircleRepository;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.BaseCircleDetailFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @Author Jliuer
 * @Date 2017/07/24/10:56
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CollectCirclePostListFragment extends  BaseCircleDetailFragment {

    @Override
    protected boolean showPostFrom() {
        return true;
    }

    @Override
    protected boolean showToolMenu() {
        return false;
    }

    @Override
    protected boolean showCommentList() {
        return false;
    }

    /**
     * 是否已经加载过网络数据了
     */
    private boolean mIsLoadedNetData = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && mPresenter != null && !mIsLoadedNetData) {
            startRefrsh();
            mIsLoadedNetData = true;
        }
    }

    @Override
    protected boolean showToolBarDivider() {
        return false;
    }

    public static CollectCirclePostListFragment newInstance(BaseCircleRepository.CircleMinePostType circleMinePostType) {
        CollectCirclePostListFragment circleDetailFragment = new CollectCirclePostListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CIRCLE_TYPE, circleMinePostType);
        circleDetailFragment.setArguments(bundle);
        return circleDetailFragment;
    }
}
