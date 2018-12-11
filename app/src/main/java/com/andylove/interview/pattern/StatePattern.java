package com.andylove.interview.pattern;

/**
 * 状态模式
 */
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
        //调音无效，因为关机状态没有任何功能细节实现
        controller.turnUp();
    }

}
