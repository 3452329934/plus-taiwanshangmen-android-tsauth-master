package com.hyphenate.easeui.widget.chatrow;

import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.easeui.EaseUI;

import java.io.IOException;

/**
 * Created by zhangsong on 17-10-20.
 */

public class EaseChatRowVoicePlayer {
    private static final String TAG = "ConcurrentMediaPlayer";

    private static EaseChatRowVoicePlayer instance = null;

    private AudioManager audioManager;
    private MediaPlayer mediaPlayer;
    private String playingId;

    private MediaPlayer.OnCompletionListener onCompletionListener;

    public static EaseChatRowVoicePlayer getInstance(Context context) {
        if (instance == null) {
            synchronized (EaseChatRowVoicePlayer.class) {
                if (instance == null) {
                    instance = new EaseChatRowVoicePlayer(context);
                }
            }
        }
        return instance;
    }

    public MediaPlayer getPlayer() {
        return mediaPlayer;
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /**
     * May null, please consider.
     *
     * @return
     */
    public String getCurrentPlayingId() {
        return playingId;
    }

    public void play(final EMMessage msg, final MediaPlayer.OnCompletionListener listener) {
        if (!(msg.getBody() instanceof EMVoiceMessageBody)) {
            return;
        }

        if (mediaPlayer.isPlaying()) {
            stop();
        }

        playingId = msg.getMsgId();
        onCompletionListener = listener;

        try {
            setSpeaker();
            requestAudioFocus(true);
            EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) msg.getBody();
            mediaPlayer.setDataSource(voiceBody.getLocalUrl());
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stop();

                    playingId = null;
                    onCompletionListener = null;
                }
            });
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        requestAudioFocus(false);

        /**
         * This listener is to stop the voice play animation currently, considered the following 3 conditions:
         *
         * 1.A new voice item is clicked to play, to stop the previous playing voice item animation.
         * 2.The voice is play complete, to stop it's voice play animation.
         * 3.Press the voice record button will stop the voice play and must stop voice play animation.
         *
         */
        if (onCompletionListener != null) {
            onCompletionListener.onCompletion(mediaPlayer);
        }
    }

    private EaseChatRowVoicePlayer(Context cxt) {
        Context baseContext = cxt.getApplicationContext();
        audioManager = (AudioManager) baseContext.getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
        onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        if (isPlaying()) {
                            stop();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        if (isPlaying()) {
                            stop();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        break;
                    default:
                }
            }
        };

    }

    private void setSpeaker() {
        boolean speakerOn = EaseUI.getInstance().getSettingsProvider().isSpeakerOpened();
        if (speakerOn) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } else {
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }
    }

    public AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener;

    /**
     * 是否获取音频焦点AudioFocus
     * @param requestFocus true 获得，false 释放
     */
    private void requestAudioFocus(boolean requestFocus) {
        if (audioManager == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest audioFocusRequest =  new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT).build();
            if (!requestFocus) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest);
            } else {
                audioManager.requestAudioFocus(audioFocusRequest);
            }
        } else {
            if (!requestFocus){
                audioManager.abandonAudioFocus(mAudioFocusChangeListener);
            }else {
                audioManager.requestAudioFocus(mAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            }
        }
    }

    AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {//监听器
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    //播放操作
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    //暂停操作
                    break;
                default:
                    break;
            }
        }
    };

}
