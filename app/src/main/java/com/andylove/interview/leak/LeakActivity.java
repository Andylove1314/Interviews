package com.andylove.interview.leak;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.andylove.interview.R;

public class LeakActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
