package com.zhiyicx.thinksnsplus.modules.register.rule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zhiyicx.baseproject.base.TSFragment;
import com.zhiyicx.baseproject.config.ApiConfig;
import com.zhiyicx.thinksnsplus.R;

import br.tiagohm.markdownview.MarkdownView;
import butterknife.BindView;

import static com.zhiyicx.thinksnsplus.data.beans.ProtrolBean.TYPE_PRIVACY_AGREEMENT;
import static com.zhiyicx.thinksnsplus.data.beans.ProtrolBean.TYPE_USER_AGREEMENT;
import static com.zhiyicx.thinksnsplus.data.beans.ProtrolBean.TYPE_VIP_PROTROL;

/**
 * @Author Jliuer
 * @Date 2017/10/27/13:48
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class UserRuleFragment extends TSFragment {

    public static final String BUNDLE_RULE_TYPE = "bundle_rule_type";

    @BindView(R.id.md_user_rule)
    MarkdownView mMarkdownView;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.scroll_view)
    View mScrollContainer;

    String mRuleType = TYPE_USER_AGREEMENT;

    @Override
    protected String setCenterTitle() {
        switch (mRuleType) {
            case TYPE_PRIVACY_AGREEMENT:
                return getString(R.string.privite_protrol2);

            case TYPE_VIP_PROTROL:

                return getString(R.string.member_service_protrol);

            case TYPE_USER_AGREEMENT:
            default:
                return getString(R.string.user_rule_register);

        }
    }

    @Override
    protected boolean showToolBarDivider() {
        return true;
    }

    public static UserRuleFragment newInstance(Bundle bundle) {
        UserRuleFragment fragment = new UserRuleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRuleType = getArguments().getString(BUNDLE_RULE_TYPE, TYPE_USER_AGREEMENT);
        }

    }

    @Override
    protected void initView(View rootView) {
        switch (mRuleType) {
            case TYPE_PRIVACY_AGREEMENT:
                mMarkdownView.setVisibility(View.VISIBLE);
                mMarkdownView.loadUrl(ApiConfig.APP_USR_PROTROL + TYPE_PRIVACY_AGREEMENT);
                break;

            case TYPE_USER_AGREEMENT:
                mMarkdownView.setVisibility(View.VISIBLE);
                mMarkdownView.loadUrl(ApiConfig.APP_USR_PROTROL + TYPE_USER_AGREEMENT);

                break;
            default:
                if (!TextUtils.isEmpty(mRuleType) && mRuleType.contains("#")) {
                    // 使用markdonw
                    mMarkdownView.setVisibility(View.VISIBLE);
                    mMarkdownView.loadMarkdown(mRuleType);
                } else {
                    // 普通文本
                    mScrollContainer.setVisibility(View.VISIBLE);
                    mTvContent.setText(mRuleType);
                }
        }


    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_user_rule;
    }
}
