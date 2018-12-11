**自定义view**

 一，View的原理介绍(https://www.jianshu.com/p/a3014f8442b0)
    
    1，View表示的的屏幕上的某一块矩形的区域，而且所有的View都是矩形的；
    2，View是不能添加子View的，而ViewGroup是可以添加子View的。ViewGroup之所以能够添加子View，是因为它实现了两个接口：ViewParent 和 ViewManager；
    3，Activity之所以能加载并且控制View，是因为它包含了一个Window，所有的图形化界面都是由View显示的而Service之所以称之为没有界面的activity是因为它不包含有Window,不能够加载View；
    4，一个View有且只能有一个父View;
    5，在Android中Window对象通常由PhoneWindow来实现的，PhoneWindow将一个DecorView设置为整个应用窗口的根View,即DecorView为整个Window界面的最顶层View。也可以说DecorView将要显示的具体内容呈现在了PhoneWindow上；
    6，DecorView是FrameLayout的子类,它继承了FrameLayout，即顶层的FrameLayout的实现类是Decorview，它是在phoneWindow里面创建的；
    7，顶层的FrameLayout的父view是Handler，Handler的作用除了线程之间的通讯以外，还可以跟WindowManagerService进行通讯;
    8，windowManagerService是后台的一个服务（System Server的一个种），它控制并且管理者屏幕;
    9，一个应用可以有很多个window，其由windowManager来管理，而windowManager又由windowManagerService来管理;
    10，如果想要显示一个view那么他所要经历三个方法：1.测量measure, 2.布局layout, 3.绘制draw。
    
 二，View的测量/布局/绘制过程   
 
    显示一个View主要进过以下三个步骤：
    1、Measure测量一个View的大小
    2、Layout摆放一个View的位置
    3、Draw画出View的显示内容
    其中measure和layout方法都是final的，无法重写，虽然draw不是final的，但是也不建议重写该方法。
    这三个方法都已经写好了View的逻辑，如果我们想实现自身的逻辑，而又不破坏View的工作流程，可以重写onMeasure、onLayout、onDraw方法。下面来一一介绍这三个方法。
    
  1，View的测量   
    
    Android系统在绘制View之前，必须对View进行测量，即告诉系统该画一个多大的View，这个过程在onMeasure()方法中进行。
    
    MeasureSpec类：
    Android系统给我们提供了一个设计小而强的工具类———MeasureSpec类
    1、MeasureSpe描述了父View对子View大小的期望。里面包含了测量模式和大小。
    2、MeasureSpe类把测量模式和大小组合到一个32位的int型的数值中，其中高2位表示模式，低30位表示大小而在计算中使用位运算的原因是为了提高并优化效率。
    3、我们可以通过以下方式从MeasureSpec中提取模式和大小，该方法内部是采用位移计算。
   
    1. EXACTLY
    即精确值模式，当控件的layout_width属性或layout_height属性指定为具体数值时，例如android:layout_width="100dp"，或者指定为match_parent属性时，系统使用的是EXACTLY 模式。
    2. AT_MOST
    即最大值模式，当控件的layout_width属性或layout_height属性指定为warp_content时，控件大小一般随着控件的子控件或者内容的变化而变化，此时控件的尺寸只要不超过父控件允许的最大尺寸即可。
    3.UNSPECIFIED
    这个属性很奇怪，因为它不指定其大小测量的模式，View想多大就多大，通常情况下在绘制自定义View时才会使用。
    
    View默认的onMeasure()方法只支持EXACTLY模式，所以如果在自定义控件的时候不重写onMeasure()方法的话，就只能使用EXACTLY模式，且控件只可以响应你指定的具体宽高值或者是match_parent属性。如果要让自定义的View支持wrap_content属性，那么就必须重写onMeasure()方法来指定wrap_content时的大小。
    而通过上面介绍的MeasureSpec这个类，我们就可以获取View的测量模式和View想要绘制的大小。
    
    拓展：
    如果想在activity中的onCreat()方法中获取控件测量以后的宽跟高,那么可以用下面的方法:
    view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();
        }
    });
    
  2，View的布局
     
     layout()
     ① Layout方法中接受四个参数，是由父View提供，指定了子View在父View中的左、上、右、下的位置。父View在指定子View的位置时通常会根据子View在measure中测量的大小来决定。
     ② 子View的位置通常还受有其他属性左右，例如父View的orientation，gravity，自身的margin等等，特别是RelativeLayout，影响布局的因素非常多。
     ③ layout方法虽然可以被复写,但是不建议去复写,我们可以直接调用layout方法去确定自身的位置, 而且可以去复写onLayout方法去确定子view的位置
     setFrame()
     ① setFrame方法是一个隐藏方法，所以作为应用层程序员来说，无法重写该方法。该方法体内部通过比对本次的l、t、r、b四个值与上次是否相同来判断自身的位置和大小是否发生了改变。
     ② 如果发生了改变，将会调用invalidate请求重绘。
     ③ 记录本次的l、t、r、b，用于下次比对。
     ④ 如果大小发生了变化，onSizeChanged方法，该方法在大多数View中都是空实现，我们可以重写该方法用于监听View大小发生变化的事件，在可以滚动的视图中重载了该方法，用于重新根据大小计算出需要滚动的值，以便显示之前显示的区域。
     onLayout
     ① onLayout是ViewGroup用来决定子View摆放位置的，各种布局的差异都在该方法中得到了体现。
     ② onLayout比layout多一个参数，changed，该参数是在setFrame通过比对上次的位置得出是否发生了变化，通常该参数没有被使用的意义，因为父View位置和大小不变，并不能代表子View的位置和大小没有发生改变。
     
  3, View绘制
     
     draw方法绘制要遵循一定的顺序:
     1. Draw the background 画背景
     2. If necessary, save the canvas' layers to prepare for fading 如有必要，保存画布的图层，以便为褪色做好准备
     3. Draw view's content 画内容
     4. Draw children 画子view，dispatchDraw
     5. If necessary, draw the fading edges and restore layers 如有必要，画出褪色的边缘并恢复层。 
     6. Draw decorations (scrollbars for instance) 绘制装饰(例如滚动条) 

     
     draw()
     draw是由ViewRoot的performTraversals方法发起，它将调用DecorView的draw方法，并把成员变量canvas传给给draw方法。而在后面draw遍历中，传递的都是同一个canvas。所以android的绘制是同一个window中的所有View都绘制在同一个画布上。等绘制完成，将会通知WMS把canvas上的内容绘制到屏幕上。自定义View时一般不重写该方法。
     onDraw()
     ①View用来绘制自身的实现方法，如果我们想要自定义View，通常需要重载该方法。
     ②TextView中在该方法中绘制文字、光标和CompoundDrawable
     ③ImageView中相对简单，只是绘制了图片
     因为我们的目的就是自定义View，所以当我们测量好了一个View之后，我们就可以间的重写onDraw()这个方法，并在Canvas对象上来绘制所需要的图形。在onDraw()中就有一个参数，该参数就是Canvas canvas对象，使用这个对象即可进行绘图操作；而如果在其他地方，通常需要使用代码创建一个Canvas对象：
     Canvas canvas = new Canvas(bitmap);
     
     之所以要传入一个bitmap,是因为传进来的bitmap与通过这个bitmap创建的Canvas画布是紧紧联系在一起的，这个过程称之为装载画布。
     在View类的onDraw()方法中，我们通过下面的代码，让canvas与bitmap发生直接的联系：
     canvas.drawBitmap(bitmap, 0, 0, null);
     然后将bitmap装载到另外一个Canvas对象中：
     Canvas mCanvas = new Canvas(bitmap);
     通过mCanvas将绘制效果作用在了bitmap上，再通过invalidate()刷新的时候，我们就会发现通过onDraw()方法画出来的bitmap已经发生了改变。
     
     dispatchDraw()
     先根据自身的padding剪裁画布，所有的子View都将在画布剪裁后的区域绘制。
     遍历所有子View，调用子View的computeScroll对子View的滚动值进行计算。
     根据滚动值和子View在父View中的坐标进行画布原点坐标的移动，根据子在父View中的坐标计算出子View的视图大小，然后对画布进行剪裁。
     dispatchDraw的逻辑其实比较复杂，但是幸运的是对子View流程都采用该方式，而ViewGroup已经处理好了，我们不必要重载该方法对子View进行绘制事件的派遣分发。
     重写时，千万千万不要注释了super.方法
    