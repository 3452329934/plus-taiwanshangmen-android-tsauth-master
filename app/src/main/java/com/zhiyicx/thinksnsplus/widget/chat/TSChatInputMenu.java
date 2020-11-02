package com.zhiyicx.thinksnsplus.widget.chat;

import android.content.Context;
import android.util.AttributeSet;

import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.hyphenate.easeui.widget.EaseChatPrimaryMenuBase;
import com.hyphenate.easeui.widget.emojicon.EaseEmojiconMenu;
import com.zhiyi.emoji.EomjiSource;
import com.zhiyicx.thinksnsplus.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Catherine
 * @describe
 * @date 2018/1/5
 * @contact email:648129313@qq.com
 */

public class TSChatInputMenu extends EaseChatInputMenu {

    public TSChatInputMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TSChatInputMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TSChatInputMenu(Context context) {
        super(context);
    }

    @Override
    public EaseChatPrimaryMenuBase setPrimaryMenuView() {
        return (TSChatPrimaryMenu) layoutInflater.inflate(R.layout.view_ts_chat_primary_menu, null);
    }

    @Override
    public void init(List<EaseEmojiconGroupEntity> emojiconGroupList) {
        if (inited) {
            return;
        }
        // primary menu, use default if no customized one
        if (chatPrimaryMenu == null) {
            chatPrimaryMenu = setPrimaryMenuView();
        }
        primaryMenuContainer.addView(chatPrimaryMenu);

        // emojicon menu, use default if no customized one
        if (emojiconMenu == null) {
            emojiconMenu = (EaseEmojiconMenu) layoutInflater.inflate(com.hyphenate.easeui.R.layout.ease_layout_emojicon_menu, null);
            if (emojiconGroupList == null) {
                emojiconGroupList = new ArrayList<>();
                emojiconGroupList.add(new EaseEmojiconGroupEntity(com.hyphenate.easeui.R.drawable.ee_1, getEmoji(), EaseEmojicon.Type.UNICODE));
            }
            ((EaseEmojiconMenu) emojiconMenu).init(emojiconGroupList);
        }
        emojiconMenuContainer.addView(emojiconMenu);

        processChatMenu();
        chatExtendMenu.init();

        inited = true;
    }

    private List<EaseEmojicon> getEmoji() {
        Map<String, String> map = EomjiSource.emojiMap(EomjiSource.getJson("emoji.json", getContext()));
        List<String> names, values;
        names = new ArrayList<>();
        values = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String result = entry.getKey().replaceAll("\"", "");
            result = result.replaceAll("\\s*", "");
            names.add(result);
            values.add(entry.getValue());
        }
        return createData(names, values);
    }

    private List<EaseEmojicon> createData(List<String> names, List<String> values) {
        List<EaseEmojicon> result = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            EaseEmojicon data = new EaseEmojicon(names.get(i), values.get(i), EaseEmojicon.Type.UNICODE);
            result.add(data);
        }
        return result;
    }
}
