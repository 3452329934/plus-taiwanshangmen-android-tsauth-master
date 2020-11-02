package com.zhiyicx.thinksnsplus.modules.settings.aboutus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.common.config.MarkdownConfig;
import com.zhiyicx.common.utils.DeviceUtils;
import com.zhiyicx.thinksnsplus.base.AppApplication;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.CircleDetailActivity;
import com.zhiyicx.thinksnsplus.modules.circle.detailv2.post.CirclePostDetailActivity;
import com.zhiyicx.thinksnsplus.modules.dynamic.detail.DynamicDetailActivity;
import com.zhiyicx.thinksnsplus.modules.guide.GuideActivity;
import com.zhiyicx.thinksnsplus.modules.guide.GuideFragment;
import com.zhiyicx.thinksnsplus.modules.information.infodetails.InfoDetailsActivity;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.answer.AnswerDetailsActivity;
import com.zhiyicx.thinksnsplus.modules.q_a.detail.question.QuestionDetailActivity;
import com.zhiyicx.thinksnsplus.modules.register.RegisterPresenter;
import com.zhiyicx.thinksnsplus.modules.topic.detail.TopicDetailActivity;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Describe 关于我们等网页
 * @Author Jungle68
 * @Date 2017/1/10
 * @Contact master.jungle68@gmail.com
 */

public class CustomWEBActivity extends TSActivity<RegisterPresenter, CustomWEBFragment> {

    private static String flag = "";

    /**
     * 广告跳转
     *
     * @param context
     * @param args
     */
    public static void startToWEBActivity(Context context, String... args) {
        startToWEBActivity(context, null, args);
    }

    /**
     * 网页跳转
     *
     * @param context
     * @param headers
     * @param args    args[0] url ; args[1] title
     */
    public static void startToWEBActivity(Context context, HashMap<String, String> headers, String... args) {
        flag = "";
        Intent intent = new Intent(context, CustomWEBActivity.class);
        Bundle bundle = new Bundle();
        if (args.length > 0) {
            try {
                String url = args[0];
                // 广告需要 token
                url = url.replace("__token__", AppApplication.getTOKEN().replace(" ", "|"));
                bundle.putString(CustomWEBFragment.BUNDLE_PARAMS_WEB_URL, url);
                bundle.putString(CustomWEBFragment.BUNDLE_PARAMS_WEB_TITLE, args[1]);
                if (headers != null) {
                    bundle.putSerializable(CustomWEBFragment.BUNDLE_PARAMS_WEB_HEADERS, headers);
                }
                flag = args[2];
            } catch (Exception e) {
                e.printStackTrace();
            }
            intent.putExtras(bundle);
        }
        String url = bundle.getString(CustomWEBFragment.BUNDLE_PARAMS_WEB_URL, "");
        String[] advertType = getAdvertType(url);
        if (!TextUtils.isEmpty(advertType[0]) && TextUtils.isEmpty(flag)) {
            Long id = null;
            try {
                id = Long.parseLong(advertType[1]);
            } catch (Exception e) {
                context.startActivity(intent);
                return;
            }
            switch (advertType[0]) {
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC:
                    DynamicDetailActivity.startDynamicDetailActivity(context, id);
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO:
                    InfoDetailsActivity.startInfoDetailsActivity(context, id);
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_CIRCLE:
                    CircleDetailActivity.startCircleDetailActivity(context, id);
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST:
                    Long pistId = null;
                    try {
                        pistId = Long.parseLong(advertType[2]);
                    } catch (Exception e) {
                        context.startActivity(intent);
                        return;
                    }
                    CirclePostDetailActivity.startCirclePostDetailActivity(context, id, pistId, false);
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION:
                    QuestionDetailActivity.startQuestionDetailActivity(context, id);
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION_TOPIC:
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_ANSWERS:
                    AnswerDetailsActivity.startAnswerDetailsActivity(context, id);
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_TOPIC:
                    TopicDetailActivity.startTopicDetailActivity(context, id);
                    break;

                default:
            }
            return;
        }
        context.startActivity(intent);

    }

    /**
     * 跳转浏览器网页
     *
     * @param context
     * @param url
     */
    public static void startToOutWEBActivity(Context context, String url) {

        try {
            // 广告需要 token
            url = url.replace("__token__", AppApplication.getTOKEN());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] advertType = getAdvertType(url);
        if (!TextUtils.isEmpty(advertType[0]) && TextUtils.isEmpty(flag)) {
            Long id = null;
            try {
                id = Long.parseLong(advertType[1]);
            } catch (Exception e) {
                gotoOutSideWeb(url,context);
                return;
            }
            switch (advertType[0]) {
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC:
                    DynamicDetailActivity.startDynamicDetailActivity(context, id);
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO:
                    InfoDetailsActivity.startInfoDetailsActivity(context, id);
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_CIRCLE:
                    CircleDetailActivity.startCircleDetailActivity(context, id);
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST:
                    Long pistId = null;
                    try {
                        pistId = Long.parseLong(advertType[2]);
                    } catch (Exception e) {
                        gotoOutSideWeb(url,context);
                        return;
                    }
                    CirclePostDetailActivity.startCirclePostDetailActivity(context, id, pistId, false);
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION:
                    QuestionDetailActivity.startQuestionDetailActivity(context, id);
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION_TOPIC:
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_ANSWERS:
                    AnswerDetailsActivity.startAnswerDetailsActivity(context, id);
                    break;
                case TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_TOPIC:
                    TopicDetailActivity.startTopicDetailActivity(context, id);
                    break;

                default:
            }
        }else{
            gotoOutSideWeb(url,context);
        }
    }

    private static void gotoOutSideWeb(String url,Context context){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        String urlRege = "^http://[\\s\\S]+";
        if (!url.matches(MarkdownConfig.NETSITE_FORMAT) && !url.matches(urlRege)) {
            url = url.replace(MarkdownConfig.SCHEME_ZHIYI, "");
            url = MarkdownConfig.SCHEME_HTTP + url;
        }
        intent.setData(Uri.parse(url));
        if (!DeviceUtils.hasPreferredApplication(context, intent)) {
            intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
        }
        context.startActivity(intent);
    }

    @Override
    protected void componentInject() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (GuideFragment.ADVERT.equals(flag)) {
            finish();
            startActivity(new Intent(this, GuideActivity.class));
        }
    }

    @Override
    protected CustomWEBFragment getFragment() {
        return CustomWEBFragment.newInstance(getIntent().getExtras());
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    /**
     * 覆盖系统的回退键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mContanierFragment.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private static String[] getAdvertType(String url) {
        Matcher dynamic = Pattern.compile(ApiConfig.ADVERT_DYNAMIC).matcher(url);
        Matcher info = Pattern.compile(ApiConfig.ADVERT_INFO).matcher(url);
        Matcher circle = Pattern.compile(ApiConfig.ADVERT_CIRCLE).matcher(url);
        Matcher post = Pattern.compile(ApiConfig.ADVERT_POST).matcher(url);
        Matcher question = Pattern.compile(ApiConfig.ADVERT_QUESTION).matcher(url);
        Matcher question_topic = Pattern.compile(ApiConfig.ADVERT_QUESTION_TOPIC).matcher(url);
        Matcher answer = Pattern.compile(ApiConfig.ADVERT_ANSWER).matcher(url);
        Matcher topic = Pattern.compile(ApiConfig.ADVERT_TOPIC).matcher(url);
        String[] result = new String[3];
        if (dynamic.find()) {
            result[0] = TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_DYNAMIC;
            result[1] = dynamic.group(1);
        } else if (info.find()) {
            result[0] = TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_INFO;
            result[1] = info.group(1);
        } else if (circle.find()) {
            result[0] = TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_CIRCLE;
            result[1] = circle.group(1);
        } else if (post.find()) {
            result[0] = TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_POST;
            result[1] = post.group(1);
            result[2] = post.group(2);
        } else if (question.find()) {
            result[0] = TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION;
            result[1] = question.group(1);
        } else if (answer.find()) {
            result[0] = TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_ANSWERS;
            result[1] = answer.group(1);
        } else if (topic.find()) {
            result[0] = TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_TOPIC;
            result[1] = topic.group(1);
        }

//        else if (question_topic.find()) {
//            result[0] = TSEMConstants.BUNDLE_CHAT_MESSAGE_FORWARD_TYPE_QUESTION_TOPIC;
//            result[1] = question_topic.group(1);
//        }
        return result;
    }
}
