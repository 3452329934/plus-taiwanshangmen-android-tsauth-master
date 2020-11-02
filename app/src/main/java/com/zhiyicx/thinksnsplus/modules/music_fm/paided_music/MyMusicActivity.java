package com.zhiyicx.thinksnsplus.modules.music_fm.paided_music;

import android.content.ComponentName;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.zhiyicx.baseproject.base.TSActivity;
import com.zhiyicx.thinksnsplus.modules.music_fm.music_album_detail.MusicDetailFragment;
import com.zhiyicx.thinksnsplus.modules.music_fm.music_play.MusicPlayService;

public class MyMusicActivity extends TSActivity implements MusicDetailFragment.MediaBrowserProvider{

    private MediaBrowser mMediaBrowser;

    private final MediaBrowser.ConnectionCallback mConnectionCallback =
            new MediaBrowser.ConnectionCallback() {
                @Override
                public void onConnected() {
                    try {
                        connectToSession(mMediaBrowser.getSessionToken());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };

    public final MediaController.Callback mMediaControllerCallback =
            new MediaController.Callback() {
                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackState state) {

                }

                @Override
                public void onMetadataChanged(MediaMetadata metadata) {

                }
            };

    @Override
    protected void onStart() {
        super.onStart();
        mMediaBrowser.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (getMediaController() != null) {
            getMediaController().unregisterCallback(mMediaControllerCallback);
        }
        mMediaBrowser.disconnect();
    }

    @Override
    protected Fragment getFragment() {
        mMediaBrowser = new MediaBrowser(this, new ComponentName(this,
                MusicPlayService.class), mConnectionCallback, null);
        return MyMusicFragmentContainer.getInstance();
    }

    private void connectToSession(MediaSession.Token token) throws RemoteException {
        MediaController mediaController = new MediaController(this, token);
        setMediaController(mediaController);
        mediaController.registerCallback(mMediaControllerCallback);
    }

    @Override
    protected void componentInject() {

    }

    @Override
    public MediaBrowser getMediaBrowser() {
        return mMediaBrowser;
    }
}
