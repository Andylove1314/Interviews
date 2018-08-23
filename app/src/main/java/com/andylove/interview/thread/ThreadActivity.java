package com.andylove.interview.thread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.andylove.interview.R;

import java.lang.ref.WeakReference;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadActivity extends Activity {

    private AsyncTask task = null;

    private MyHandlerThread hand = null;
    private THandler th = null;
    private static UiHandler uih = null;
    private Looper looper;

    private ThreadPoolExecutor mThreadPoolExec;
    private static final int MAX_POOL_SIZE = 4;
    private static final int KEEP_ALIVE = 1;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        final TextView content = findViewById(R.id.thread_content);

//        task = new MyTask(content);

//        hand = new MyHandlerThread("handlethread");
//        hand.start();
//        looper = hand.getLooper();
//        th = new THandler(looper);

        mThreadPoolExec = new ThreadPoolExecutor(
                1,
                MAX_POOL_SIZE,
                KEEP_ALIVE,
                TimeUnit.SECONDS,
                workQueue);
        findViewById(R.id.thread_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UI线程执行
//                task.execute();
//                th.sendEmptyMessage(0);

                if (uih == null) {
                    uih = new UiHandler(content);
                }
                handleMsg(new Runnable() {
                    @Override
                    public void run() {
                        int MAX = 100;
                        int cou = 0;
                        for (int i = 0; i < MAX; i++) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            cou = i;
                            Message uimsg = Message.obtain();
                            uimsg.obj = cou;
                            uih.sendMessage(uimsg);
                        }
                    }
                });

            }
        });

        findViewById(R.id.thread_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //UI线程执行
//                boolean cancelSuccess = task.cancel(false);
//                Log.e("cancel", cancelSuccess + "");
            }
        });

        findViewById(R.id.thread_safe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ThreadActivity.this, ThreadSafeActivity.class));
            }
        });

    }

    /**
     * AsyncTask
     */
    public static class MyTask extends AsyncTask {
        private WeakReference<TextView> con;

        public MyTask(TextView content) {
            con = new WeakReference<>(content);
        }

        //工作线程方法体
        @Override
        protected Object doInBackground(Object[] objects) {

            int count = 0;
            int MAX = 100;

            for (int i = 0; i < MAX; i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                count = i;
                publishProgress(count);
            }

            return count;
        }

        //UI线程方法体
        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.e("ui refresh", o.toString());
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            Log.e("ui progress", values[0].toString());
            if (con.get() != null) {
                con.get().setText(values[0].toString());
            }
        }

        @Override
        protected void onCancelled(Object o) {
            super.onCancelled(o);
            Log.e("canceled", "task canceled");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.e("canceled", "task canceled");
        }
    }

    /**
     * HandlerThread
     */
    private static class MyHandlerThread extends HandlerThread {
        public MyHandlerThread(String name) {
            super(name);
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
        }

    }

    private static class THandler extends Handler {

        public THandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            int MAX = 100;
            int cou = 0;
            for (int i = 0; i < MAX; i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cou = i;
                Message uimsg = Message.obtain();
                uimsg.obj = cou;
                uih.sendMessage(uimsg);
            }

        }
    }

    private static class UiHandler extends Handler {
        private WeakReference<TextView> con;

        public UiHandler(TextView content) {
            con = new WeakReference<>(content);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.e("ui progress", msg.obj.toString());
            if (con.get() != null) {
                con.get().setText(msg.obj.toString());
            }
        }
    }

    /**
     * ThreadPoolExecutor
     */
    private synchronized void handleMsg(Runnable command) {
        mThreadPoolExec.execute(command);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //前台线程，一般用于更新视图，所以在销毁Activity时，要取消线程，节省系统资源，防止内存泄露
//        task.cancel(true);
        if (uih != null) {
            uih.removeCallbacks(null);
        }
    }
}
