package com.andylove.interview.leak;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;

import com.andylove.interview.R;

import java.lang.ref.WeakReference;

public class LeakActivity extends Activity {

    /**
     * 匿名内部类容易泄露
     */
//    private Handler  mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//
//        }
//    };
    /**
     * 静态内部类匿不容易泄露
     */
    private Handler mHandler = new MyHandler(LeakActivity.this);

    private Thread thread = null;
    private MyTask task = null;
    private ServiceConnection connection = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak);
        //错误至极
        InterviewSingle.getInstance(this);
        //正确做法避免泄露
        InterviewSingle.getInstance(getApplicationContext());

        ////线程内存泄漏
        new Thread(new Runnable() {
            @Override
            public void run() {
                //执行时间比较长，所以极有可能还没完事，activity就被关了
                SystemClock.sleep(10000);
            }
        }).start();
        //线程池内存泄漏
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //执行时间比较长，所以极有可能还没完事，activity就被关了
                SystemClock.sleep(10000);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //对组件的操作
            }

        }.execute();
        //线程正确做法
        thread = new MyThread();
        thread.start();
        //线程池正确做法
        task = new MyTask();
        task.execute();

        //绑定服务
        Intent bind = new Intent(this, MyService.class);
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(bind, connection, Service.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //再次加强保险
        mHandler.removeCallbacksAndMessages(null);
        //线程再次加强保险
        thread.stop();
        task.cancel(true);
        //必须解绑
        unbindService(connection);
        //广播，输入流等一些资源，在不用时 一定要关闭，否则gc无法回收。
    }

    /**
     * 静态内部类，不容易泄露
     */
    private static class MyHandler extends Handler {

        //若引用，更保险
        private WeakReference<Activity> mAct = null;

        public MyHandler(Activity act) {
            mAct = new WeakReference<>(act);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    /**
     * 静态内部类线程，不容易泄露
     */
    private static class MyThread extends Thread {

        public MyThread() {
        }

        @Override
        public void run() {
            super.run();
            SystemClock.sleep(10000);
        }
    }

    /**
     * 静态内部类线程，不容易泄露
     */
    private static class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SystemClock.sleep(10000);
            return null;
        }
    }



}
