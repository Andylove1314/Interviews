// MessageAidlInterface.aidl
package com.andylove.interview.ipc;
import com.andylove.interview.ipc.ITaskCallBack;
// Declare any non-default types here with import statements
//import android.os.Handler;
interface MusicAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void playMusic(String musicPath);
    void pauseMusic();
    void stopMusic();

    void registerCallBack(ITaskCallBack cb);
    void unRegisterCallBack(ITaskCallBack cb);

}
