package com.zhiyicx.thinksnsplus.widget.chat;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easeui.widget.presenter.EaseChatRowPresenter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhiyicx.thinksnsplus.R;

public class EaseChatMessageList extends RelativeLayout {

    protected static final String TAG = "EaseChatMessageList";
    protected ListView listView;
    protected SmartRefreshLayout swipeRefreshLayout;
    protected Context context;
    protected EMConversation conversation;
    protected int chatType;
    protected String toChatUsername;
    protected EaseMessageAdapter messageAdapter;

    protected EaseMessageListItemStyle itemStyle;

    public EaseChatMessageList(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseChatMessageList(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseStyle(context, attrs);
        init(context);
    }

    public EaseChatMessageList(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.ts_chat_message_list, this);
        swipeRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshlayout);
        listView = (ListView) findViewById(R.id.list);
        View view = new View(getContext());
        view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelOffset(R.dimen
                .chat_bottom_footer_height)));
        listView.addFooterView(view);
    }

    /**
     * init widget
     *
     * @param toChatUsername
     * @param chatType
     * @param customChatRowProvider
     */
    public void init(String toChatUsername, int chatType, EaseCustomChatRowProvider customChatRowProvider, EaseChatRow.OnTipMsgClickListener
            onTipMsgClickListener,EaseChatRowPresenter.MessageErrorListener messageErrorListener) {
        this.chatType = chatType;
        this.toChatUsername = toChatUsername;
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername, EaseCommonUtils.getConversationType(chatType), true);
        messageAdapter = new EaseMessageAdapter(context, toChatUsername, chatType, listView, onTipMsgClickListener);
        messageAdapter.setMessageErrorListener(messageErrorListener);
        messageAdapter.setItemStyle(itemStyle);
        messageAdapter.setCustomChatRowProvider(customChatRowProvider);
        // set message adapter AbsListView
        listView.setAdapter(messageAdapter);

        refreshSelectLast();
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //停止加载图片
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    Glide.with(getContext()).pauseRequests();
                } else {
                    //开始加载图片
                    Glide.with(getContext()).resumeRequests();
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    protected void parseStyle(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatMessageList);
        EaseMessageListItemStyle.Builder builder = new EaseMessageListItemStyle.Builder();
        builder.showAvatar(ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserAvatar, true))
                .showUserNick(ta.getBoolean(R.styleable.EaseChatMessageList_msgListShowUserNick, false))
                .myBubbleBg(ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground))
                .otherBuddleBg(ta.getDrawable(R.styleable.EaseChatMessageList_msgListMyBubbleBackground));

        itemStyle = builder.build();
        ta.recycle();
    }


    /**
     * refresh
     */
    public void refresh() {
        if (messageAdapter != null) {
            messageAdapter.refresh();
        }
    }

    /**
     * refresh and jump to the last
     */
    public void refreshSelectLast() {
        if (messageAdapter != null) {
            messageAdapter.refreshSelectLast();
        }
    }

    /**
     * refresh and jump to the position
     *
     * @param position
     */
    public void refreshSeekTo(int position) {
        if (messageAdapter != null) {
            messageAdapter.refreshSeekTo(position);
        }
    }

    public ListView getListView() {
        return listView;
    }

    public SmartRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }

    public EMMessage getItem(int position) {
        return messageAdapter.getItem(position);
    }

    public void setShowUserNick(boolean showUserNick) {
        itemStyle.setShowUserNick(showUserNick);
    }

    public boolean isShowUserNick() {
        return itemStyle.isShowUserNick();
    }


    public interface MessageListItemClickListener extends com.hyphenate.easeui.widget.EaseChatMessageList.MessageListItemClickListener{

    }

    /**
     * set click listener
     *
     * @param listener
     */
    public void setItemClickListener(MessageListItemClickListener listener) {
        if (messageAdapter != null) {
            messageAdapter.setItemClickListener(listener);
        }
    }

    /**
     * set chat row provider
     *
     * @param rowProvider
     */
    public void setCustomChatRowProvider(EaseCustomChatRowProvider rowProvider) {
        if (messageAdapter != null) {
            messageAdapter.setCustomChatRowProvider(rowProvider);
        }
    }
}
