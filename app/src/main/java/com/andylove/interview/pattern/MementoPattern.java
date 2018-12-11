package com.andylove.interview.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * 备忘录模式（备份用于恢复数据）
 */
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
         * 保存在备忘数据里面，以便从该对象中恢复
         */
        public InstanceState saveStateToMemento(){
            return new InstanceState(state);
        }

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
