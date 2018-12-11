package com.andylove.interview.pattern;

/**
 * 工厂方法模式
 */
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
