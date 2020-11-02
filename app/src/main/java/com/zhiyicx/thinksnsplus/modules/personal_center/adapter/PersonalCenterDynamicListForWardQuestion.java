package com.zhiyicx.thinksnsplus.modules.personal_center.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.klinker.android.link_builder.Link;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.widget.textview.SpanTextViewWithEllipsize;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.common.utils.ConvertUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.DynamicDetailBeanV2;
import com.zhiyicx.thinksnsplus.data.beans.Letter;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.answer.AnswerDetailsActivity;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.question.QuestionDetailActivity;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.zhiyicx.common.config.ConstantConfig.JITTER_SPACING_TIME;
import static com.zhiyicx.thinksnsplus.data.beans.DynamicListAdvert.DEFAULT_ADVERT_FROM_TAG;

/**
 * @author LiuChao
 * @describe
 * @date 2017/3/10
 * @contact email:450127106@qq.com
 */

public class PersonalCenterDynamicListForWardQuestion extends PersonalCenterDynamicListBaseItem {
    public PersonalCenterDynamicListForWardQuestion(Context context) {
        super(context);
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_personal_center_dynamic_list_forward_question;
    }

    @Override
    public boolean isForViewType(DynamicDetailBeanV2 item, int position) {
        Letter letter = item.getMLetter();
        return item.getFeed_mark() != null &&
                item.getFeed_from() != DEFAULT_ADVERT_FROM_TAG &&
                (item.getImages() == null || item.getImages().isEmpty()) &&
                item.getVideo() == null &&
                letter != null &&
                TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION.equals(letter.getType());
    }

    @Override
    public void convert(ViewHolder holder, final DynamicDetailBeanV2 dynamicBean, DynamicDetailBeanV2 lastT, int position, int itemCounts) {
        super.convert(holder, dynamicBean, lastT, position,itemCounts);
        holder.setText(R.id.tv_forward_name, dynamicBean.getMLetter().getName());
        SpanTextViewWithEllipsize forwardContentView = holder.getView(R.id.tv_forward_content);
        forwardContentView.setText(dynamicBean.getMLetter().getContent());
        forwardContentView.post(() -> forwardContentView.updateForRecyclerView(forwardContentView.getText(),
                forwardContentView.getWidth()));

        List<Link> links = setLiknks(dynamicBean, dynamicBean.getMLetter().getContent());

//        Link link = new Link(MarkdownConfig.IMAGE_REPLACE)
//                .setTextColor(ContextCompat.getColor(mContext, R.color.themeColor))
//                .setUnderlined(false)
//                .setTextColorOfHighlightedLink(ContextCompat.getColor(mContext, R.color.normal_for_dynamic_list_content));
//        links.add(link);

        ConvertUtils.stringLinkConvert(forwardContentView, links, false);
        RxView.clicks(holder.getView(R.id.tv_forward_container))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> QuestionDetailActivity.startQuestionDetailActivity(mContext,
                        Long.parseLong(dynamicBean.getMLetter().getId())));
        RxView.clicks(holder.getView(R.id.tv_forward_name))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> QuestionDetailActivity.startQuestionDetailActivity(mContext,
                        Long.parseLong(dynamicBean.getMLetter().getId())));
        RxView.clicks(holder.getView(R.id.tv_forward_content))
                .throttleFirst(JITTER_SPACING_TIME, TimeUnit.SECONDS)
                .subscribe(aVoid -> QuestionDetailActivity.startQuestionDetailActivity(mContext,
                        Long.parseLong(dynamicBean.getMLetter().getId())));
    }
}
