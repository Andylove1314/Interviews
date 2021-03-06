**fails**
 ### so加载方式，有几种？，注册jni方法的方式，有几种？，都是什么？
     答：1，so加载方式：
         （1）System.loadLibrary(${lib名称})，静态加载，dalvik在加载class时，即从lib里直接load so包。
         （2）System.load(${so文件外部绝对路径})，根据路径动态load so包。
         2，jni方法注册方式：
         （1）静态注册：创建java类，定义native方法，然后用javah命令生成相对应的.h头文件，然后创建其.cpp源文件，然后用.mk文件，借助ndkBundle工具，生成so包。AS3.0之后可以用通过配置CMakeLists
         文件，借助cmake工具，生成so
         缺点：初次调用native函数时要根据函数名字搜索对应的JNI层函数来建立关联关系，这样会影响运行效率。
         （2）动态注册：创建java类，定义native方法，创建jni目录，然后在该目录创建hello.c文件，定义native方法（随意），然后指定java类（/路径），定义JNINativeMethod数组，数组包括 native方法名，返回值类型，还有c的方法，然后去注册native方法，然后实现JNI_OnLoad，以备load用户空间的so包。
           以上就是动态注册JNI函数的方法，上面只是一个简单的例子，如果你还想再实现一个native方法，只需要在JNINativeMethod数组中添加一个元素，然后实现对应的JNI层函数即可，下次我们加载动态库时就会动态的将你声明的方法注册到JNI环境中，而不需要你做其他任何操作。
         
### Linlayout与RelativeLayout的渲染效率问题分析


### 工作线程安全问题
    
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
          
   ### 接口gzip压缩
   
   ### ArrayList，LinkedList区别
   
    1，ArrayList：
     （1）implements List Colection extends Iterable
     （2）ArrayList实现了List接口,它是以数组的方式来实现的,数组的特性是可以使用索引的方式来快速定位对象的位置,因此对于快速的随机取得对象的需求,使用ArrayList实现执行效率上会比较好。
      
    2，LinkedList：
    （1）implements List Colection extends Iterable
    （2）LinkedList是采用链表的方式来实现List接口的,它本身有自己特定的方法，如: addFirst(),addLast(),getFirst(),removeFirst()等. 由于是采用链表实现的,因此在进行insert和remove动作时在效率上要比ArrayList要好得多!适合用来实现Stack(堆栈)与Queue(队列),前者先进后出，后者是先进先出
   
    3，优缺点：
    （1）ArrayList是实现了基于动态数组的数据结构，LinkedList基于链表的数据结构。 
    （2）对于随机访问get和set（改，查），ArrayList觉得优于LinkedList，因为LinkedList要移动指针。 
    （3）对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList要移动数据。
     解析: 因为ArrayList是使用数组实现的,若要从数组中删除或插入某一个对象，需要移动后段的数组元素，从而会重新调整索引顺序,调整索引顺序会消耗一定的时间，所以速度上就会比LinkedList要慢许多. 相反,LinkedList是使用链表实现的,若要从链表中删除或插入某一个对象,只需要改变前后对象的引用即可! LinkedList查找需要进行遍历查询，效率较低。

### Animation 与 Animator区别
   
   1，Animation：视图动画
   
    即对view的旋转，渐变，移动，伸缩的控制，视图动画不会随着view的变化而更改实际位置或者状态。
    applyTransformation()是核心，applyTransformation()方法就是动画具体的实现，系统会以一个比较高的频率来调用这个方法，一般情况下60FPS，是一个非常流畅的画面了，也就是16ms，为了验证这一点，我在applyTransformation方法中加入计算时间间隔并打印的代码进行验证。
    
    View动画的“不变性”：
    根据介绍，View动画的执行最终并不影响View对象本身的“放置区域”。
    例如对一个按钮执行TranslateAnimation动画，将它“移动到另一个位置”，那么新位置是无法点击的，而原始按钮的位置依然可以点击。
    这是为什么呢？
    作为结论，View动画只是针对View绘制的内容进行各种变换，但并不影响View对象本身的布局属性。也就是View动画只是改变了对应View得到的父布局的Canvas上绘制的内容，但没有新的layout过程，View的left,top,right,bottom都没改变。
   
   2，Animator：属性动画
      
      属性动画系统是一个强大的框架，允许您动画几乎任何东西。您可以定义动画以随时间更改任何对象属性，无论它是否绘制到屏幕。属性动画在指定的时间长度内更改属性（对象中的字段）值。要为某些内容设置动画，请指定要设置动画的对象属性，例如对象在屏幕上的位置，要为其设置动画的时间长度以及要在其间设置动画的值。
      属性动画系统允许您定义动画的以下特征：
      持续时间：您可以指定动画的持续时间。默认长度为300毫秒。
      时间插值：您可以指定如何计算属性值作为动画当前已用时间的函数。
      重复计数和行为：您可以指定是否在到达持续时间结束时重复动画以及重复动画的次数。您还可以指定是否要反向播放动画。将其设置为反向向前播放动画然后反复播放动画，直到达到重复次数。
      动画设置：您可以将动画分组为一起或按顺序或在指定延迟后播放的逻辑集。
      帧刷新延迟：您可以指定刷新动画帧的频率。默认设置为每10毫秒刷新一次，但应用程序刷新帧的速度最终取决于系统整体的繁忙程度以及系统为基础计时器提供服务的速度。
      
   3，区别：
      
      官方：视图动画系统提供仅为View 对象设置动画的功能，因此如果要为非View对象设置动画，则必须实现自己的代码才能执行此操作。视图动画系统也受到约束，因为它仅将View对象的一些方面暴露给动画，例如视图的缩放和旋转而不是背景颜色。
      视图动画系统的另一个缺点是它只修改了“绘制视图的位置”，而不是实际的视图本身。例如，如果您设置了一个按钮以在屏幕上移动，则该按钮会正确绘制，但您可以单击该按钮的实际位置不会更改，因此您必须实现自己的逻辑来处理此问题。
      使用属性动画系统，可以完全删除这些约束，并且可以为任何对象（视图和非视图）的任何属性设置动画，并且实际修改了对象本身。属性动画系统在执行动画方面也更加强大。在较高级别，您可以将动画师分配给要设置动画的属性，例如颜色，位置或大小，并可以定义动画的各个方面，例如多个动画师的插值和同步。
      但是，视图动画系统设置时间较短，编写代码较少。如果视图动画完成了您需要执行的所有操作，或者现有代码已按您希望的方式工作，则无需使用属性动画系统。如果出现用例，将两种动画系统用于不同情况也是有意义的。
      个人：在使用animation和animator的时候，你可以体会一下，这两种动画的思路给人的感觉完全不一样，animation给人一样，View在控制动画的感觉，而animator则刚好相反，它给人一种，动画本身就是存放在那，是使用动画来控制目标对象的。