// ITaskCallBack.aidl
package com.andylove.interview.ipc;

// Declare any non-default types here with import statements

interface ITaskCallBack {
    void actionPerformed(int actionId, String content);
}
