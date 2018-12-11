package com.andylove.interview.foundation.generic;

import java.util.Set;

/**
 * 泛型
 *
 * @param <不确定杜全中是什么>，但是确定是动物类，所以继承MyGeneric.Animal
 *                  一般用 TKEV代替不确定类型
 */
public class MyGenericClass<不确定杜全中是什么 extends MyGenericClass.Animal> {

    private 不确定杜全中是什么 any;

    public MyGenericClass(不确定杜全中是什么 any) {
        this.any = any;
    }

    public void printGeneric() {
        System.out.print("代号：" + any.getName() + "\n" + "寿龄：" + any.getAge() + "\n" + "公母：" + any.getSexuality() + "\n");
    }

    /**
     * 也是普通法法
     * @return
     */
    public 不确定杜全中是什么 getAny() {
        return any;
    }

    public <H> H printGeneric(H h) {
        System.out.print(h.toString() + "\n");
        return h;
    }

    static class Animal {
        private String name;
        private int age;
        private String sexuality;

        Animal(String name, int age, String sexuality) {
            this.age = age;
            this.name = name;
            this.sexuality = sexuality;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public String getSexuality() {
            return sexuality;
        }
    }

    public static void main(String[] args) {
        //多态
        MyGenericClass<Persion> persion = new GenericExt(new Persion("du", 32, "男"));
        persion.printGeneric();
        persion.getAny();
    }

    /**
     * 继承或者实现泛型类，接口，一定要确定实际类型参数
     */
    static class GenericExt<T> extends MyGenericClass<Persion> implements MyGenericInterface<String, Integer>{
        public GenericExt(Persion any) {
            super(any);
        }

        @Override
        public void addT(String s) {

        }

        @Override
        public void addE(Integer integer) {

        }

        @Override
        public Set<String> makeSet() {
            return null;
        }
    }


    static class Persion extends Animal {

        Persion(String name, int age, String sexuality) {
            super(name, age, sexuality);
        }
    }

}

