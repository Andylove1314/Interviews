package com.andylove.interview.ipc;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.io.IOException;

/**
 * 音乐播放服务进程
 *
 * @author andylove
 */
public class MusicService extends Service {

    private boolean isPause = false;
    private MediaPlayer player = null;

    private RemoteCallbackList<ITaskCallBack> callbackList = new RemoteCallbackList<>();

    private MusicAidlInterface.Stub mBinder = new MusicAidlInterface.Stub() {

        @Override
        public void playMusic(final String musicPath) throws RemoteException {

            //回调告诉client
            callBack("开始播放");
            //异步播放音乐
            if (player == null) {//创建播放器
                player = new MediaPlayer();
            } else {
                if (!isPause) {
                    //切歌
                    player.reset();
                    isPause = false;
                }
            }
            try {
                if (!isPause) {//从头开始播放
                    AssetFileDescriptor fileDescriptor = getAssets().openFd(musicPath);
                    player.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                            fileDescriptor.getLength());
                    player.prepare();
                }
                player.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void pauseMusic() throws RemoteException {
            //回调告诉client
            callBack("暂停播放");
            if (player != null) {
                player.pause();
                isPause = true;
            }
        }

        @Override
        public void stopMusic() throws RemoteException {//回调告诉client
            callBack("停止播放");
            playRelease();
            isPause = false;

        }

        @Override
        public void registerCallBack(ITaskCallBack cb) throws RemoteException {

            if (cb != null) {
                callbackList.register(cb);
            }

        }

        @Override
        public void unRegisterCallBack(ITaskCallBack cb) throws RemoteException {
            if (cb != null) {
                callbackList.unregister(cb);
            }
        }
    };

    /**
     * 回调通知
     */
    private void callBack(String msg) {
        callbackList.beginBroadcast();
        try {
            callbackList.getBroadcastItem(0).actionPerformed(1000, msg);
        } catch (RemoteException e) {

        }
        callbackList.finishBroadcast();
    }

    /**
     * 释放播放器
     */
    private void playRelease() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("music_service", "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("music_service", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("music_service", "onBind");
        if (mBinder == null) {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        return mBinder;
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        Log.e("music_service", "unbindService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("music_service", "onDestroy");
    }
}
