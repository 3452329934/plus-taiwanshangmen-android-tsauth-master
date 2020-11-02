package com.zhiyicx.thinksnsplus.modules.collect.qa;

import android.os.Bundle;

import com.zhiyicx.baseproject.base.BaseListBean;
import com.zhiyicx.thinksnsplus.data.beans.AnswerInfoBean;
import com.zhiyicx.thinksnsplus.modules.q_a.mine.answer.MyAnswerFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.zhiyicx.thinksnsplus.modules.q_a.mine.container.MyQuestionActivity.BUNDLE_MY_QUESTION_TYPE;

/**
 * @Author Jliuer
 * @Date 2018/01/04/10:34
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class CollectAnswerListFragment extends MyAnswerFragment {

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

    public static CollectAnswerListFragment instance(String type) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_MY_QUESTION_TYPE, type);
        CollectAnswerListFragment fragment = new CollectAnswerListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
