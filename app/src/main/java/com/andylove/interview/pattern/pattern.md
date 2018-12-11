#《设计模式》http://www.runoob.com/design-pattern/prototype-pattern.html

   说到设计模式，对于大部分开发者来说可能就是依稀记得一些模式的简单定义，而且还是经常遗忘的那种，只有面试前温习一下，
其实对于设计模式，android系统代码里面，无处不在，下面详细说！

   对于代码的设计，其实并不是为了zb的，它是有时机用处的，设计这个是有原则的：（面向对象的六大原则）

## 单一职责原则
单一原则很简单，就是将一组相关性很高的函数、数据封装到一个类中。换句话说，一个类应该有职责单一。

## 开闭原则
开闭原则理解起来也不复杂，就是一个类应该对于扩展是开放的，但是对于修改是封闭的。我们知道，在开放的app或者是系统中，经常需要升级、维护等，这就要对原来的代码进行修改，可是修改时容易破坏原有的系统，甚至带来一些新的难以发现的BUG。
因此，我们在一开始编写代码时，就应该注意尽量通过扩展的方式实现新的功能，而不是通过修改已有的代码实现。

## 里氏替换原则
里氏替换原则的定义为：所有引用基类的地方必须能透明地使用其子类对象。定义看起来很抽象，其实，很容易理解，本质上就是说，要好好利用继承和多态。
简单地说，就是以父类的形式声明的变量（或形参），赋值为任何继承于这个父类的子类后不影响程序的执行。看一组代码你就明白这个原则了：
    
    //窗口类
    public class Window(){
        public void show(View child){
            child.draw();
        }
    }
    public abstract class View(){
        public abstract void draw();
        public void measure(int widht,int height){
            //测量视图大小
        }
    }
    public class Button extends View{
        public void draw(){
            //绘制按钮
        }
    }
    
    public class TextView extends View{
        public void draw(){
            //绘制文本
        }
    }
Window 类中show函数需要传入View，并且调用View对象的draw函数。而每个继承于View的子对象都有draw的实现，
不存在继承于View但是却没实现draw函数的子类（abstract方法必须实现）。我们在抽象类设计之时就运用到了里氏替换原则。

## 依赖倒置原则
依赖倒置主要是实现解耦，使得高层次的模块不依赖于低层次模块的具体实现细节。怎么去理解它呢，我们需要知道几个关键点：
（1）高层模块不应该依赖底层模块（具体实现），二者都应该依赖其抽象（抽象类或接口）
（2）抽象不应该依赖细节（废话，抽象类跟接口肯定不依赖具体的实现了）
（3）细节应该依赖于抽象（同样废话，具体实现类肯定要依赖其继承的抽象类或接口）
其实，在我们用的Java语言中，抽象就是指接口或者抽象类，二者都是不能直接被实例化；细节就是实现类，实现接口或者继承抽象类而产生的类，就是细节。使用Java语言描述就简单了：就是各个模块之间相互传递的参数声明为抽象类型，而不是声明为具体的实现类；

## 接口隔离原则
接口隔离原则定义：类之间的依赖关系应该建立在最小的接口上。其原则是将非常庞大的、臃肿的接口拆分成更小的更具体的接口。

## 迪米特原则
描述的原则：一个对象应该对其他的对象有最少的了解。什么意思呢？就是说一个类应该对自己调用的类知道的最少。还是不懂？
其实简单来说：假设类A实现了某个功能，类B需要调用类A的去执行这个功能，那么类A应该只暴露一个函数给类B，这个函数表示是实现这个功能的函数，而不是让类A把实现这个功能的所有细分的函数暴露给B。

## 具体设计模式：
###，单例模式
 定义：确保单例类只有一个实例，并且这个单例类提供一个函数接口让其他类获取到这个唯一的实例。
 何时使用单例模式呢：如果某个类，创建时需要消耗很多资源，即new出这个类的代价很大；或者是这个类占用很多内存，如果创建太多这个类实例会导致内存占用太多。
 关于单例模式，虽然很简单，无需过多的解释，但是这里还要提个醒，其实单例模式里面有很多坑。我们去会会单例模式。最简单的单例模式如下：
     
     public class Singleton{
          private static Singleton instance;
          //将默认的构造函数私有化，防止其他类手动new
          private Singleton(){};
          public static Singleton getInstance(){
              if(instance==null)
                  instance=new Singleton();
               return instatnce;
          }
      }
如果是单线程下的系统，这么写肯定没问题。可是如果是多线程环境呢？这代码明显不是线程安全的，存在隐患：某个线程拿到的instance可能是null，可能你会想，这有什么难得，直接在getInstance()函数上加sychronized关键字不就好了。
可是你想过没有，每次调用getInstance()时都要执行同步，这带来没必要的性能上的消耗。注意，在方法上加sychronized关键字时，一个线程访问这个方法时，其他线程无法同时访问这个类其他sychronized方法。的我们看看另外一种实现：
    
    public class Singleton{
        private static Singleton instance;
        //将默认的构造函数私有化，防止其他类手动new
        private Singleton(){};
        public static Singleton getInstance(){
            if(instance==null){
                sychronized(Singleton.class){
                    if(instance==null)
                        instance=new Singleton();
                }
            }
            return instatnce;
        }
    }
为什么需要2次判断是否为空呢？第一次判断是为了避免不必要的同步，第二次判断是确保在此之前没有其他线程进入到sychronized块创建了新实例。这段代码看上去非常完美，但是，，，却有隐患！问题出现在哪呢？主要是在instance=new Singleton();
这段代码上。这段代码会编译成多条指令，大致上做了3件事:
（1）给Singleton实例分配内存
（2）调用Singleton()构造函数，初始化成员字段
（3）将instance对象指向分配的内存（此时instance就不是null啦~）
上面的（2）和（3）的顺序无法得到保证的，也就是说，JVM可能先初始化实例字段再把instance指向具体的内存实例，也可能先把instance指向内存实例再对实例进行初始化成员字段。考虑这种情况：一开始，第一个线程执行instance=new Singleton();这句时，JVM先指向一个堆地址，而此时，又来了一个线程2，它发现instance不是null，就直接拿去用了，但是堆里面对单例对象的初始化并没有完成，最终出现错误~ 。

看看另外一种方式：
    
    public class Singleton{
        private volatile static Singleton instance;
        //将默认的构造函数私有化，防止其他类手动new
        private Singleton(){};
        public static Singleton getInstance(){
            if(instance==null){
                sychronized(Singleton.class){
                    if(instance==null)
                        instance=new Singleton();
                }
            }
            return instatnce;
        }
    }
相比前面的代码，这里只是对instance变量加了一个volatile关键字volatile关键字的作用是：
线程每次使用到被volatile关键字修饰的变量时，都会去堆里拿最新的数据。换句话说，就是每次使用instance时，保证了instance是最新的。注意：volatile关键字并不能解决并发的问题，关于volatile请查看其它相关文章。但是volatile能解决我们这里的问题。
单例模式比较简单，这里就不深究其在android中的用处了。

### 构建者模式（Builder模式）
定义：将一个复杂对象的构造与它的表示分离，使得同样的构造过程可以创建不同的表示。
Builder模式，主要是在创建某个对象时，需要设定很多的参数（通过setter方法），但是这些参数必须按照某个顺序设定，或者是设置步骤不同会得到不同结果。

    public class MyBuilder{
        private int id;
        private String num;
        public MyData build(){
            MyData d=new MyData();
            d.setId(id);
            d.setNum(num);
            return t;
        }
        public MyBuilder setId(int id){
            this.id=id;
            return this;
        }
        public MyBuilder setNum(String num){
            this.num=num;
            return this;
        }
    
    }
    
    public class Test{
        public static void  main(String[] args){
            MyData d=new MyBuilder().setId(10).setNum("hc").build();
        }
    }
注意到，Builer类的setter函数都会返回自身的引用this，
这主要是用于链式调用，这也是Builder设计模式中的一个很明显的特征。
那么在android中用的最多的 就是这个了：
     
     AlertDialog.Builer builder=new AlertDialog.Builder(context);
     builder.setIcon(R.drawable.icon)
         .setTitle("title")
         .setMessage("message")
         .setPositiveButton("Button1", 
             new DialogInterface.OnclickListener(){
                 public void onClick(DialogInterface dialog,int whichButton){
                     setTitle("click");
                 }   
             })
         .create()
         .show();
show一个对话框！

### 原型模式
原型设计模式非常简单，就是将一个对象进行拷贝。

什么时候会用到原型模式呢？我个人认为，可以在类的属性特别多，但是又要经常对类进行拷贝的时候可以用原型模式，这样代码比较简洁，而且比较方便。
另外要注意的是，还有深拷贝和浅拷贝。深拷贝就是把对象里面的引用的对象也要拷贝一份新的对象，并将这个新的引用对象作为拷贝的对象引用。说的比较绕哈~，举个例子，
假设A类中有B类的引用b，现在需要对A类实例进行拷贝，那么深拷贝就是，先对b进行一次拷贝得到nb，然后把nb作为A类拷贝的对象的引用，如此一层一层迭代拷贝，把所有的引用都拷贝结束。浅拷贝则不是。

原型模式比较简单，看看Android怎么运用原型模式：
   
    Uri uri=Uri.parse("smsto:10086");
    Intent shareIntent=new Intent(Intent.ACTION_SENDTO,uri);
    //克隆副本
    Intent intent=(Intetn)shareIntent.clone();
    startActivity(intent);

### 工厂方法模式
定义：定义一个创建对象的接口，让子类决定实例化哪个类
先看一个例子：

    public class FactoryPattern {

    public abstract class Product {
        public abstract void create();
    }

    public class ProductA extends Product {
        @Override
        public void create() {
            System.out.print("ProductA");
        }
    }

    public class ProductB extends Product {
        @Override
        public void create() {
            System.out.print("ProductB");
        }
    }

    public class ProductC extends Product {
        @Override
        public void create() {
            System.out.print("ProductC");
        }
    }

    /**
     * 生产商品A，B，C
     *
     * @return
     */
    public Product createProduct(String proName) {
        if ("ProductA".equals(proName)) {
            return new ProductA();
        }
        if ("ProductB".equals(proName)) {
            return new ProductB();
        }
        if ("ProductC".equals(proName)) {
            return new ProductC();
        }
        return null;
    }
    public static void main(String[] args){
        new FactoryPattern().createProduct("ProductB");
    }
}

同样的，我们不希望记住这个例子，而是通过Android中的代码来记忆：
其实，在getSystemService方法中就是用到了工厂模式，他就是根据传入的参数决定创建哪个对象，当然了，由于返回的都是以单例模式存在的对象，因此不用new了，直接把单例返回就好。
    
    public Object getSystemService(String name) {
        if (getBaseContext() == null) {
            throw new IllegalStateException("System services not available to Activities before onCreate()");
        }
        //........
        if (WINDOW_SERVICE.equals(name)) {
             return mWindowManager;
        } else if (SEARCH_SERVICE.equals(name)) {
            ensureSearchManager();
            return mSearchManager;
        }
        //.......
        return super.getSystemService(name);
      }
  
 ### 抽象工厂模式
  抽象工厂模式：为创建一组相关或者是相互依赖的对象提供一个接口，而不需要制定他们的具体类
  看个例子吧，将它跟工厂方法模式做个对比：
      
      public abstract class AbstractFactoryPattern {
  
      public abstract class AbProduct1{
          public abstract void create();
      }
      public abstract class AbProduct2{
          public abstract void create();
      }
  
      public class AbProduct1Impl extends AbProduct1{
          @Override
          public void create() {
              System.out.println("具体产品1方法！");
          }
      }
      public class AbProduct2Impl extends AbProduct2{
          @Override
          public void create() {
              System.out.println("具体产品2方法！");
          }
      }
  
      public class AbProduct1Impl1 extends AbProduct1{
          @Override
          public void create() {
              System.out.println("具体产品1方法！");
          }
      }
      public class AbProduct2Impl1 extends AbProduct2{
          @Override
          public void create() {
              System.out.println("具体产品2方法！");
          }
      }
  
      public abstract AbProduct1 createProduct1();
      public abstract AbProduct2 createProduct2();
  
      public class Factory1 extends AbstractFactoryPattern{
  
          @Override
          public AbProduct1 createProduct1() {
              return new AbProduct1Impl();
          }
  
          @Override
          public AbProduct2 createProduct2() {
              return new AbProduct2Impl();
          }
      }
  
      public class Factory2 extends AbstractFactoryPattern{
  
          @Override
          public AbProduct1 createProduct1() {
              return new AbProduct1Impl1();
          }
  
          @Override
          public AbProduct2 createProduct2() {
              return new AbProduct2Impl1();
          }
      }
    }
    
 ### 策略模式
  定义：在策略模式（Strategy Pattern）中，一个类的行为或其算法可以在运行时更改。这种类型的设计模式属于行为型模式。
        在策略模式中，我们创建表示各种策略的对象和一个行为随着策略对象改变而改变的 context 对象。策略对象改变 context 对象的执行算法。
  
  优点： 1、算法可以自由切换。 2、避免使用多重条件判断。 3、扩展性良好。
  缺点： 1、策略类会增多。 2、所有策略类都需要对外暴露。
  
  举个例子来理解吧，比如，你现在又很多排序算法：冒泡、希尔、归并、选择等等。我们要根据实际情况来选择使用哪种算法，有一种常见的方法是，通过if...else或者case...等条件判断语句来选择。但是这个类的维护成本会变高，维护时也容易发生错误。
  如何使用策略模式呢，我不打算写示例代码了，简单描述一下，就将前面说的算法选择进行描述。我们可以定义一个算法抽象类AbstractAlgorithm，这个类定义一个抽象方法sort()。每个具体的排序算法去继承AbstractAlgorithm类并重写sort()实现排序。
  在需要使用排序的类Client类中，添加一个setAlgorithm(AbstractAlgorithm al)；方法将算法设置进去，每次Client需要排序而是就调用al.sort()。
  
  看看Android中哪里出现了策略模式，其中在属性动画中使用时间插值器的时候就用到了。
  在使用动画时，你可以选择线性插值器LinearInterpolator、加速减速插值器AccelerateDecelerateInterpolator、减速插值器DecelerateInterpolator以及自定义的插值器。这些插值器都是实现根据时间流逝的百分比来计算出当前属性值改变的百分比。
  通过根据需要选择不同的插值器，实现不同的动画效果。这些比较好理解，就不去粘贴Android源码了。
  
  ### 状态模式
  状态模式中，行为是由状态来决定的，不同状态下有不同行为。状态模式和策略模式的结构几乎是一模一样的，主要是他们表达的目的和本质是不同。状态模式的行为是平行的、不可替换的，策略模式的行为是彼此独立可相互替换的。
  举个例子把，比如电视，电视有2个状态，一个是开机，一个是关机，开机时可以切换频道，关机时切换频道不做任何响应。
      
      public class StatePattern {
      
          /**
           * 电视状态
           */
          public interface TvState{
              public void nextChannerl();
              public void prevChannerl();
              public void turnUp();
              public void turnDown();
          }
      
          /**
           * 关机状态，各个功能不好使
           */
          public static class TvOffState implements TvState{
      
              @Override
              public void nextChannerl() {
      
              }
      
              @Override
              public void prevChannerl() {
      
              }
      
              @Override
              public void turnUp() {
      
              }
      
              @Override
              public void turnDown() {
      
              }
          }
      
          /**
           * 开机状态
           */
          public static class TvOnState implements TvState{
      
              @Override
              public void nextChannerl() {
                  System.out.print("下个频道");
              }
      
              @Override
              public void prevChannerl() {
                  System.out.print("上个频道");
              }
      
              @Override
              public void turnUp() {
                  System.out.print("调高音量");
              }
      
              @Override
              public void turnDown() {
                  System.out.print("调低音量");
              }
          }
      
          public interface TvController{
              public void powerOn();
              public void powerOff();
          }
      
          public static class Controller implements TvController{
              TvState mTvState;
              private void setTvState(TvState tvState){
                  mTvState=tvState;
              }
              @Override
              public void powerOn() {
                  setTvState(new TvOnState());
                  System.out.println("开机啦");
              }
      
              @Override
              public void powerOff() {
                  setTvState(new TvOffState());
                  System.out.println("关机啦");
              }
      
              public void nextChannerl() {
                  mTvState.nextChannerl();
              }
      
              public void prevChannerl() {
                  mTvState.prevChannerl();
              }
      
              public void turnUp() {
                  mTvState.turnUp();
              }
      
              public void turnDown() {
                  mTvState.turnDown();
              }
      
          }
      
          public static void main(String[] args){
              //遥控器实例
              Controller controller = new Controller();
              //开机
              controller.powerOn();
              //切频有效
              controller.nextChannerl();
      
              //关机
              controller.powerOff();
              //调音无效
              controller.turnUp();
          }
      
      }
   
   在Android源码中，哪里有用到状态模式呢？其实很多地方用到了，举一个地方例子，就是WIFI管理模块。
   当WIFI开启时，自动扫描周围的接入点，然后以列表的形式展示；当wifi关闭时则清空。这里wifi管理模块就是根据不同的状态执行不同的行为。由于代码太多，我就不手打敲入了~我们只要知道大致Android里面在哪里用到了以及大概是怎么用的就好。
   
   ### 责任链模式
   定义：顾名思义，责任链模式（Chain of Responsibility Pattern）为请求创建了一个接收者对象的链。这种模式给予请求的类型，对请求的发送者和接收者进行解耦。这种类型的设计模式属于行为型模式。 
         在这种模式中，通常每个接收者都包含对另一个接收者的引用。如果一个对象不能处理该请求，那么它会把相同的请求传给下一个接收者，依此类推。
   
   demo：log打印
   
      public class ChainOfResponsibilityPattern {
      
          public static abstract class Lloger {
              protected int level;
              public static final int V_INFO = 0;
              public static final int V_WARN = 1;
              public static final int V_ERROR = 2;
      
              private Lloger nextLloger;
      
              public void setNextLloger(Lloger next) {
                  this.nextLloger = next;
              }
      
              public void logMessage(int v, String msg) {
                  if (v <= level) {
                      print(msg);
                  }
                  //链式调用
                  if (nextLloger != null) {
                      nextLloger.logMessage(v, msg);
                  }
              }
      
              abstract void print(String msg);
          }
      
          public static class ErrorLog extends Lloger {
      
              public ErrorLog(int l){
                  level = l;
              }
              @Override
              void print(String msg) {
                  System.out.print(msg);
              }
          }
          public static class WarnLog extends Lloger {
      
              public WarnLog(int l){
                  level = l;
              }
              @Override
              void print(String msg) {
                  System.out.print(msg);
              }
          }
          public static class InfoLog extends Lloger {
      
              public InfoLog(int l){
                  level = l;
              }
              @Override
              void print(String msg) {
                  System.out.print(msg);
              }
          }
      
          public static void main(String[] args){
              Lloger log = new ErrorLog(Lloger.V_ERROR);
              Lloger logNext = new WarnLog(Lloger.V_WARN);
              Lloger logNext2 = new InfoLog(Lloger.V_INFO);
      
              //责任链
              log.setNextLloger(logNext);
              logNext.setNextLloger(logNext2);
      
              log.logMessage(Lloger.V_ERROR, "error...");
              log.logMessage(Lloger.V_WARN, "warn...");
              log.logMessage(Lloger.V_INFO, "info...");
          }
   相信聪明的你很容易理解吧，基本不需要例子来解释了，直接进如到Android源码中哪里用到了责任链：android触摸事件分发原理就是这种模式，activity--phonewindow--decoreview--containerLayout--viewgroup--view。
   父View先接收到点击事件，如果父View不处理则交给子View，依次往下传递。
   
   ### 解释器模式
   定义：解释器模式（Interpreter Pattern）提供了评估语言的语法或表达式的方式，它属于行为型模式。
   意图：给定一个语言，定义它的文法表示，并定义一个解释器，这个解释器使用该标识来解释语言中的句子。
   主要解决：对于一些固定文法构建一个解释句子的解释器。
   
   从定义中看起来比较抽象，其实，很简单，很容易理解！就是相当于自定义一个格式的文件，然后去解析它。不用理解的那么复杂！
   我们看看Android中哪里用到了，从我们第一次学Android时就知道，四大组件需要在AndroidManifest.xml中定义，其实AndroidManifest.xml就定义了<Activity>，<Service>等标签（语句）的属性以及其子标签，规定了具体的使用（语法），通过PackageManagerService（解释器）进行解析。
  
  ### 命令模式
  定义：命令模式（Command Pattern）是一种数据驱动的设计模式，它属于行为型模式。请求以命令的形式包裹在对象中，并传给调用对象。调用对象寻找可以处理该命令的合适的对象，并把该命令传给相应的对象，该对象执行命令。
      
    public class OrderPattern {
      /**
       * 命令
       */
      public interface Order {
          public void excute();
      }
  
      /**
       * 请求类
       */
      public static class Stock {
  
          private String name = "ABC";
          private int quantity = 10;
  
          public void buy() {
              System.out.println("buy:" + name + quantity);
          }
  
          public void sell() {
              System.out.println("sell:" + name + quantity);
          }
      }
  
      public static class Buy implements Order {
          private Stock abcStock;
  
          public Buy(Stock abcStock){
              this.abcStock = abcStock;
          }
          @Override
          public void excute() {
              abcStock.buy();
          }
      }
  
      public static class Sell implements Order {
          private Stock abcStock;
  
          public Sell(Stock abcStock){
              this.abcStock = abcStock;
          }
          @Override
          public void excute() {
              abcStock.sell();
          }
      }
  
      /**
       * 命令调用者
       */
      public static class Broker {
          private List<Order> orderList = new ArrayList<Order>();
  
          public void takeOrder(Order order) {
              orderList.add(order);
          }
  
          public void excOrders() {
              for (Order order : orderList) {
                  order.excute();
              }
              orderList.clear();
          }
      }
  
      public static void main(String[] args) {
          //创建命令任务
          Stock stock = new Stock();
          Buy buyStockOrder = new Buy(stock);
          Sell sellStockOrder = new Sell(stock);
  
          //执行命令
          Broker broker = new Broker();
          broker.takeOrder(buyStockOrder);
          broker.takeOrder(sellStockOrder);
          broker.excOrders();
      }
    }
 那么Android中哪里用到了命令模式呢？在framework层还真不多。
 但是在底层却用到了，一个比较典型的例子就是在Android事件机制中，底层逻辑对事件的转发处理。每次的按键事件会被封装成NotifyKeyArgs对象。通过InputDispatcher封装具体的事件操作。

### 观察者模式

当对象间存在一对多关系时（1被观察者：n观察者、1发布者：n订阅者），则使用观察者模式（Observer Pattern）。比如，当一个对象被修改时，则会自动通知它的依赖对象。观察者模式属于行为型模式。
主要解决：一个对象状态改变给其他对象通知的问题，而且要考虑到易用和低耦合，保证高度的协作。
何时使用：一个对象（目标对象）的状态发生改变，所有的依赖对象（观察者对象）都将得到通知，进行广播通知。
如何解决：使用面向对象技术，可以将这种依赖关系弱化。
优点： 1、观察者和被观察者是抽象耦合的。 
       2、建立一套触发机制。
缺点： 1、如果一个被观察者对象有很多的直接和间接的观察者的话，将所有的观察者都通知到会花费很多时间。 
       2、如果在观察者和观察目标之间有循环依赖的话，观察目标会触发它们之间进行循环调用，可能导致系统崩溃。 
       3、观察者模式没有相应的机制让观察者知道所观察的目标对象是怎么发生变化的，而仅仅只是知道观察目标发生了变化。
    
    public class ObserverPattern {
    
        /**
         * 观察者基类
         */
        public static abstract class Observer {
            public abstract void update();
        }
    
        /**
         * 发布信息执行者
         */
        public static class Subject {
            private int stateCode = 0;
            protected List<Observer> observers = new ArrayList<>();
    
            public void atach(Observer observer) {
                observers.add(observer);
            }
    
            public void detach(Observer observer) {
                observers.remove(observer);
            }
    
            public void setStateCode(int stateCode) {
                this.stateCode = stateCode;
                notifyRefresh();
            }
    
            private void notifyRefresh() {
                if (observers.isEmpty()) {
                    return;
                }
                for (Observer observer : observers) {
                    observer.update();
                }
            }
        }
    
        /**
         * 真实观察者
         */
        public static class RealObserver extends Observer {
    
            private Subject mSubject;
    
            public RealObserver(Subject subject) {
                this.mSubject = subject;
                this.mSubject.atach(this);
            }
    
            @Override
            public void update() {
                System.out.print(mSubject.stateCode + "变了");
            }
        }
    
        public static void main(String[] args) {
            Subject subject = new Subject();
            RealObserver realObserver = new RealObserver(subject);
            subject.setStateCode(666);
            subject.detach(realObserver);
        }
    }
    
    android中的应用有哪些呢？
    1，recycleview刷新机制 notifyDataSetChanged()
    2，ContentObserver，数据变化观察者
    等等
    
 ### 备忘录模式
 定义：备忘录模式（Memento Pattern）保存一个对象的某个状态，以便在适当的时候恢复对象。备忘录模式属于行为型模式。
 主要解决：所谓备忘录模式就是在不破坏封装的前提下，捕获一个对象的内部状态，并在该对象之外保存这个状态，这样可以在以后将对象恢复到原先保存的状态。
 应用实例： 1、后悔药。 2、打游戏时的存档。 3、Windows 里的 ctri + z。 4、IE 中的后退。 4、数据库的事务管理。
 public class MementoPattern {
 
     /**
      * 备忘实体类
      */
     public static class InstanceState{
         private String state;
         public InstanceState(String state) {
             this.state = state;
         }
 
         public String getState() {
             return state;
         }
     }
 
     /**
      * 备忘及恢复
      */
     public static class Originator {
         private String state;
 
         public void setState(String state){
             this.state = state;
         }
 
         public String getState(){
             return state;
         }
 
        /**
        * 保存在备忘数据里面，以便从改对象中恢复
        */
         public InstanceState saveStateToMemento(){
             return new InstanceState(state);
         }
 
         /**
         * 用时直接恢复出来
          */
         public void getStateFromMemento(InstanceState memento){
             state = memento.getState();
         }
     }
 
      /**
       * 备忘录操作者
       */
     public static class CareTaker {
         private List<InstanceState> mementoList = new ArrayList<>();
 
         public void add(InstanceState state){
             mementoList.add(state);
         }
 
         public InstanceState get(int index){
             return mementoList.get(index);
         }
     }
 
     public static void main(String[] args){
         Originator originator = new Originator();
         CareTaker careTaker = new CareTaker();
         originator.setState("State #1");
         originator.setState("State #2");
         careTaker.add(originator.saveStateToMemento());
         originator.setState("State #3");
         careTaker.add(originator.saveStateToMemento());
         originator.setState("State #4");
 
         System.out.println("Current State: " + originator.getState());
         originator.getStateFromMemento(careTaker.get(0));
         System.out.println("First saved State: " + originator.getState());
         originator.getStateFromMemento(careTaker.get(1));
         System.out.println("Second saved State: " + originator.getState());
     }
 
 }
 android中的应用是：Activity的onSaveInstanceState和onRestoreInstanceState就是用到了备忘录模式，分别用于保存和恢复。     
 
 ### 迭代器模式
 迭代器模式（Iterator Pattern）是 Java 和 .Net 编程环境中非常常用的设计模式。这种模式用于顺序访问集合对象的元素，不需要知道集合对象的底层表示。
 迭代器模式属于行为型模式。
 主要解决：不同的方式来遍历整个整合对象。
 关键代码：定义接口：hasNext, next。
 优点： 1、它支持以不同的方式遍历一个聚合对象。 2、迭代器简化了聚合类。 3、在同一个聚合上可以有多个遍历。 4、在迭代器模式中，增加新的聚合类和迭代器类都很方便，无须修改原有代码。
 缺点：由于迭代器模式将存储数据和遍历数据的职责分离，增加新的聚合类需要对应增加新的迭代器类，类的个数成对增加，这在一定程度上增加了系统的复杂性。
 使用场景： 1、访问一个聚合对象的内容而无须暴露它的内部表示。 2、需要为聚合对象提供多种遍历方式。 3、为遍历不同的聚合结构提供一个统一的接口。
 
       public class IteratorPattern {
       
           /**
            * 迭代器
            */
           public interface MyIterator{
               public boolean hasNext();
               public String next();
           }
       
           public interface IteratorContainer {
               public MyIterator getIterator();
           }
       
           public static class MyResposity implements IteratorContainer{
               public static String[] names = {"mayun","mahuateng","gaichi"};
       
       
               @Override
               public MyIterator getIterator() {
                   return new ContainerIterator();
               }
       
               /**
                * 可以定义不同的迭代方式类
                */
               public static class ContainerIterator implements MyIterator{
       
                   private int index = 0;
                   @Override
                   public boolean hasNext() {
                       return (index<names.length);
                   }
       
                   @Override
                   public String next() {
                       if (hasNext()){
                           return names[index++];
                       }
                       return null;
                   }
               }
       
           }
       
           public static void main(String[] args){
       
               MyResposity resposity = new MyResposity();
               MyResposity.ContainerIterator iterator = (MyResposity.ContainerIterator) resposity.getIterator();
               while (iterator.hasNext()){
                   System.out.print(iterator.next()+"\n");
               }
           }
       
       }
  那么android中用的迭代器模式有哪些呢？
  最典型的就是Cursor用到了迭代器模式，当我们使用SQLiteDatabase的query方法时，返回的就是Cursor对象，通过如下方式去遍历：
  
  ### 模板方法模式
  定义：定义一个操作中的算法的骨架，而将一些步骤延迟到子类中。模板方法使得子类可以不改变一个算法的结构即可重定义该算法的某些特定步骤。
  主要解决：一些方法通用，却在每一个子类都重新写了这一方法。
  何时使用：有一些通用的方法。
  如何解决：将这些通用算法抽象出来。
  关键代码：在抽象类实现，其他步骤在子类实现。
  
      public class TemplatePattern {
      
          public static abstract class Activity{
              public abstract void onCreate();
              public abstract void onResume();
              public abstract void onStop();
      
              public final void startActivity(){
                  onCreate();
                  onResume();
                  onStop();
              }
          }
      
          /**
           * 修改部分算法的activity
           */
          public static class FragmentActivity extends Activity{
              @Override
              public void onCreate() {
                  System.out.print("FragmentActivity onCreate\n");
              }
      
              @Override
              public void onResume() {
                  System.out.print("FragmentActivity onResume\n");
      
              }
      
              @Override
              public void onStop() {
                  System.out.print("FragmentActivity onStop\n");
      
              }
      
          }
      
          /**
           * 修改部分算法的activity
           */
          public static class FlutterActivity extends Activity{
              @Override
              public void onCreate() {
                  System.out.print("FlutterActivity onCreate\n");
              }
      
              @Override
              public void onResume() {
      
                  System.out.print("FlutterActivity onResume\n");
              }
      
              @Override
              public void onStop() {
      
                  System.out.print("FlutterActivity onStop\n");
              }
      
          }
      
          public static void main(String[] args){
              Activity activity = new FragmentActivity();
              activity.startActivity();
              activity = new FlutterActivity();
              activity.startActivity();
          }
      
      }
   在安卓中的案例是？
   启动一个Activity过程非常复杂，如果让开发者每次自己去调用启动Activity过程无疑是一场噩梦。好在启动Activity大部分代码时不同的，但是有很多地方需要开发者定制。也就是说，整体算法框架是相同的，但是将一些步骤延迟到子类中，比如Activity的onCreate、onStart等等。这样子类不用改变整体启动Activity过程即可重定义某些具体的操作了~。
      
   ### 访问者模式
   定义：
   ### 访问者模式
   定义：
   ### 代理模式
   定义：为其他类提供一种代理以控制这个对象的访问。
   其实比较好理解，就是当我们需要对一个对象进行访问时，我们不直接对这个对象进行访问，而是访问这个类的代理类，代理类能帮我们执行我们想要的操作。
   