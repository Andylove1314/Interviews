package com.andylove.interview.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * 观察者模式
 */
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
