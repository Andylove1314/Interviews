**android内存溢出**
一，内存溢出（native+jvm）：
 内存溢出，通俗理解就是内存不够，即app所占内存达到系统为其分配的最大内存峰值，导致app出现异常的一个过程，因为系统会为每个app进程动态分配内存，运行过程中由于内存回收不及时或者无法回收，很容易就达到内存峰值，导致app停止运行。
 
二，溢出类型（jvm ，new或者malloc申请的内存是虚拟内存，申请之后不会立即映射到物理内存，即不会占用RAM，只有调用memset使用内存后，虚拟内存才会真正映射到RAM。）：
      
    1、java.lang.OutOfMemoryError: PermGen space：
    JVM管理两种类型的内存,堆和非堆。堆是给开发人员用的上面说的就是,是在JVM启动时创建;非堆是留给JVM自己用的,用来存放类的信息的。它 和堆不同,运行期内GC不会释放空间。如果web app用了大量的第三方jar或者应用有太多的class文件而恰好MaxPermSize设置较小,超出了也会导致这块内存的占用过多造成溢出,或者 tomcat热部署时侯不会清理前面加载的环境,只会将context更改为新部署的,非堆存的内容就会越来越多。
    PermGen space的全称是Permanent Generation space,是指内存的永久保存区域,这块内存主要是被JVM存放Class和Meta信息的,Class在被Loader时就会被放到PermGen space中,它和存放类实例(Instance)的Heap区域不同,GC(Garbage Collection)不会在主程序运行期对PermGen space进行清理,所以如果你的应用中有很CLASS的话,就很可能出现PermGen space错误,这种错误常见在web服务器对JSP进行pre compile的时候。如果你的WEB APP下都用了大量的第三方jar, 其大小超过了jvm默认的大小(4M)那么就会产生此错误信息了。
    
    2、java.lang.OutOfMemoryError: Java heap space
    第一种情况是个补充,主要存在问题就是出现在 这个情况中。其默认空间(即-Xms)是物理内存的1/64,最大空间(-Xmx)是物理内存的1/4。如果内存剩余不到40%,JVM就会增大堆到 Xmx设置的值,内存剩余超过70%,JVM就会减小堆到Xms设置的值。所以服务器的Xmx和Xms设置一般应该设置相同避免每次GC后都要调整虚拟机 堆的大小。假设物理内存无限大,那么JVM内存的最大值跟操作系统有关,一般32位机是1.5g到3g之间,而64位的就不会有限制了。
    注意:如果Xms超过了Xmx值,或者堆最大值和非堆最大值的总和超过了物理内存或者操作系统的最大限制都会引起服务器启动不起来。
    垃圾回收GC（Garbage Collection）的角色：
    JVM调用GC的频度还是很高的,主要两种情况下进行垃圾回收:
    当应用程序线程空闲;另一个是java内存堆不足时,会不断调用GC,若连续回收都解决不了内存堆不足的问题时,就会报out of memory错误。因为这个异常根据系统运行环境决定,所以无法预期它何时出现。
    根据GC的机制,程序的运行会引起系统运行环境的变化,增加GC的触发机会。
    为了避免这些问题,程序的设计和编写就应避免垃圾对象的内存占用和GC的开销。显示调用System.GC()只能建议JVM需要在内存中对垃圾对象进行回收,但不是必须马上回收,
    一个是并不能解决内存资源耗空的局面,另外也会增加GC的消耗。
    一句话概况：由于Android系统对dalvik的vm heap size作了硬性限制，当java进程申请的java空间超过阈值时，就会抛出OOM异常（这个阈值可以是48M、24M、16M等，视机型而定）
     注意：程序发生OMM并不表示RAM不足，而是因为程序申请的java heap对象超过了dalvik vm heap growthlimit。也就是说，在RAM充足的情况下，也可能发生OOM。
           这样的设计似乎有些不合理，但是Google为什么这样做呢？这样设计的目的是为了让Android系统能同时让比较多的进程常驻内存，这样程序启动时就不用每次都重新加载到内存，能够给用户更快的响应。迫使每个应用程序使用较小的内存，移动设备非常有限的RAM就能使比较多的app常驻其中。
           如果RAM真的不足，会发生什么呢？这时Android的memory killer会起作用，当RAM所剩不多时，memory killer会杀死一些优先级比较低的进程来释放物理内存，让高优先级程序得到更多的内存。
           * 应用程序如何绕过dalvikvm heapsize的限制？
            对于一些大型的应用程序（比如游戏），内存使用会比较多，很容易超超出vm heapsize的限制，这时怎么保证程序不会因为OOM而崩溃呢？
           
           （1）创建子进程
              创建一个新的进程，那么我们就可以把一些对象分配到新进程的heap上了，从而达到一个应用程序使用更多的内存的目的，当然，创建子进程会增加系统开销，而且并不是所有应用程序都适合这样做，视需求而定。创建子进程的方法：使用android:process标签
           
           （2）使用jni在native heap上申请空间（推荐使用，jni开发，new或者malloc申请的内存是虚拟内存，申请之后不会立即映射到物理内存，即不会占用RAM，只有调用memset使用内存后，虚拟内存才会真正映射到RAM。）
              nativeheap的增长并不受dalvik vm heapsize的限制，从图6可以看出这一点，它的native heap size已经远远超过了dalvik heap size的限制。
             只要RAM有剩余空间，程序员可以一直在native heap上申请空间(在32位操作系统中，进程的地址空间为0到4GB)，当然如果 RAM快耗尽，memory killer会杀进优先级比较低的进程来释放物理内存，让高优先级程序得到更多的内存。大家使用一些软件时，有时候会闪退，就可能是软件在native层申请了比较多的内存导致的。比如，我就碰到过UC web在浏览内容比较多的网页时闪退，原因就是其native heap增长到比较大的值，占用了大量的RAM，被memory killer杀掉了。
             也就是说dvm heap挑战的是虚拟机进程的内存底线（挑战失败结果是爆出的OOM，app崩溃），而native heap挑战的是ram的底线（挑战失败的结果就是linux memory killer干死你这个linux进程，他是不会因为你的挑战而关机的哦 哈哈哈）。
           
           （3）使用显存（操作系统预留RAM的一部分作为显存）
             使用OpenGL textures等API，texture memory不受dalvik vm heapsize限制，这个我没有实践过。再比如Android中的GraphicBufferAllocator申请的内存就是显存。
三，JVM内存区域组成：

    简单的说java中的堆和栈
    java把内存分两种:一种是栈内存,另一种是堆内存
    1。在函数中定义的基本类型变量和对象的引用变量都在函数的栈内存中分配（包括java和c/c++，一般分配的比较少几MB吧）;
    2。堆内存用来存放由new创建的对象和数组（运行时分配，不可预测，知道oom）
    在函数(代码块)中定义一个变量时,java就在栈中为这个变量分配内存空间,当超过变量的作用域后,java会自动释放掉为该变量所分配的内存空间;在堆中分配的内存由java虚拟机的自动垃圾回收器来管理
    堆的优势是可以动态分配内存大小,生存期也不必事先告诉编译器,因为它是在运行时动态分配内存的。缺点就是要在运行时动态分配内存,存取速度较慢;
    栈的优势是存取速度比堆要快,缺点是存在栈中的数据大小与生存期必须是确定的无灵活性。
    java堆分为三个区:New、Old和Permanent
    GC有两个线程:
    新创建的对象被分配到New区,当该区被填满时会被GC辅助线程移到Old区,当Old区也填满了会触发GC主线程遍历堆内存里的所有对象。Old区的大小等于Xmx减去-Xmn
    java栈存放
    栈调整:参数有+UseDefaultStackSize -Xss256K,表示每个线程可申请256k的栈空间
    每个线程都有他自己的Stack

四，内存溢出种种因素及需要注意的地方：
   
   原理上面已经说了，那么从codding层面上讲，一般开发中遇到的都是java.lang.OutOfMemoryError: Failed to allocate a 4308492 byte allocation with 467872 free bytes and 456KB until OOM 
   这样的，也就是说jvm的heap size 达到阈值了。
   
    1，内存泄漏（详情见泄漏讲解）
    内存泄漏，导致堆内存无法被gc，浪费了系统分配给app的堆内存（堆内存分配内存大小是有上限的），导致内存溢出。
    2，使用较大的bitmap，即图片太大导致内存溢出。比如app load了很多很大的高清图片，而且没有及时recycle(),导致内存溢出。
    还有就是假如只是取图片的一些信息，请不要把bitmap加载到内存当中：
    BitmapFactory.Options options = new BitmapFactory.Options();  
     // 不把图片加载到内存中 
    options.inJustDecodeBounds = true;
    Bitmap btimapObject = BitmapFactory.decodeFile(myImage.getAbsolutePath(), options);
      
    对bitmap进行压缩，并且及时recycle()
    3，强引用：强引用是使用最普遍的引用（默认都是强引用）。如果一个对象具有强引用，那垃圾回收器绝不会回收它。 当内存空间不足，Java虚拟机宁愿抛出OutOfMemoryError错误，使程序异常终止，也不会靠随意回收具有强引用的对象来解决内存不足的问题。
              一般我们在声明对象变量时，使用完后就不管了，认为垃圾回收器会帮助我们回收这些对象所指向的内存空间，实际上如果这个对象的内存空间还处在被引用状态的话（这就是内存泄漏啊），垃圾回收器是永远不会回收它的内存空间的，只有当这个内存空间不被任何对象引用的时候，垃圾回收器才会去回收。
      所以建议在对象不用的时候要 object = null，这样 jvm就会在合适时机（不用的时候gc它）
      或者用其他引用（软引用，弱引用，虚引用）
      
    4，就是比较流氓的想法：溢出还不是因为你给我分配的heap size太小吗？
       所以如果你要耍流氓的话，那就没办法了，哈哈
       （1）优化Dalvik虚拟机的堆内存分配
       VMRuntime类提供的setTargetHeapUtilization方法可以增强程序堆内存的处理效率。
       private final static float TARGET_HEAP_UTILIZATION = 0.75f;  
       VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION); 
       
       （2）自定义堆内存大小
       强制定义Android给当前App分配的内存大小，使用VMRuntime设置应用最小堆内存。
       // 设置最小heap内存为 20MB 大小  
       private final static int HEAP_SIZE = 20 * 1024 * 1024 ;  
       VMRuntime.getRuntime().setMinimumHeapSize(HEAP_SIZE); 
       largeHeap
       让Dalvik虚拟机为App分配更大的内存，该方法能为我们的App争取到更多内存空间，从而缓解内存不足的压力
       可以在程序中使用ActivityManager.getMemoryClass()方法来获取App内存正常使用情况下的大小，通过ActivityManager.getLargeMemoryClass()可获得开启largeHeap时最大的内存大小
       使用方法：
       该方法使用非常简单，只要在AndroidManifest.xml文件中的<application>节点属性中加上”android:largeHeap="true"
       <application  
           android:icon="@mipmap/ic_launcher" 
           android:label="@string/app_name"  
           android:theme="@style/AppTheme"  
           android:largeHeap="true"  
           
 
 五，android内存分析工具：
    
    1，AS Memory Profiler（studio 自带分析工具，还包括网络，cpu的分析）
    
 六，内存溢出总结：
    
    在编码时，资源一定要及时置空，还有就是不用的对象，其尽早脱离被引用，还有多媒体编码时，一定要熟悉其创建及回收代码，
    做到及时处理不用的资源。
 