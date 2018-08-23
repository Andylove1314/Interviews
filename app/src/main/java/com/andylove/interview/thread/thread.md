**android线程**

   一，进程和线程
   
   当某个应用组件启动且该应用没有运行其他任何组件时，Android 系统会使用单个执行线程为应用启动新的 Linux 进程。
   默认情况下，同一应用的所有组件在相同的进程和线程（称为“主”线程）中运行。 如果某个应用组件启动且该应用已存在进程（因为存在该应用的其他组件），则该组件会在此进程内启动并使用相同的执行线程。 
   但是，您可以安排应用中的其他组件在单独的进程中运行，并为任何进程创建额外的线程。
   
   二，进程
   
   默认情况下，同一应用的所有组件均在相同的进程中运行，且大多数应用都不会改变这一点。 但是，如果您发现需要控制某个组件所属的进程，则可在清单文件中执行此操作。
   各类组件元素的清单文件条目—<activity>、<service>、<receiver> 和 <provider>—均支持 android:process 属性，此属性可以指定该组件应在哪个进程运行。您可以设置此属性，使每个组件均在各自的进程中运行，或者使一些组件共享一个进程，而其他组件则不共享。 此外，您还可以设置 android:process，使不同应用的组件在相同的进程中运行，但前提是这些应用共享相同的 Linux 用户 ID 并使用相同的证书进行签署。
   此外，<application> 元素还支持 android:process 属性，以设置适用于所有组件的默认值。
   如果内存不足，而其他为用户提供更紧急服务的进程又需要内存时，Android 可能会决定在某一时刻关闭某一进程。在被终止进程中运行的应用组件也会随之销毁。 当这些组件需要再次运行时，系统将为它们重启进程。
   决定终止哪个进程时，Android 系统将权衡它们对用户的相对重要程度。例如，相对于托管可见 Activity 的进程而言，它更有可能关闭托管屏幕上不再可见的 Activity 的进程。 因此，是否终止某个进程的决定取决于该进程中所运行组件的状态。 下面，我们介绍决定终止进程所用的规则。
   
   三，进程生命周期
   
   Android 系统将尽量长时间地保持应用进程，但为了新建进程或运行更重要的进程，最终需要移除旧进程来回收内存。 为了确定保留或终止哪些进程，系统会根据进程中正在运行的组件以及这些组件的状态，将每个进程放入“重要性层次结构”中。 必要时，系统会首先消除重要性最低的进程，然后是重要性略逊的进程，依此类推，以回收系统资源。
   重要性层次结构一共有 5 级。以下列表按照重要程度列出了各类进程（第一个进程最重要，将是最后一个被终止的进程）：
   
   1，前台进程
   用户当前操作所必需的进程。如果一个进程满足以下任一条件，即视为前台进程：
   托管用户正在交互的 Activity（已调用 Activity 的 onResume() 方法）
   托管某个 Service，后者绑定到用户正在交互的 Activity
   托管正在“前台”运行的 Service（服务已调用 startForeground()）
   托管正执行一个生命周期回调的 Service（onCreate()、onStart() 或 onDestroy()）
   托管正执行其 onReceive() 方法的 BroadcastReceiver
   通常，在任意给定时间前台进程都为数不多。只有在内存不足以支持它们同时继续运行这一万不得已的情况下，系统才会终止它们。 此时，设备往往已达到内存分页状态，因此需要终止一些前台进程来确保用户界面正常响应。
   
   2，可见进程
   没有任何前台组件、但仍会影响用户在屏幕上所见内容的进程。 如果一个进程满足以下任一条件，即视为可见进程：
   托管不在前台、但仍对用户可见的 Activity（已调用其 onPause() 方法）。例如，如果前台 Activity 启动了一个对话框，允许在其后显示上一 Activity，则有可能会发生这种情况。
   托管绑定到可见（或前台）Activity 的 Service。
   可见进程被视为是极其重要的进程，除非为了维持所有前台进程同时运行而必须终止，否则系统不会终止这些进程。
   
   3，服务进程
   正在运行已使用 startService() 方法启动的服务且不属于上述两个更高类别进程的进程。尽管服务进程与用户所见内容没有直接关联，但是它们通常在执行一些用户关心的操作（例如，在后台播放音乐或从网络下载数据）。因此，除非内存不足以维持所有前台进程和可见进程同时运行，否则系统会让服务进程保持运行状态。
   
   4，后台进程
   包含目前对用户不可见的 Activity 的进程（已调用 Activity 的 onStop() 方法）。这些进程对用户体验没有直接影响，系统可能随时终止它们，以回收内存供前台进程、可见进程或服务进程使用。 通常会有很多后台进程在运行，因此它们会保存在 LRU （最近最少使用）列表中，以确保包含用户最近查看的 Activity 的进程最后一个被终止。如果某个 Activity 正确实现了生命周期方法，并保存了其当前状态，则终止其进程不会对用户体验产生明显影响，因为当用户导航回该 Activity 时，Activity 会恢复其所有可见状态。 有关保存和恢复状态的信息，请参阅 Activity文档。
   
   5，空进程
   不含任何活动应用组件的进程。保留这种进程的的唯一目的是用作缓存，以缩短下次在其中运行组件所需的启动时间。 为使总体系统资源在进程缓存和底层内核缓存之间保持平衡，系统往往会终止这些进程。
   
   根据进程中当前活动组件的重要程度，Android 会将进程评定为它可能达到的最高级别。例如，如果某进程托管着服务和可见 Activity，则会将此进程评定为可见进程，而不是服务进程。
   
   此外，一个进程的级别可能会因其他进程对它的依赖而有所提高，即服务于另一进程的进程其级别永远不会低于其所服务的进程。 例如，如果进程 A 中的内容提供程序为进程 B 中的客户端提供服务，或者如果进程 A 中的服务绑定到进程 B 中的组件，则进程 A 始终被视为至少与进程 B 同样重要。
   
   由于运行服务的进程其级别高于托管后台 Activity 的进程，因此启动长时间运行操作的 Activity 最好为该操作启动服务，而不是简单地创建工作线程，当操作有可能比 Activity 更加持久时尤要如此。例如，正在将图片上传到网站的 Activity 应该启动服务来执行上传，这样一来，即使用户退出 Activity，仍可在后台继续执行上传操作。使用服务可以保证，无论 Activity 发生什么情况，该操作至少具备“服务进程”优先级。 同理，广播接收器也应使用服务，而不是简单地将耗时冗长的操作放入线程中。
   
   
   四，主线程
   
   应用启动时，系统会为应用创建一个名为“主线程”的执行线程。 此线程非常重要，因为它负责将事件分派给相应的用户界面小部件，其中包括绘图事件。 此外，它也是应用与 Android UI 工具包组件（来自 android.widget 和 android.view 软件包的组件）进行交互的线程。因此，主线程有时也称为 UI 线程。
   系统不会为每个组件实例创建单独的线程。运行于同一进程的所有组件均在 UI 线程中实例化，并且对每个组件的系统调用均由该线程进行分派。 因此，响应系统回调的方法（例如，报告用户操作的 onKeyDown() 或生命周期回调方法）始终在进程的 UI 线程中运行。
   例如，当用户触摸屏幕上的按钮时，应用的 UI 线程会将触摸事件分派给小部件，而小部件反过来又设置其按下状态，并将失效请求发布到事件队列中。 UI 线程从队列中取消该请求并通知小部件应该重绘自身。
   在应用执行繁重的任务以响应用户交互时，除非正确实现应用，否则这种单线程模式可能会导致性能低下。 具体地讲，如果 UI 线程需要处理所有任务，则执行耗时很长的操作（例如，网络访问或数据库查询）将会阻塞整个 UI。 一旦线程被阻塞，将无法分派任何事件，包括绘图事件。 从用户的角度来看，应用显示为挂起。 更糟糕的是，如果 UI 线程被阻塞超过几秒钟时间（目前大约是 5 秒钟），用户就会看到一个让人厌烦的“应用无响应”(ANR) 对话框。如果引起用户不满，他们可能就会决定退出并卸载此应用。
   此外，Android UI 工具包并非线程安全工具包。因此，您不得通过工作线程操纵 UI，而只能通过 UI 线程操纵用户界面。 
   因此，Android 的单线程模式必须遵守两条规则：
   1，不要阻塞 UI 线程
   2，不要在 UI 线程之外访问 Android UI 工具包
   
   主线程入口方法：
   
    ActivityThread.class:
    public static void main(String[] args) {
           Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "ActivityThreadMain");
           SamplingProfilerIntegration.start();
  
           // CloseGuard defaults to true and can be quite spammy.  We
           // disable it here, but selectively enable it later (via
           // StrictMode) on debug builds, but using DropBox, not logs.
           CloseGuard.setEnabled(false);
   
           Environment.initForCurrentUser();
   
           // Set the reporter for event logging in libcore
           EventLogger.setReporter(new EventLoggingReporter());
   
           // Make sure TrustedCertificateStore looks in the right place for CA certificates
           final File configDir = Environment.getUserConfigDirectory(UserHandle.myUserId());
           TrustedCertificateStore.setDefaultUserDirectory(configDir);
   
           Process.setArgV0("<pre-initialized>");
   
           
           Looper.prepareMainLooper();
   
           ActivityThread thread = new ActivityThread();
           thread.attach(false);
   
           if (sMainThreadHandler == null) {
               sMainThreadHandler = thread.getHandler();
           }
   
           if (false) {
               Looper.myLooper().setMessageLogging(new
                       LogPrinter(Log.DEBUG, "ActivityThread"));
           }
   
           // End of event ActivityThreadMain.
           Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
            //looper引擎启动，为后续的工作做准备
           Looper.loop();
   
           throw new RuntimeException("Main thread loop unexpectedly exited");
       }
       
      Looper.class:
      public static void loop() {
              final Looper me = myLooper();
              if (me == null) {
                  throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
              }
              final MessageQueue queue = me.mQueue;
      
              // Make sure the identity of this thread is that of the local process,
              // and keep track of what that identity token actually is.
              Binder.clearCallingIdentity();
              final long ident = Binder.clearCallingIdentity();
      
               //无限死循环，随时接受用户及系统的旨意。
              for (;;) {
                  Message msg = queue.next(); // might block
                  if (msg == null) {
                      // No message indicates that the message queue is quitting.
                      return;
                  }
      
                  // This must be in a local variable, in case a UI event sets the logger
                  final Printer logging = me.mLogging;
                  if (logging != null) {
                      logging.println(">>>>> Dispatching to " + msg.target + " " +
                              msg.callback + ": " + msg.what);
                  }
      
                  final long traceTag = me.mTraceTag;
                  if (traceTag != 0 && Trace.isTagEnabled(traceTag)) {
                      Trace.traceBegin(traceTag, msg.target.getTraceName(msg));
                  }
                  try {
                      msg.target.dispatchMessage(msg);
                  } finally {
                      if (traceTag != 0) {
                          Trace.traceEnd(traceTag);
                      }
                  }
      
                  if (logging != null) {
                      logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
                  }
      
                  // Make sure that during the course of dispatching the
                  // identity of the thread wasn't corrupted.
                  final long newIdent = Binder.clearCallingIdentity();
                  if (ident != newIdent) {
                      Log.wtf(TAG, "Thread identity changed from 0x"
                              + Long.toHexString(ident) + " to 0x"
                              + Long.toHexString(newIdent) + " while dispatching to "
                              + msg.target.getClass().getName() + " "
                              + msg.callback + " what=" + msg.what);
                  }
      
                  msg.recycleUnchecked();
              }
          } 
          
 五，工作线程
   工作线程，即大家熟知的子线程， android框架提供的工作线程都有哪些？
  
    1，AsyncTask
     
     要使用它，必须创建 AsyncTask 的子类并实现 doInBackground() 回调方法，该方法将在后台线程池中运行。 要更新 UI，应该实现 onPostExecute() 以传递 doInBackground() 返回的结果并在 UI 线程中运行，以便您安全地更新 UI。
     稍后，您可以通过从 UI 线程调用 execute() 来运行任务。
    下面简要概述了 AsyncTask 的工作方法，但要全面了解如何使用此类，您应阅读 AsyncTask 参考文档：
    可以使用泛型指定参数类型、进度值和任务最终值
    方法 doInBackground() 会在工作线程上自动执行
    onPreExecute()、onPostExecute() 和 onProgressUpdate() 均在 UI 线程中调用
    doInBackground() 返回的值将发送到 onPostExecute()
    您可以随时在 doInBackground() 中调用publishProgress()，以在 UI 线程中执行 onProgressUpdate()
    您可以随时取消任何线程中的任务。
 
    本AsyncTask类是需要快速从主线程移动工作到工作线程应用程序的简单，实用的原始。例如，输入事件可能会触发使用加载的位图更新UI的需要。一个AsyncTask 对象可以卸载位图加载和解码到一个备用线程; 一旦该处理完成，该AsyncTask对象就可以管理在主线程上接收工作以更新UI。
    
    使用时AsyncTask，请记住一些重要的性能方面。首先，默认情况下，应用程序将AsyncTask 其创建的所有对象推送到单个线程中。因此，它们以串行方式执行，并且 - 与主线程一样 - 特别长的工作包可以阻塞队列。因此，我们建议您仅用于AsyncTask处理持续时间小于5毫秒的工作项。
    
    AsyncTask对象也是隐式引用问题最常见的违规者。 AsyncTask对象也存在与显式引用相关的风险，但这些风险有时更容易解决。例如，AsyncTask 可能需要引用UI对象，以便AsyncTask在主线程上执行其回调后正确更新UI对象。在这种情况下，您可以使用 WeakReference 来存储对所需UI对象的引用，并AsyncTask在主线程上运行后访问该对象 。要清楚，保持WeakReference 对象不会使对象成为线程安全的; 在 WeakReference仅提供处理与明确提到和垃圾收集问题的方法。
 
    2，HandlerThread
     
     虽然AsyncTask 它很有用， 但它可能并不总是解决您的线程问题的正确方法。相反，您可能需要一种更传统的方法来在较长时间运行的线程上执行工作块，以及一些手动管理该工作流的功能。
     
     考虑从Camera对象获取预览帧的常见挑战 。当您注册Camera预览帧时，您将在onPreviewFrame() 回调中接收它们，该 回调在调用它的事件线程上调用。如果在UI线程上调用此回调，则处理大像素阵列的任务将干扰渲染和事件处理工作。同样的问题适用于AsyncTask，它也连续执行作业并且易受阻塞。
     
     这种情况下处理程序线程是合适的：处理程序线程实际上是一个长时间运行的线程，它从队列中抓取工作并对其进行操作。在此示例中，当您的应用程序将Camera.open()命令委托 给处理程序线程上的工作块时，关联的 onPreviewFrame() 回调将位于处理程序线程上，而不是UI或AsyncTask 线程上。因此，如果您要对像素进行长时间的工作，这对您来说可能是更好的解决方案。
     
     当您的应用程序使用创建线程时HandlerThread，不要忘记 根据其正在进行的工作类型设置线程的 优先级。请记住，CPU只能并行处理少量线程。设置优先级有助于系统知道在所有其他线程争夺注意力时安排此工作的正确方法。
     
     3，ThreadPoolExecutor
     
      某些类型的工作可以简化为高度并行的分布式任务。例如，一个这样的任务是为800万像素图像的每个8×8块计算滤波器。由于大量的工作包，这会创建，而不是适当的类。单线程性质会将所有线程化工作转变为线性系统。另一方面，使用该类需要程序员手动管理一组线程之间的负载平衡。 AsyncTaskHandlerThreadAsyncTaskHandlerThread
      
      ThreadPoolExecutor是一个帮助类，使这个过程更容易。此类管理一组线程的创建，设置其优先级，并管理这些线程之间的工作分配方式。随着工作负载的增加或减少，类会旋转或销毁更多线程以适应工作负载。
     
      此类还可以帮助您的应用程序生成最佳线程数。在构造ThreadPoolExecutor 对象时，应用程序会设置最小和最大线程数。作为ThreadPoolExecutor增加的工作量 ，类将考虑初始化的最小和最大线程计数，并考虑待处理的待处理工作量。基于这些因素，ThreadPoolExecutor决定在任何给定时间应该有多少线程存活。
      
      一个ExecutorService执行使用可能的几个池线程之一，通常用配置的每个提交的任务Executors工厂方法。
      
      线程池解决了两个不同的问题：它们通常在执行大量异步任务时提供改进的性能，这是由于减少了每个任务的调用开销，并且它们提供了一种绑定和管理资源的方法，包括执行集合时所消耗的线程。任务。每个ThreadPoolExecutor还维护一些基本统计数据，例如已完成任务的数量。
      
      为了在各种上下文中有用，该类提供了许多可调参数和可扩展性钩子。
      但是，程序员被要求使用更方便的 Executors工厂方法Executors.newCachedThreadPool()（无界线程池，具有自动线程回收），Executors.newFixedThreadPool(int) （固定大小线程池）和Executors.newSingleThreadExecutor()（单个后台线程），为最常见的使用场景预配置设置。
      
      线程池构造：
      ThreadPoolExecutor(int corePoolSize,
                         int maximumPoolSize,
                         long keepAliveTime,
                         TimeUnit unit,
                         BlockingQueue<Runnable> workQueue) 
                         
      ThreadPoolExecutor(int corePoolSize,
                         int maximumPoolSize,
                         long keepAliveTime,
                         TimeUnit unit,
                         BlockingQueue<Runnable> workQueue,
                         ThreadFactory threadFactory)
                         
      ThreadPoolExecutor(int corePoolSize,
                         int maximumPoolSize,
                         long keepAliveTime,
                         TimeUnit unit,
                         BlockingQueue<Runnable> workQueue,
                         RejectedExecutionHandler handler)
                         
      ThreadPoolExecutor(int corePoolSize,
                         int maximumPoolSize,
                         long keepAliveTime,
                         TimeUnit unit,
                         BlockingQueue<Runnable> workQueue,
                         ThreadFactory threadFactory,
                         RejectedExecutionHandler handler)        
      构造方法参数说明：
      corePoolSize
      核心线程数，默认情况下核心线程会一直存活，即使处于闲置状态也不会受存keepAliveTime限制。除非将allowCoreThreadTimeOut设置为true。
      
      maximumPoolSize
      线程池所能容纳的最大线程数。超过这个数的线程将被阻塞。当任务队列为没有设置大小的LinkedBlockingDeque时，这个值无效。
      
      keepAliveTime
      非核心线程的闲置超时时间，超过这个时间就会被回收。
      
      unit
      指定keepAliveTime的单位，如TimeUnit.SECONDS。当将allowCoreThreadTimeOut设置为true时对corePoolSize生效。
      
      workQueue
      线程池中的任务队列.
      常用的有三种队列，SynchronousQueue,LinkedBlockingDeque,ArrayBlockingQueue。
      
      threadFactory
      线程工厂，提供创建新线程的功能。ThreadFactory是一个接口，只有一个方法
      public interface ThreadFactory {
        Thread newThread(Runnable r);
      }           
      
 六，线程安全
  
  如果你的代码所在的进程中有多个线程在同时运行，而这些线程可能会同时运行这段代码。
  如果每次运行结果和单线程运行的结果是一样的，而且其他的变量的值也和预期的是一样的，就是线程安全的。
  或者说:一个类或者程序所提供的接口对于线程来说是原子操作或者多个线程之间的切换不会导致该接口的执行结果存在二义性,也就是说我们不用考虑同步的问题 。
  
  1，主线程（UI线程）安全问题
  
    当一个程序第一次启动的时候，Android会启动一个LINUX进程和一个主线程。默认的情况下，所有该程序的组件都将在该进程和线程中运行 。主线程（Main Thread）主要负责处理与UI相关的事件，如：用户的按键事件，用户接触屏幕的事件以及屏幕绘图事 件，并把相关的事件分发到对应的组件进行处理。所以主线程通常又被叫做UI线程。
    系统不会为每个组件单独创建线程，在同一个进程里的UI组件都会在UI线程里实例化，系统对每一个组件的调用都从UI线程分发出去。结果就是，响应系统回调的方法（比如响应用户动作的onKeyDown()和各种生命周期回调）永远都是在UI线程里运行。
    UI线程才能与Android UI工具包中的组件进行交互，在开发Android应用时必须遵守单线成原则：
    
    （1）Android UI操作并不是线程安全的并且这些操作必须在UI线程中执行。
      android UI 中提供invalidate（）来更新界面，而invalidate（）方法是线程不安全。
      Android提供了Invalidate方法实现界面刷新，但是Invalidate不能直接在非UI主线程中调用，因为他是违背了单线程模型：Android UI操作并不是线程安全的，并且这些操作必须在UI线程中调用。例如：在非UI线程中调用invalidate会导致线程不安全，也就是说可能在非UI线程中刷新界面的时候，UI线程(或者其他非UI线程)也在刷新界面，这样就导致多个界面刷新的操作不能同步，导致线程不安全
      
    （2）不要阻塞UI线程。
      为了用户体验，在app要及时响应用户及系统的触发，所以在UI线程里，切记不要做太多的操作，否则影响到用户体验，系统直接告诉你ANR,然后让你关掉你的垃圾。
   
  2，工作线程安全问题
    
    （1）不安全的根本原因：
    1.存在两个或者两个以上的线程对象共享同一个资源。
    2.多线程操作共享资源代码有多个语句。
    
    （2）线程安全问题的解决方案
        方式一：同步代码块
        格式：synchronize（锁对象）{
                      //需要被同步的代码
                   }
        同步代码块需要注意的事项：（例子代码看demo）
            1.锁对象可以是任意的一个对象；
            2.一个线程在同步代码块中sleep了，并不会释放锁对象；
            3.如果不存在线程安全问题，千万不要使用同步代码块；
            4.锁对象必须是多线程共享的一个资源，否则锁不住。
            
        方式二：同步方法（同步函数就是使用synchronized修饰一个函数）
        同步函数注意事项：
            1.如果函数是一个非静态的同步函数，那么锁对象是this对象；
            2.如果函数是静态的同步函数，那么锁对象是当前函数所属的类的字节码文件（class对象）；
            3.同步函数的锁对象是固定的，不能由自己指定。
    
        推荐使用：同步代码块
        原因：
          1.同步代码块的锁对象可以由我们自由指定，方便控制；
          2.同步代码块可以方便的控制需要被同步代码的范围，同步函数必须同步函数的所有代码。