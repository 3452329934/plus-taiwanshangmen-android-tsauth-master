package com.zhiyicx.thinksnsplus.modules.home.message.messagegroup;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easeui.EaseConstant;
import com.zhiyicx.baseproject.base.TSListFragment;
import com.zhiyicx.baseproject.em.manager.eventbus.TSEMConnectionEvent;
import com.zhiyicx.baseproject.em.manager.util.TSEMConstants;
import com.zhiyicx.baseproject.impl.imageloader.glide.transformation.GlideCircleTransform;
import com.zhiyicx.common.utils.ToastUtils;
import com.zhiyicx.common.utils.log.LogUtils;
import com.zhiyicx.thinksnsplus.R;
import com.zhiyicx.thinksnsplus.data.beans.ChatGroupBeanForGroupList;
import com.zhiyicx.thinksnsplus.modules.chat.ChatActivity;
import com.zhiyicx.thinksnsplus.widget.TSSearchView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.jetbrains.annotations.NotNull;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @Author Jliuer
 * @Date 2018/05/03/15:37
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class MessageGroupListFragment extends TSListFragment<MessageGroupContract.Presenter, ChatGroupBeanForGroupList>
        implements MessageGroupContract.View {

    @BindView(R.id.searchView)
    TSSearchView mSearchView;
    private List<ChatGroupBeanForGroupList> cache;

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    protected void initView(View rootView) {
        super.initView(rootView);
    }

    @Override
    protected float getItemDecorationSpacing() {
        return 0;
    }

    @Override
    protected String setCenterTitle() {
        return getString(R.string.chat_group);
    }

    @Override
    protected boolean isLoadingMoreEnable() {
        return true;
    }

    @Override
    protected boolean isNeedRefreshDataWhenComeIn() {
        return true;
    }

    @Override
    protected void initData() {
        super.initData();
        initListener();

        // 刷新信息内容
        getGroupListData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mSearchView.getText())) {
            mSearchView.setText("");
        }
    }

    @Override
    public void onNetResponseSuccess(@NotNull List<ChatGroupBeanForGroupList> data, boolean isLoadMore) {
        boolean notSearch = TextUtils.isEmpty(getsearchKeyWord());
        if (notSearch) {
            if (cache == null) {
                cache = new ArrayList<>(data);
            } else {
                if (!isLoadMore) {
                    cache.clear();
                }
                cache.addAll(data);
            }
            mSearchView.setVisibility(data.isEmpty() ? View.GONE : View.VISIBLE);
        } else {
            mSearchView.setVisibility(View.VISIBLE);
        }
        super.onNetResponseSuccess(data, isLoadMore);
        if (!notSearch && mListDatas.isEmpty()) {
            setEmptyViewVisiable(false);
        }
    }

    @Override
    protected boolean showNoMoreData() {
        return super.showNoMoreData() && TextUtils.isEmpty(getsearchKeyWord());
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        CommonAdapter adapter = new CommonAdapter<ChatGroupBeanForGroupList>(getActivity(), R.layout.item_group_list, mListDatas) {
            @Override
            protected void convert(ViewHolder holder, ChatGroupBeanForGroupList chatGroupBean, int position) {
                holder.setText(R.id.tv_group_name, chatGroupBean.getName());
                holder.setVisible(R.id.tv_onwer_tag, chatGroupBean.isOwner() ? View.VISIBLE : View.GONE);
                Glide.with(mContext)
                        .load(TextUtils.isEmpty(chatGroupBean.getGroup_face()) ? R.mipmap.ico_ts_assistant : chatGroupBean
                                .getGroup_face())
                        .error(R.mipmap.ico_ts_assistant)
                        .placeholder(R.mipmap.ico_ts_assistant)
                        .transform(new GlideCircleTransform(mContext))
                        .into(holder.getImageViwe(R.id.uv_group_head));
            }
        };
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                ChatGroupBeanForGroupList groupBean = mListDatas.get(position);
                mPresenter.checkGroupExist(groupBean);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        return adapter;
    }

    @Override
    public void checkGroupExist(ChatGroupBeanForGroupList groupBean, EMGroup data) {
        if (data != null) {
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(groupBean.getId(), EMConversation.EMConversationType.GroupChat, true);
            ChatActivity.startChatActivity(mActivity, conversation.conversationId(), EaseConstant.CHATTYPE_GROUP);
            mActivity.finish();
        } else {
            ToastUtils.showLongToast("error");
        }

    }

    @Override
    protected int getBodyLayoutId() {
        return R.layout.fragment_message_grouplist;
    }

    @Override
    public String getsearchKeyWord() {
        return mSearchView.getText().toString().trim();
    }

    @Subscriber(mode = ThreadMode.MAIN)
    public void onTSEMConnectionEventBus(TSEMConnectionEvent event) {
        LogUtils.d("onTSEMConnectionEventBus");
        switch (event.getType()) {
            case TSEMConstants.TS_CONNECTION_USER_LOGIN_OTHER_DIVERS:
                break;
            case TSEMConstants.TS_CONNECTION_USER_REMOVED:
                break;
            case TSEMConstants.TS_CONNECTION_CONNECTED:
                hideStickyMessage();
                getGroupListData();
                break;
            case TSEMConstants.TS_CONNECTION_DISCONNECTED:
                showStickyMessage(getString(R.string.chat_unconnected));
                break;
            default:
        }
    }

    private void initListener() {

        mSearchView.setOnSearchClickListener(new TSSearchView.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mPresenter == null) {
                    return;
                }
                filterData(s.toString().trim());
            }
        });
    }

    /**
     * 获取
     *
     * @param filterStr
     */
    private void filterData(CharSequence filterStr) {
        if (cache.isEmpty()) {
            return;
        }
        if (TextUtils.isEmpty(filterStr)) {
            mListDatas.clear();
            mListDatas.addAll(cache);
        } else {
            List<ChatGroupBeanForGroupList> result = new ArrayList<>();
            for (ChatGroupBeanForGroupList sortModel : cache) {
                String name = sortModel.getName();
                boolean isContent = !TextUtils.isEmpty(name) && name.toLowerCase().contains(filterStr.toString().toLowerCase());
                if (isContent) {
                    result.add(sortModel);
                }
            }
            mListDatas.clear();
            mListDatas.addAll(result);
        }
        refreshData();
        if (mListDatas.isEmpty()) {
            setEmptyViewVisiable(false);
        }
    }

    /**
     * 获取群列表
     */
    private void getGroupListData() {
        if (mPresenter != null) {
            autoRefresh(0);
        }
    }

    @Override
    protected void requestNetData(Long maxId, boolean isLoadMore) {
        if (maxId == 0) {
            maxId = (long) DEFAULT_PAGE_DB_SIZE;
        }
        super.requestNetData(maxId, isLoadMore);
    }

    @Override
    protected Long getMaxId(@NotNull List<ChatGroupBeanForGroupList> data) {
        if (DEFAULT_PAGE_SIZE == null) {
            return (long) DEFAULT_PAGE_DB_SIZE;
        } else {
            return (long) DEFAULT_PAGE_SIZE;
        }
    }
}
