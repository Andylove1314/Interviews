package com.andylove.interview.ipc;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.andylove.interview.MainActivity;
import com.andylove.interview.R;

/**
 * 跨进程通信
 */
public class IPCActivity extends Activity {

    private Button ipcIntentOne, ipcIntentThird, ipcContentProvider, ipcAidlBinder, ipcAidlBinderStop, ipcAidlBinderPause;

    /**
     * 服务连接器
     */
    private ServiceConnection connection = null;

    private IBinder mBinder = null;
    private MusicAidlInterface musicInterface = null;

    private ITaskCallBack callBack = new ITaskCallBack.Stub() {
        @Override
        public void actionPerformed(int actionId, String con) throws RemoteException {
            Toast.makeText(IPCActivity.this, actionId + "--" + con, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipc);

        //intent同进程通信
        ipcIntentOne = findViewById(R.id.ipc_intent_one);
        ipcIntentOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IPCActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //intent跨进程通信
        ipcIntentThird = findViewById(R.id.ipc_intent_third);
        ipcIntentThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:15011564***"));
                startActivity(intent);
            }
        });

        //contentprovider跨进程通信
        ipcContentProvider = findViewById(R.id.ipc_contentprovider);
        ipcContentProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContactsName();
            }
        });

        //binder跨进程通信--开启音乐服务
        ipcAidlBinder = findViewById(R.id.ipc_binder);
        ipcAidlBinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    initMusicService();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });

        //binder跨进程通信--暂停音乐服务
        ipcAidlBinderPause = findViewById(R.id.ipc_binder_pause);
        ipcAidlBinderPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    pauseMusic();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });

        //binder跨进程通信--停止音乐服务
        ipcAidlBinderStop = findViewById(R.id.ipc_binder_stop);
        ipcAidlBinderStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    stopMusic();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });

    }

    /**
     * 初始化播放服务
     *
     * @throws Throwable
     */
    private void initMusicService() throws Throwable {

        //为了实现c/s通信
        if (connection == null) {
            //以防解绑服务后，播放服务销毁
            Intent music0 = new Intent(this, MusicService.class);
            startService(music0);
            connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mBinder = service;
                    try {
                        playMusic();
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                }
            };
            Intent music = new Intent(this, MusicService.class);
            bindService(music, connection, Context.BIND_AUTO_CREATE);
        } else {
            playMusic();
        }
    }

    private void playMusic() throws Throwable {
        if (musicInterface == null) {
            musicInterface = MusicAidlInterface.Stub.asInterface(mBinder);
            //注册service回调
            musicInterface.registerCallBack(callBack);
        }
        musicInterface.playMusic("广东爱情故事.mp3");
    }

    private void pauseMusic() throws Throwable {
        if (musicInterface != null) {
            musicInterface.pauseMusic();
        }
    }

    private void stopMusic() throws Throwable {
        if (musicInterface != null) {
            musicInterface.stopMusic();
        }
    }

    /**
     * 获取联系人姓名
     */
    private void getContactsName() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                        0);
                return;
            }

        }

        // 获取手机联系人
        ContentResolver resolver = getContentResolver();
        Cursor phoneCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (phoneCursor != null) {
            //演示，只获取一次
            boolean oneTime = phoneCursor.moveToNext();
            while (oneTime) {
                //得到联系人名称
                String contactName = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Toast.makeText(IPCActivity.this, "通讯录app提供数据--" + contactName, Toast.LENGTH_LONG).show();
                oneTime = false;
            }
            //记住关闭游标，否则会内存泄露哦！！！
            phoneCursor.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //必须断开连接 否则内存泄露
        if (connection != null) {
            unbindService(connection);
            connection = null;
        }
    }
}
