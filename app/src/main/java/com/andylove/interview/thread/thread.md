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
 