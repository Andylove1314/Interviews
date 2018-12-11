package com.andylove.interview.pattern;

/**
 * 责任链模式
 */
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


}
