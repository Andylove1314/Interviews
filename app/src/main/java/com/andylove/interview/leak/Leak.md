**内存泄露**

一，内存泄露概念定义，或者说实质原因：
   
   `内存泄露就是指程序（android Dalvik）中己动态分配的(堆内存)由于某种原因程序未释放或无法释放，造成系统内存的浪费，导致程序运行速度减慢甚至系统崩溃等严重后果。
   _在android app开发中，泄露的原因归根结底就是一句话：生命周期短的对象被生命周期长的所引用，当短的被销毁时，长的无法释放对短的引用，所以导致无法回收没用的内存，从而导致内存泄露，
   比如Activity对象呗静态对象所引用，activity销毁时，静态对象无法释怀的感觉，哈哈哈。`
   
二，那么有哪些不当的编码会导致内存泄露呢，该如何解决？

   `本来对于内存的管理，对于c来说，是需要手动管理内存的，但是对于java来说，对于Dalvik，是不需要人肉去释放不需要的内存的，GC帮助我们做到了。
   ,但是由于开发者写法原因，阻碍了GC，不能释放没用的内存，内存泄露，累计过多，会直接导致程序oom，崩溃。不合理的写法都有哪些，为何导致泄露，如何正确编码呢？`

     （1）handler 导致内存泄露：
       比如定义一个handler，如下：
       private Handler mHandler = new Handler() {
               @Override
               public void handleMessage(Message msg) {
                   //...
               }
           };

       handler是一个非静态匿名内部类，它会持有Activity的引用，我们知道handler是运行在一个Looper线程中的，而Looper线程是轮询来处理消息队列中的消息的，假设我们处理的消息有十条，而当他执行到第6条的时候，用户点击了back返回键，销毁了当前的Activity，这个时候消息还没有处理完，handler还在持有Activity的引用，
       这个时候就会导致无法被GC回收，造成了内存泄漏。
       如何写可以避免内存泄露呢，如下：
      
       private static class MyHandler extends Handler {
               private WeakReference<Context> reference;
              //在构造函数中使用弱引用来引用context对象
               public MyHandler(Context context) {
                   reference = new WeakReference<>(context);
               }
               @Override
               public void handleMessage(Message msg) {
                   
               }
           }
           
            @Override
               protected void onDestroy() {
                   super.onDestroy();
                   //移除handler message和callback
                   mHandler.removeCallbacksAndMessages(null)；
               }
              
           创建一个静态内部类继承自handler，然后再在构造参数中对handler持有的对象做弱引用，这样在回收时就会回收了handler持有的对象，静态类不持有外部类的对象，所以你的Activity可以随意被回收，由于Handler不再持有外部类对象的引用，导致程序不允许你在Handler中操作Activity中的对象了。这里还做了一处修改，就是当我
           们的回收了handler持有的对向，即销毁了该Activity时，这时如果handler中的还有未处理的消息，我们就需要在OnDestry方法中移除消息队列中的消息。

     （2）非静态内部类的静态对象导致内存泄露：
       首先非静态内部类隐式地保存了一个指向外部类对象的引用，当你初始化一个静态的对象时，大家知道，静态对象的生命周期比较长（生命周期跟app生命周期一样长），只有在app进程结束后（即退出app）才会被回收，
       所以比如在Activity内部创建一个非静态内部类，并且初始化了其静态对象，则当activity销毁时，这个对象对activity也是无法释怀啊，activity很无奈，只能导致泄露了。（其实上面上的handler也属于这个类型的泄露！）
       那如何解决呢？那就把非静态内部类改成静态内部类就好了（静态内部类不会默认持有外部类的引用）。
     
     （3）单例模式造成的内存泄露，单例大家知道，整个app维护的一个静态的对象，生命周期贯穿整个app进程，当单例持有了对某个周期较短的对象时，一但这个对象在销毁的时候，便导致了泄露，比如对activity的引用导致的泄露。
     所以假如用到单例模式，而且必须要引用Context，则请选择引用Application对象（他们生命周期一样长）。
     
     （4）线程造成的内存泄漏
     大家都知道，service，activity等四大组件所有的代码执行都是在主线程里面的，ActivityThread或者ApplicationThread里面，在组件里面创建执行的子线程是跟组件完全独立的（thread的宿主只是process），组件与子线程的通信手段则是handler。
     所以当activity，service销毁时，他们里面启动的子线程是不会受影响的，thread会直到自己执行完毕（或者人肉去主动中断关闭这个子线程），它所操作的数据，资源才会被回收。
     所以线程使用不恰当造成的内存泄漏也是很常见的。比如下面的：
     //——————————test1 匿名内部类———————————//
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
     //——————————test2 匿名内部类———————————//
             new Thread(new Runnable() {
                 @Override
                 public void run() {
                     //执行时间比较长，所以极有可能还没完事，activity就被关了
                     SystemClock.sleep(10000);
                 }
             }).start();
       首先这种写法导致内存泄露的因素有2：
      
       1，非静态匿名内部类，隐式地保存了一个指向外部类对象的引用，即对activity的引用，当activity被销毁时，线程的执行还没有完毕，所以activity无法被回收，自然activity对象所关联的对象也无法释放（比如view），假如当子线程执行完后，在关闭activity，则不会出现泄漏，
       可能有人会质疑，等子线程执行完，activity所泄漏的内存不就可以被回收了么，no！因为Dalvik回收是有时机的，不好意思，你已经错过了最好时机，这块内存将白白浪费了。
       解决办法：就是把匿名内部类改为静态内部类，当然为了保险起见，最好是把对activity的引用变为weak引用。
       
       2，线程没有执行完（或者没有在合适的时机对其中断），dvm是不会对其进行回收的(当然在它所引用的对象如activity销毁之前它执行完毕，是没问题的)，不要天真的以为activity都关了，线程自然也会被关闭，希望从今以后大家不要再执迷不悟。线程的回收，只要你错过了时机，将永远别想回收了（呵呵，除非你杀掉你的进程）。
       线程的内存泄露将再也不能够被回收，线程是JAVA垃圾回收机制的根源，由于在运行系统中DVM虚拟机一直持有着所有运行状态的线程的引用，结果导致处于运行状态的线程将永远不会被回收。
       所以在codding的时候一定要根据他的时机去手动中断关闭你的子线程，比如在Activity销毁时去关掉你的线程，释放资源，否则一但activity泄露，thread就别想被回收了，从而内存就这么浪费了，被占着茅坑不拉屎了。
       比如这样，则是聪明的选择：
        @Override
         protected void onDestroy() {
           super.onDestroy();
           mThread.close();
         }
       
    （5）不用的资源未关闭导致内存泄漏
    对于使用了bindServioce绑定了服务，但是当activity销毁时没有unBindServioce，BraodcastReceiver，ContentObserver，File，Cursor，Stream，Bitmap等资源的代码，应该在引用对象销毁之前及时关闭或者注销，否则这些资源将不会被回收，造成内存泄漏。
    
 三，内存泄露总结：
   上面五种泄漏，则是android开发中经常遇到的，也是有时候注意不到的，总之，之所以泄漏，就是
   JVM进程对没用的内存无法回收，导致占着茅坑不拉屎，浪费了内存资源，所以没用的对象在不用时，一定要在合适的时机处理销毁掉，虽然jvm的gc
   是自动回收的，但是可能由于你的操作不当，导致jvm无能为力。
   解决办法就一句话：杀掉某个对象时，一定要先杀掉引用它的对象，否则这个对象将无法被真实回收让出所占内存，浪费了内存空间。
    