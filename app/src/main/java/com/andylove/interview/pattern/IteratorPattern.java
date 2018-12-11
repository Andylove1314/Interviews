package com.andylove.interview.pattern;

/**
 * 迭代器模式
 */
public class IteratorPattern {

    /**
     * 迭代器
     */
    public interface MyIterator{
        public boolean hasNext();
        public String next();
        public void moveToFirst();
        public void moveToLast();
    }

    public interface IteratorContainer {
        public MyIterator getIterator();
    }

    public static class MyResposity implements IteratorContainer{
        public static String[] names = {"mayun","mahuateng","gaichi"};


        @Override
        public MyIterator getIterator() {
            return new ContainerIterator();
//            return new ContainerIterator2();
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

            @Override
            public void moveToFirst() {
                index = 0;
            }

            @Override
            public void moveToLast() {
                index = names.length - 1;
            }
        }


        /**
         * 可以定义不同的迭代方式类
         */
        public static class ContainerIterator2 implements MyIterator{

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public String next() {
                return null;
            }

            @Override
            public void moveToFirst() {

            }

            @Override
            public void moveToLast() {

            }
        }

    }

    public static void main(String[] args){

        MyResposity resposity = new MyResposity();
        MyResposity.ContainerIterator iterator = (MyResposity.ContainerIterator) resposity.getIterator();
        while (iterator.hasNext()){
            System.out.print(iterator.next()+"\n");
        }
        iterator.moveToFirst();
        while (iterator.hasNext()){
            System.out.print(iterator.next()+"\n");
        }
    }

}
