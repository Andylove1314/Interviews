package com.andylove.interview.thread;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.andylove.interview.R;

public class ThreadSafeActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threadsafe);

        //代码锁
        findViewById(R.id.thread_syn_code).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaleTicket saleThread1 = new SaleTicket("sale1");
                SaleTicket saleThread2 = new SaleTicket("sale2");
                SaleTicket saleThread3 = new SaleTicket("sale3");
                saleThread1.start();
                saleThread2.start();
                saleThread3.start();
            }
        });

        //方法锁
        findViewById(R.id.thread_syn_method).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BankThread bankThread1 = new BankThread("bank1");
                BankThread bankThread2 = new BankThread("bank2");
                BankThread bankThread3 = new BankThread("bank3");
                bankThread1.start();
                bankThread2.start();
                bankThread3.start();
            }
        });

    }

    /**
     * 售票
     */
    private static class SaleTicket extends Thread {
        //票数  非静态的成员变量,非静态的成员变量数据是在每个对象中都会维护一份数据的。
        static int num = 50;

        public SaleTicket(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                //同步代码块
                synchronized ("代码块锁") {
                    if (num > 0) {
                        System.out.println(Thread.currentThread().getName() + "售出了第" + num + "号票");
                        //sleep导致锁对象被不会释放
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        num--;
                    } else {
                        System.out.println(Thread.currentThread().getName() + "售罄了..");
                        break;
                    }
                }
            }
        }
    }

    /**
     * 取钱工作
     */
    private static class BankThread extends Thread {
        static int count = 5000;
        final int DRAW = 1000;

        public BankThread(String name) {
            super(name);
            System.out.println(toString());
        }

        @Override  //方法锁
        public synchronized void run() {
            while (true) {
                synchronized ("代码块锁") {
                    if (count > 0) {
                        count -= DRAW;
                        System.out.println(Thread.currentThread().getName() + "取走了" + DRAW + "块,还剩余" + count + "元");
                    } else {
                        System.out.println(Thread.currentThread().getName() + "取光了...");
                        break;
                    }
                }
            }
        }
    }

}