package com.zycx.shortvideo.interfaces;

public interface TrimVideoListener {

    void onStartTrim();

    void onFinishTrim(String url);

    void onCancel();
}
