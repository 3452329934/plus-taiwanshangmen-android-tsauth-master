package com.zhiyicx.thinksnsplus.modules.draftbox.adapter;

import android.app.Activity;

import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.BaseDraftBean;
import com.zhiyicx.thinksnsplus.data.beans.InfoDraftBean;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/09/21/10:41
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class InfoDraftItem extends BaseDraftItem<InfoDraftBean> {

    public InfoDraftItem(Activity activity) {
        super(activity);
    }

    @Override
    protected String setCreateTime(InfoDraftBean draftBean) {
        return draftBean.getCreate_at();
    }

    @Override
    protected String editeType() {
        return mActivity.getString(R.string.edit_draft_info);
    }

    @Override
    protected String setTitle(InfoDraftBean draftBean) {
        return draftBean.getInfoPublishBean().getTitle();
    }

    @Override
    public boolean isForViewType(BaseDraftBean item, int position) {
        return item instanceof InfoDraftBean;
    }
}
