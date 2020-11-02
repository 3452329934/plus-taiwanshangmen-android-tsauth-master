package com.zhiyicx.thinksnsplus.modules.circle.publish.choose_circle;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.modules.circle.mine.joined.BaseCircleListPresenter;

/**
 * @author Jliuer
 * @Date 2017/11/28/14:31
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class ChooseCircleActivity extends TSActivity<BaseCircleListPresenter, ChooseCircleFragment> {
    @Override
    protected ChooseCircleFragment getFragment() {
        return ChooseCircleFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void componentInject() {

    }
}
