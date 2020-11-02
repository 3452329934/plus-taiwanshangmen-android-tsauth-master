package com.zhiyi.emoji;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * ThinkSNS Plus
 * Copyright (c) 2018 Chengdu ZhiYiChuangXiang Technology Co., Ltd.
 *
 * @Author Jliuer
 * @Date 2018/09/03/17:20
 * @Email Jliuer@aliyun.com
 * @Description
 */
public class EmojiDialog extends DialogFragment {

    public static final String Tag = "EmojiDialog";

    private EmojiKeyboard mEmojiKeyboard;

    private int maxLine = 3;
    private int maxColumns = 8;
    private EditText editText;

    public EmojiDialog() {
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dialog = inflater.inflate(R.layout.emoji_dialog, container);
        mEmojiKeyboard = dialog.findViewById(R.id.emojiview);

        //去掉dialog的标题，需要在setContentView()之前
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = this.getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //去掉dialog默认的padding
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //设置dialog的位置在底部
            lp.gravity = Gravity.BOTTOM;
        }
        init();
        if (mOnViewValidListener != null) {
            mOnViewValidListener.onViewValid(dialog);
        }
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnViewValidListener != null) {
            mOnViewValidListener.onDismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.0f;
            window.setAttributes(windowParams);
        }
    }

    public EmojiDialog init() {
        if (editText == null) {
            throw new IllegalArgumentException("error");
        }
        mEmojiKeyboard.setEditText(editText);
        mEmojiKeyboard.setMaxLines(maxLine);
        mEmojiKeyboard.setMaxColumns(maxColumns);
        mEmojiKeyboard.setEmojiLists(EomjiSource.emojiListMap("emoji.json", getContext()));
        mEmojiKeyboard.setIndicatorPadding(3);
        mEmojiKeyboard.init();
        return this;
    }

    public EmojiDialog setMaxLines(int maxLine) {
        this.maxLine = maxLine;
        return this;
    }

    public EmojiDialog setMaxColumns(int maxColumns) {
        this.maxColumns = maxColumns;
        return this;
    }

    public EmojiDialog setEditText(EditText editText) {
        this.editText = editText;
        return this;
    }

    public interface OnViewValidListener {
        void onViewValid(View v);

        void onDismiss();
    }

    OnViewValidListener mOnViewValidListener;

    public void setOnViewValidListener(OnViewValidListener onViewValidListener) {
        mOnViewValidListener = onViewValidListener;
    }
}
