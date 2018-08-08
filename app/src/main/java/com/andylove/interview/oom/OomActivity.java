package com.andylove.interview.oom;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

public class OomActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取堆内存阈值
       ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        Log.e("large memory", manager.getLargeMemoryClass() + "");
    }
}
