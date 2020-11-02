package com.zhiyicx.thinksnsplus.modules.information.publish.detail;

import com.zhiyicx.thinksnsplus.data.beans.BaseDraftBean;
import com.zhiyicx.thinksnsplus.data.beans.InfoDraftBean;
import com.zhiyicx.thinksnsplus.data.source.local.InfoDraftBeanGreenDaoImpl;
import com.zhiyicx.thinksnsplus.modules.markdown_editor.MarkdownPresenter;

import javax.inject.Inject;

/**
 * @Author Jliuer
 * @Date 2018/01/24/11:14
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class EditeInfoDetailPresenter extends MarkdownPresenter<EditeInfoDetailContract.View> implements EditeInfoDetailContract.Presenter {

    @Inject
    InfoDraftBeanGreenDaoImpl mInfoDraftBeanGreenDao;

    @Inject
    public EditeInfoDetailPresenter(EditeInfoDetailContract.View rootView) {
        super(rootView);
    }

    @Override
    public void saveDraft(BaseDraftBean postDraftBean) {
        if (postDraftBean instanceof InfoDraftBean) {
            InfoDraftBean draft = (InfoDraftBean) postDraftBean;
            mInfoDraftBeanGreenDao.insertOrReplace(draft);
        }
    }
}
