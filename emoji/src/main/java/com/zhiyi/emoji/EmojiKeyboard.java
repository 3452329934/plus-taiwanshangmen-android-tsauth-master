package com.zhiyi.emoji;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @author Jliuer
 * @Date 18/09/04 16:38
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class EmojiKeyboard extends LinearLayout {

    private Context context;

    private LinearLayout mLinearLayoutEmojiContainer;

    private ViewPager mEmojiViewpager;

    private RecyclerView mEmojiClass;

    private EmojiIndicatorLinearLayout mEmojiIndicatorLinearLayout;

    private BottomClassAdapter mBottomClassAdapter;
    private EditText mEditText;

    private EmojiAdapter mEmojiAdapter;

    /**
     * 数据源
     */
    private List<List<String>> lists;
    private List<List<String>> names;
    private List<Map<String, String>> emojiMap;

    /**
     * 底部图标信息
     */
    private List<Drawable> tips = new ArrayList<>();

    /**
     * 输入器分页情况
     */
    private List<Integer> listInfo = new ArrayList<>();

    /**
     * 行数
     */
    int maxLinex = 3;

    /**
     * 列数
     */
    int maxColumns = 8;

    /**
     * 字体大小
     */
    private int emojiSize = 26;

    /**
     * 底部指示器距离
     */
    private int indicatorPadding = 10;

    /**
     * 当前选择页面
     */
    private int itemIndex = 0;

    /**
     * 当前条目页面最小位置
     */
    private int minItemIndex;

    /**
     * 当前条目页面最大位置
     */
    private int maxItemIndex;

    /**
     * 页面宽度
     */
    private int maxViewWidth;


    public EmojiKeyboard(Context context) {
        super(context);
        this.context = context;
    }

    public EmojiKeyboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setLists(List<List<String>> lists) {
        this.lists = lists;
    }

    public void setEmojiLists(List<Map<String, String>> maps) {
        this.emojiMap = maps;
        if (lists == null) {
            lists = new ArrayList<>();
            names = new ArrayList<>();
        }
        lists.clear();
        names.clear();
        for (Map<String, String> map : maps) {
            List<String> name = new ArrayList<>();
            List<String> value = new ArrayList<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String result = entry.getKey().replaceAll("\"", "");
                result = result.replaceAll("\\s*", "");
                name.add(result);
                value.add(entry.getValue());
            }
            lists.add(name);
            names.add(value);
        }
    }

    public void setTips(List<Drawable> tips) {
        this.tips = tips;
    }

    public void setEditText(EditText editText) {
        this.mEditText = editText;
    }

    boolean init = true;

    public void init() {
        initView();
    }


    public void setEmojiSize(int emojiSize) {
        this.emojiSize = emojiSize;
    }

    public void setIndicatorPadding(int indicatorPadding) {
        this.indicatorPadding = indicatorPadding;
    }

    public void setMaxLines(int maxLinex) {
        this.maxLinex = maxLinex;
    }

    public void setMaxColumns(int maxColumns) {
        this.maxColumns = maxColumns;
    }

    private void initView() {
        if (mLinearLayoutEmojiContainer != null) {
            return;
        }
        LayoutInflater.from(context).inflate(R.layout.emoji_keyobard, this);
        //获取根布局对象
        mLinearLayoutEmojiContainer = (LinearLayout) findViewById(R.id.linearLayout_emoji);
        mEmojiViewpager = (ViewPager) findViewById(R.id.viewpager_emojikeyboard);
        mEmojiIndicatorLinearLayout = (EmojiIndicatorLinearLayout) findViewById(R.id.emojiIndicatorLinearLayout_emoji);
        mEmojiClass = (RecyclerView) findViewById(R.id.recycleview_emoji_class);

        mEmojiViewpager.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                if (init) {
                    init = false;
                    maxViewWidth = mLinearLayoutEmojiContainer.getWidth();
                    mEmojiAdapter = new EmojiAdapter(context, lists, names, maxViewWidth, maxLinex, maxColumns, emojiSize);
                    // 通过构建后的EmojiAdapter获取底部指示器的范围

                    listInfo = mEmojiAdapter.getListInfo();
                    mEmojiViewpager.setAdapter(mEmojiAdapter);


                    minItemIndex = 0;
                    maxItemIndex = listInfo.get(itemIndex);

                    // 初始化底部指示器信息
                    mEmojiIndicatorLinearLayout.setMaxCount(listInfo.get(itemIndex));
                    mEmojiIndicatorLinearLayout.setPadding(0, ViewUtils.dip2px(context, indicatorPadding), 0, 0);


                    // 初始化底部icon
                    initBottomClass();
                    // 为ViewPager添加滑动监听
                    initViewPageChangeListener();
                    // 设置emoji点击的回调
                    initEmojiOnClick();
                }

                mEmojiViewpager.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        });
    }

    private void initBottomClass() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mEmojiClass.setLayoutManager(linearLayoutManager);

        if (tips.size() != 0 && listInfo.size() < tips.size()) {
            tips = tips.subList(0, listInfo.size());
        }

        mBottomClassAdapter = new BottomClassAdapter(context, tips, listInfo.size());
        mBottomClassAdapter.setItemOnClick(new BottomClassAdapter.ItemOnClick() {
            @Override
            public void itemOnClick(int position) {
                clickChangeBottomClass(position);
            }
        });
        mEmojiClass.setAdapter(mBottomClassAdapter);
    }

    private void initViewPageChangeListener() {
        mEmojiViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // 滑动后更新底部指示器
                touchChangeBottomClass(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * @param clickItemIndex 点击后更新指示器
     */
    private void clickChangeBottomClass(int clickItemIndex) {
        itemIndex = clickItemIndex;
        maxItemIndex = 0;
        minItemIndex = 0;
        for (int i = 0; i <= itemIndex; i++) {
            maxItemIndex += listInfo.get(i);
        }

        for (int i = 0; i < itemIndex; i++) {
            minItemIndex += listInfo.get(i);
        }


        mEmojiViewpager.setCurrentItem(minItemIndex);
        changeBottomClassIcon();


        mEmojiIndicatorLinearLayout.setMaxCount(listInfo.get(itemIndex));
        mEmojiIndicatorLinearLayout.setChoose(0);
    }

    /**
     * @param position 滑动后更新底部指示器
     */
    private void touchChangeBottomClass(int position) {
        //判断滑动是否越界
        if (position >= maxItemIndex) {
            itemIndex++;

            maxItemIndex = 0;
            minItemIndex = 0;
            for (int i = 0; i <= itemIndex; i++) {
                maxItemIndex += listInfo.get(i);
            }

            for (int i = 0; i < itemIndex; i++) {
                minItemIndex += listInfo.get(i);
            }

            mEmojiIndicatorLinearLayout.setMaxCount(listInfo.get(itemIndex));
            mEmojiIndicatorLinearLayout.setChoose(0);
        } else if (position < minItemIndex) {
            itemIndex--;

            maxItemIndex = 0;
            minItemIndex = 0;
            for (int i = 0; i <= itemIndex; i++) {
                maxItemIndex += listInfo.get(i);
            }

            for (int i = 0; i < itemIndex; i++) {
                minItemIndex += listInfo.get(i);
            }

            mEmojiIndicatorLinearLayout.setMaxCount(listInfo.get(itemIndex));
            mEmojiIndicatorLinearLayout.setChoose(listInfo.get(itemIndex) - 1);
        } else {
            position -= minItemIndex;

            mEmojiIndicatorLinearLayout.setChoose(position);
        }

        changeBottomClassIcon();
    }

    private void changeBottomClassIcon() {
        mBottomClassAdapter.changeBottomItem(itemIndex);


        int firstItem = mEmojiClass.getChildLayoutPosition(mEmojiClass.getChildAt(0));
        int lastItem = mEmojiClass.getChildLayoutPosition(mEmojiClass.getChildAt(mEmojiClass.getChildCount() - 1));


        if (itemIndex <= firstItem || itemIndex >= lastItem) {
            mEmojiClass.smoothScrollToPosition(itemIndex);
        }
    }


    private void initEmojiOnClick() {
        mEmojiAdapter.setEmojiOnClick(new EmojiAdapter.EmojiTextOnClick() {

            @Override
            public void onClick(String text, String name) {
                // 获取坐标位置及文本内容
                if (mOnEmojiClickLisenter != null) {
                    mOnEmojiClickLisenter.onEmojiClick(text, name);
                }
                if (mEditText == null) {
                    return;
                }
                int index = mEditText.getSelectionStart();
                Editable edit = mEditText.getEditableText();

                // 当点击删除按钮时text为 - 1
                if (text.equals("-1")) {
                    String str = mEditText.getText().toString();
                    if (!str.equals("")) {

                        // 只有一个字符
                        if (str.length() < 2) {
                            mEditText.getText().delete(index - 1, index);
                        } else if (index > 0) {
                            String lastText = str.substring(index - 2, index);
                            // 检测最后两个字符是否为一个emoji(emoji可能存在一个字符的情况 需要进行正则校验)
                            if (EmojiRegexUtil.checkEmoji(lastText)) {
                                mEditText.getText().delete(index - 2, index);
                            } else {
                                mEditText.getText().delete(index - 1, index);
                            }
                        }

                    }
                } else {
                    // 插入你内容
                    if (index < 0 || index >= edit.length()) {
                        edit.append(text);
                    } else {
                        edit.insert(index, text);
                    }
                }
            }
        });
    }


    public void reset() {
        if (mEmojiViewpager == null) {
            return;
        }
        mEmojiViewpager.setCurrentItem(0);
        setVisibility(GONE);
    }

    private OnEmojiClickLisenter mOnEmojiClickLisenter;

    public void setOnEmojiClickLisenter(OnEmojiClickLisenter lisenter) {
        mOnEmojiClickLisenter = lisenter;
    }

    public interface OnEmojiClickLisenter {
        void onEmojiClick(String text, String name);
    }

}
