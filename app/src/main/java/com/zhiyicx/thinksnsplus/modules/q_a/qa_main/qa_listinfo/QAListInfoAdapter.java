package com.zhiyicx.thinksnsplus.modules.q_a.qa_main.qa_listinfo;

import android.content.Context;

import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.qa.QAListInfoBean;

import java.util.List;

/**
 * @Author Jliuer
 * @Date 2017/08/15/15:44
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class QAListInfoAdapter extends QAListBaseInfoAdapter<QAListInfoBean> {

    public QAListInfoAdapter(Context context, List<QAListInfoBean> datas) {
        super(context, R.layout.item_qa_content, datas);
    }

}
