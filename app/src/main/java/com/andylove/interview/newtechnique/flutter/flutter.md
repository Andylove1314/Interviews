**跨平台之flutter**
1，Flutter是一款移动应用程序SDK，一份代码可以同时生成iOS和Android两个高性能、高保真的应用程序。
   Flutter目标是使开发人员能够交付在不同平台上都感觉自然流畅的高性能应用程序。我们兼容滚动行为、排版、图标等方面的差异。
   
   ### 为什么要使用Flutter?
    
    1，提高开发效率
    同一份代码开发iOS和Android
    用更少的代码做更多的事情
    轻松迭代
    在应用程序运行时更改代码并重新加载（通过热重载）
    修复崩溃并继续从应用程序停止的地方进行调试
    2，创建美观，高度定制的用户体验
    受益于使用Flutter框架提供的丰富的Material Design和Cupertino（iOS风格）的widget
    实现定制、美观、品牌驱动的设计，而不受原生控件的限制   
    
   ###  核心原则
   
    1，Flutter包括一个现代的响应式框架、一个2D渲染引擎、现成的widget和开发工具。这些组件可以帮助您快速地设计、构建、测试和调试应用程序。
    一切皆为widget，Widget是Flutter应用程序用户界面的基本构建块。每个Widget都是用户界面一部分的不可变声明。 与其他将视图、控制器、布局和其他属性分离的框架不同，Flutter具有一致的统一对象模型：widget。
    
    2，Widget可以被定义为:
    一个结构元素（如按钮或菜单）
    一个文本样式元素（如字体或颜色方案）
    布局的一个方面（如填充）
    等等…
    
    3，Widget本身通常由许多更小的、单一用途widget组成，这些widget结合起来产生强大的效果。例如，Container是一个常用的widget， 由多个widget组成，这些widget负责布局、绘制、定位和调整大小。具体来说，Container由 LimitedBox、 ConstrainedBox、 Align、 Padding、 DecoratedBox、 和Transform组成。 您可以用各种方式组合这些以及其他简单的widget，而不是继承容器。
    
    4，
    
  ###  StatefulWidget state生命周期
  
    （1）创建一个State对象时，会调用StatefulWidget.createState；
    
    （2）和一个BuildContext相关联，可以认为被加载了（mounted）；
    
    （3）调用initState；
    
    （4）调用didChangeDependencies；
    
    （5）经过上述步骤，State对象被完全的初始化了，调用build；
    
    （6）如果有需要，会调用didUpdateWidget；
    
    （7）如果处在开发模式，热加载会调用reassemble；
    
    （8）如果它的子树（subtree）包含需要被移除的State对象，会调用deactivate；
    
    （9）调用dispose,State对象以后都不会被构建；
    
    （10）当调用了dispose,State对象处于未加载（unmounted），已经被dispose的State对象没有办法被重新加载（remount）。
   
   ### Flutter渲染流程简析（https://juejin.im/post/5b7767fef265da43803bdc65）
   
   ##### 渲染框架
   Flutter的框架分为Framework和Engine两层，应用是基于Framework层开发的，Framework负责渲染中的Build，Layout，Paint，生成Layer等环节。Engine层是C++实现的渲染引擎，负责把Framework生成的Layer组合，生成纹理，然后通过Open GL接口向GPU提交渲染数据。
   
   #### 渲染过程
   当需要更新UI的时候，Framework通知Engine，Engine会等到下个Vsync信号到达的时候，会通知Framework，然后Framework会进行animations,
     build，layout，compositing，paint，最后生成layer提交给Engine。Engine会把layer进行组合，生成纹理，最后通过OpenGl接口提交数据给GPU，
     GPU经过处理后在显示器上面显示。见图。
     从流程图可以看出来，只有当有UI更新的才需要重新渲染，当然程序启动的是默认去渲染的。
     
   #### 渲染触发
   当有UI需要更新的时候，是怎么样触发渲染，从应用到Framework，再到Engine这个过程是怎么样的。在Flutter开发应用的时候，当需要更新的UI的时候，需要调用一下setState方法，然后就可以实现了UI的更新。
   
   #### 渲染过程
   当应用调用setState后，经过Framework一连串的调用后，最终调用scheduleFrame来通知Engine需要更新UI，Engine就会在下个vSync到达的时候通过调用_drawFrame来通知Framework，然后Framework就会通过BuildOwner进行Build和PipelineOwner进行Layout，Paint，最后把生成Layer，组合成Scene提交给Engine。
   从Engine回调，Framework会build，Layout，Paint，生成Layer等环节。
     
   1，Build
   在Flutter应用开发中,无状态的widget是通过StatelessWidget的build方法构建UI，有状态的widget是通过State的build方法构建UI。
   setState -> buildScope
   buildScope会遍历_dirtyElements，对每个在数组里面的每个element调用rebuild，最终就是调用到相应的widget的build方法。
   其实当setState的时候会把相应的element添加到_dirtyElements数组里，并且element标识dirty状态。
   
   2，layout
   在Flutter中应用中，是使用支持layout的widget来实现布局的，支持layout的wiget有Container，Padding，Align等等，强大又简易。在渲染流程中，在widget build后会进入layout环节。layout入口是flushLayout。
   layout的整个过程，首先是当RenderOjbect需要重新layout的时候，把自己添加到渲染管道里面，然后再触发渲染到了layout环节，先从渲染管道里面遍历找出需要渲染的RenderObject，然后调用performLayout进行计算layout，而且不同的对象实现不同的performLayout方法，计算layout的方式也不一样，然后再调用child
   的layout入口，同时把parent的限制也传给child，child调用自己的performLayout。
   
   3，paint
   当需要描绘自定义的图像的时候，可以通过继承CustomPainter，实现paint方法，然后在paint方法里面使用Flutter提供接口可以实现复杂的图像。 
   总结来说，paint过程中，渲染管道中首先找出需要重绘的RenderObject,然后如果有实现了CustomPainter，就是调用CustomPainter paint方法，再去调用child的paint方法。
   
   4，Composited Layer
   Composited Layer就是把所有layer组合成Scene，然后通过ui.window.render方法，把scene提交给Engine，到这一步，Framework向Engine提交数据基本完成了。Engine会把所有的layer根据大小，层级，透明度计算出最终的显示效果，通过OpenGl接口渲染到屏幕上。
  
   ### Flutter线程管理（https://yq.aliyun.com/articles/604054）
   Flutter Engine自己不创建管理线程。Flutter Engine线程的创建和管理是由embedder负责的。
   注意:Embeder是指将引擎移植的平台的中间层代码。
   Flutter Engine要求Embeder提供四个Task Runner。尽管Flutter Engine不在乎Runner具体跑在哪个线程，但是它需要线程配置在整一个生命周期里面保持稳定。也就是说一个Runner最好始终保持在同一线程运行。这四个主要的Task Runner包括：
   
   1，Platform Task Runner
     
      Flutter Engine的主Task Runner，运行Platform Task Runner的线程可以理解为是主线程。类似于Android Main Thread或者iOS的Main Thread。但是我们要注意Platform Task Runner和iOS之类的主线程还是有区别的。
      对于Flutter Engine来说Platform Runner所在的线程跟其它线程并没有实质上的区别，只不过我们人为赋予它特定的含义便于理解区分。实际上我们可以同时启动多个Engine实例，每个Engine对应一个Platform Runner，每个Runner跑在各自的线程里。这也是Fuchsia（Google正在开发的操作引擎）里Content Handler的工作原理。一般来说，一个Flutter应用启动的时候会创建一个Engine实例，Engine创建的时候会创建一个线程供Platform Runner使用。
      
      需要注意的是，阻塞Platform Thread不会直接导致Flutter应用的卡顿（跟iOS android主线程不同）。尽管如此，平台对Platform Thread还是有强制执行限制。所以建议复杂计算逻辑操作不要放在Platform Thread而是放在其它线程（不包括我们现在讨论的这个四个线程）。其他线程处理完毕后将结果转发回Platform Thread。长时间卡住Platform Thread应用有可能会被系统Watchdot强行杀死。
      
   2，UI Task Runner
   
      UI Task Runner被Flutter Engine用于执行Dart root isolate代码（isolate我们后面会讲到，姑且先简单理解为Dart VM里面的线程）。Root isolate比较特殊，它绑定了不少Flutter需要的函数方法。Root isolate运行应用的main code。引擎启动的时候为其增加了必要的绑定，使其具备调度提交渲染帧的能力。对于每一帧，引擎要做的事情有：
      
      Root isolate通知Flutter Engine有帧需要渲染。
      Flutter Engine通知平台，需要在下一个vsync的时候得到通知。
      平台等待下一个vsync
      对创建的对象和Widgets进行Layout并生成一个Layer Tree，这个Tree马上被提交给Flutter Engine。当前阶段没有进行任何光栅化，这个步骤仅是生成了对需要绘制内容的描述。
      创建或者更新Tree，这个Tree包含了用于屏幕上显示Widgets的语义信息。这个东西主要用于平台相关的辅助Accessibility元素的配置和渲染。
      除了渲染相关逻辑之外Root Isolate还是处理来自Native Plugins的消息响应，Timers，Microtasks和异步IO。
      我们看到Root Isolate负责创建管理的Layer Tree最终决定什么内容要绘制到屏幕上。因此这个线程的过载会直接导致卡顿掉帧。
      如果确实有无法避免的繁重计算，建议将其放到独立的Isolate去执行，比如使用compute关键字或者放到非Root Isolate，这样可以避免应用UI卡顿。但是需要注意的是非Root Isolate缺少Flutter引擎需要的一些函数绑定，你无法在这个Isolate直接与Flutter Engine交互。所以只在需要大量计算的时候采用独立Isolate。   
      
   3，GPU Task Runner
   
      GPU Task Runner被用于执行设备GPU的相关调用。UI Task Runner创建的Layer Tree信息是平台不相关，也就是说Layer Tree提供了绘制所需要的信息，具体如何实现绘制取决于具体平台和方式，可以是OpenGL，Vulkan，软件绘制或者其他Skia配置的绘图实现。GPU Task Runner中的模块负责将Layer Tree提供的信息转化为实际的GPU指令。GPU Task Runner同时也负责配置管理每一帧绘制所需要的GPU资源，这包括平台Framebuffer的创建，Surface生命周期管理，保证Texture和Buffers在绘制的时候是可用的。
      基于Layer Tree的处理时长和GPU帧显示到屏幕的耗时，GPU Task Runner可能会延迟下一帧在UI Task Runner的调度。一般来说UI Runner和GPU Runner跑在不同的线程。存在这种可能，UI Runner在已经准备好了下一帧的情况下，GPU Runner却还正在向GPU提交上一帧。这种延迟调度机制确保不让UI Runner分配过多的任务给GPU Runner。
      前面我们提到GPU Runner可以导致UI Runner的帧调度的延迟，GPU Runner的过载会导致Flutter应用的卡顿。一般来说用户没有机会向GPU Runner直接提交任务，因为平台和Dart代码都无法跑进GPU Runner。但是Embeder还是可以向GPU Runner提交任务的。因此建议为每一个Engine实例都新建一个专用的GPU Runner线程。
      
   4，IO Task Runner
     
       前面讨论的几个Runner对于执行任务的类型都有比较强的限制。Platform Runner过载可能导致系统WatchDog强杀，UI和GPU Runner过载则可能导致Flutter应用的卡顿。但是GPU线程有一些必要操作是比较耗时间的，比如IO，而这些操作正是IO Runner需要处理的。  
       
       用户操作，无论是Dart Code还是Native Plugins都是没有办法直接访问IO Runner。尽管Embeder可以将一些一般复杂任务调度到IO Runner，这不会直接导致Flutter应用卡顿，但是可能会导致图片和其它一些资源加载的延迟间接影响性能。所以建议为IO Runner创建一个专用的线程。   
       
   5，各个平台目前默认Runner线程实现
      
      前面我们提到Engine Runner的线程可以按照实际情况进行配置，各个平台目前有自己的实现策略。
      
      iOS和Android
      Mobile平台上面每一个Engine实例启动的时候会为UI，GPU，IO Runner各自创建一个新的线程。所有Engine实例共享同一个Platform Runner和线程。
      
      Fuchsia
      每一个Engine实例都为UI，GPU，IO，Platform Runner创建各自新的线程。    