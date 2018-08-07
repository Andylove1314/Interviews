#android IPC
ipc,是什么呢？我想大家都知道，那就是大家所熟悉的跨进程通信，android中有几种跨进程通信的场景呢？

**1, Intent通信**
  
   Intent就是个异步的意图中介，用于组件之间通信，包括同进程的通信，这个大家都知道，比如：
             
    Intent intent = new Intent(this, MainActivity.class);
    startActivity(intent); 
   
   还有就是异步的跨进程通信，比如，app启动拨号功能，如下：
    
    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:15011564***"));
    startActivity(intent);
    
   当然通过intent启动第三方app的方式有很多，如，通过scheme来启动
    
**2, 发送广播**

   广播机制也是典型的异步跨进程通信的好做法，通过发送指定action的Intent广播，来唤起其他app，前提是被唤醒app有注册广播接收器BroadCastReceiver，广播注册分为两种，一种是常驻广播，即
   在清单文件注册的广播，另一种是动态注册广播，在代码中动态注册，但它的宿主销毁时，要反注册广播，否则导致内存泄露，android有很多系统广播，如电量状态，信号状态，时钟变化，息屏开屏等广播，在开发中
   你可以拦截这些广播，做一些你想做的事情，比如，当屏幕亮时，启动你的app。
 
    
**3, ContentProvider**

   ContentProvider，其实大家应该开发中都有遇到，比如，app获取通讯录联系人，还有获取相册图片，及本地存储的文件等等，都与ContentProvider拖不了干系：
   
            // 获取手机联系人
             ContentResolver resolver = getContentResolver();
             Cursor phoneCursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
             if (phoneCursor != null) {
                 boolean oneTime = phoneCursor.moveToNext();
                 while (oneTime) {//演示，只获取一次
                     //得到联系人名称
                     String contactName = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                     Toast.makeText(IPCActivity.this, "通讯录app提供数据--" + contactName, Toast.LENGTH_LONG).show();
                     oneTime = false;
                 }
                 phoneCursor.close();//记住关闭游标，否则会内存泄露哦！！！
             }
   这是典型的跨进程通信案例，其实这是系统《联系人》app的ContentProvider功能，就是提供第三方app获取联系人数据的一个功能，大家知道联系人是存在联系人db里面的，但是出于安全性，ContentProvider便由然而生了，屏蔽了内部实现逻辑，只是提供了uri跟相应字段共第三方调用。
   如何创建自己的ContentProvider，提供第三方app访问自己的数据，请查看官方demo，这里就不给大家介绍了。
   
   **3, AIDL定义的c/s通信**
   AIDL,即android接口定义语言，是用于为远程服务（c-s IPC）提供接口，接口方法的参数要求：
        
        1，java基本数据类型 int short long double float boolean char
        2，String List、Map 和 CharSequence ( List、Map的元素必须是AIDL支持的类型，如1，3中所述，List支持泛型，但是Map不支持，并且接口实现处必须是 ArrayList 和 HashMap)
        3, 其他 AIDL 生成的 java IInterface
        4，实现了 Parcelable protocol 的自定义类，即序列化后的自定义类
   
   我们知道每一个Android应用都是一个独立的Android进程，它们拥有自己独立的虚拟地址空间，应用进程处于用户空间之中，彼此之间相互独立，不能共享。但是内核空间却是可以共享的，Client 进程向Server进程通信，就是利用进程间可以共享的内核地址空间来完成底层的通信的工作的。Client进程与Server端进程往往采用ioctl等方法跟内核空间的驱动进行交互。
   流程见项目中的结构图。
    在分析Binder原理之前，我们先来思考一个问题，Linux系统本身有许多IPC手段，为什么Android要重新设计一套Binder机制呢？
    为什么选用Binder，在讨论这个问题之前，我们知道Android也是基于Linux内核，Linux现有的进程通信手段有以下几种：
    
    1，管道：在创建时分配一个page大小的内存，缓存区大小比较有限；
    2，消息队列：信息复制两次，额外的CPU消耗；不合适频繁或信息量大的通信；
    3，共享内存：无须复制，共享缓冲区直接付附加到进程虚拟地址空间，速度快；但进程间的同步问题操作系统无法实现，必须各进程利用同步工具解决；
    4，套接字：作为更通用的接口，传输效率低，主要用于不通机器或跨网络的通信；
    5，信号量：常作为一种锁机制，防止某进程正在访问共享资源时，其他进程也访问该资源。因此，主要作为进程间以及同一进程内不同线程之间的同步手段。6. 信号: 不适用于信息交换，更适用于进程中断控制，比如非法内存访问，杀死某个进程等；
    
    既然有现有的IPC方式，为什么重新设计一套Binder机制呢。主要是出于以下三个方面的考量：  
    1，高性能：从数据拷贝次数来看Binder只需要进行一次内存拷贝，而管道、消息队列、Socket都需要两次，共享内存不需要拷贝，Binder的性能仅次于共享内存。
    2，稳定性：上面说到共享内存的性能优于Binder，那为什么不适用共享内存呢，因为共享内存需要处理并发同步问题，控制负责，容易出现死锁和资源竞争，稳定性较差。而Binder基于C/S架构，客户端与服务端彼此独立，稳定性较好。
    3，安全性：我们知道Android为每个应用分配了UID，用来作为鉴别进程的重要标志，Android内部也依赖这个UID进行权限管理，包括6.0以前的固定权限和6.0以后的动态权限，传统IPC只能由用户在数据包里填入UID/PID，这个标记完全 是在用户空间控制的，没有放在内核空间，因此有被恶意篡改的可能，因此Binder的安全性更高。
    
  aidl跨进程通信，就讲到这里，具体逻辑看demo代码。
  接下来脑补一下进程与线程；
      
      1，什么是进程process呢？进程就好比一台工业机器，它使机器内的原料、机器部件运作起来，输入跟产出便是机器的价值，也就是进程的价值；还比如一个活体，比如人，从出生到老死，也是一个进程的完整过程，除非中途over。
      每个app进程，都是zygote进程fork出来的，zygote进程则是linux的init进程生成的，而开机后laucher app 即桌面则是zygote fork的第一个进程，详情看源码。
      2，什么是线程Thread呢？线程就是进程当中的一个执行线索，一个小的工作流程，比如人的各个器官，眼睛负责视野，鼻子负责嗅觉等等，他们都相当于一个小线程，各干各的，互不影响。
      3，进程与线程的关系是什么呢？进程是线程存活的港湾，没有了进程，就不可能有线程，还拿人说事吧，人都挂了，你眼睛还能看到东西吗，鼻子还能嗅到味道吗，显然不可能了。
         那android中的进程线程是怎么个关系呢，当桌面app logo被点击后，则zygote就会fork出app进程，当然这个进程就是虚拟机进程，与其他app的进程是相互独立的不可以互相访问资源的，但是他们的linux进程是可以通信的，资源是可以互相访问的，如上所述。
         但是为了app的安全性，高性能，app之间的进程通信，依赖用Binder驱动通信，实现方式就如上所说：AIDL生成IInterface，来实现虚拟机层的沟通。
         那么线程是怎么启动的呢？线程分为两种一个是主线程，在app中即使UI线程，他的启动在ActivityThread.class里面，可以查看源码，及所有跟UI有关的操作，必须是在ActivityThread.main()里面执行，而且不能超过规定时间，否则会被阻塞线程，
         所以耗时的操作都必须被放到子线程里面，android中的子线程主要有Thread(),AsyncTask(对线程池的封装)，线程不会依附于任何组件，除了进程，因为进程是它的载体。
         举个例子：比如音乐播放器，播放音乐时，你退出app了，但是歌曲还在播放，为什么，因为播放歌曲是在子线程中进行的，即使你的Activity，service被销毁，他任然运行，虽然你的组件被销毁了，但是你的进程还在，所以线程就会在，
         那怎么销毁这个线程呢？，两种方式：1，手动销毁线程 2，杀死app进程。
   线程知识后续更新。。。
      
      
    