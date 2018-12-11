package com.andylove.interview.pattern;

/**
 * 抽象工厂模式
 */
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
