package com.andylove.interview.foundation.generic;

/**
 * 普通类的泛型方法
 */
public class CustomClassGenericMethod<T> {

    /**
     * 非静态方法
     * @param h 泛型方法的形参
     * @param <H> System.out.print(h.toString()+"\n");
     * @return 返回形参
     */
    public <H> H printGeneric(H h) {
        System.out.print(h.toString() + "\n");
        return h;
    }

    /**
     * 多参数
     * @param args
     * @param <H>
     */
    public <H> void printGeneric(H... args) {

    }

    /**
     * 静态泛型方法:
     * 静态方法有一种情况需要注意一下，那就是在类中的静态方法使用泛型：静态方法无法访问类上定义的泛型；如果静态方法操作的引用数据类型不确定的时候，必须要将泛型定义在方法上。
     * @param e
     * @param <E>
     * @return
     */
    public static <E> E printGeneric2(E e) {
        System.out.print(e + "\n");
        return e;
    }

    public static void main(String[] args) {
        CustomClassGenericMethod custom = new CustomClassGenericMethod();
        custom.printGeneric(new Custom2());
        CustomClassGenericMethod.printGeneric2("我是静态泛型方法的实参");
    }

    static class Custom2 {
        public Custom2() {
            System.out.print("我是一个普通类");
        }
    }

}
