package com.andylove.interview.pattern;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.andylove.interview.R;

/**
 * 设计模式
 */
public class PatternActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern);
    }
}
