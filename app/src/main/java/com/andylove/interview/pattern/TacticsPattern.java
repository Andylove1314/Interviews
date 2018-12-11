package com.andylove.interview.pattern;

/**
 * 策略模式
 */
public class TacticsPattern {

    private TacticsInterface mTact;
    public interface TacticsInterface{
        public int caculateNum(int a, int b);
    }

    public static class Add implements TacticsInterface{

        @Override
        public int caculateNum(int a, int b) {
            System.out.print(a+b);
            return a+b;
        }
    }

    public static class Min implements TacticsInterface{

        @Override
        public int caculateNum(int a, int b) {
            System.out.print(a-b);
            return a-b;
        }
    }

    public TacticsPattern(TacticsInterface tacticsInterface){
        mTact = tacticsInterface;
    }

    public int caculate(int a, int b){
        return mTact.caculateNum(a, b);
    }

    public static void main(String[] args){
        //根据入参决定其实现算法，妙，实在是妙啊！
        new TacticsPattern(new Add()).caculate(1,2);
        new TacticsPattern(new Min()).caculate(1,2);
    }

}
