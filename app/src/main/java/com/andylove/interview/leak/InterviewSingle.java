package com.andylove.interview.leak;

import android.content.Context;

class InterviewSingle {

    /**
     * 静态对象，生命周期很长，以防con泄漏
     */
    private static InterviewSingle ourInstance = null;

    static InterviewSingle getInstance(Context con) {
        if (ourInstance == null) {
            ourInstance = new InterviewSingle(con);
        }
        return ourInstance;
    }

    private InterviewSingle(Context con) {
    }
}
