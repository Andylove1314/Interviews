package com.andylove.interview.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * 命令模式
 */
public class OrderPattern {

    /**
     * 命令
     */
    public interface Order {
        public void excute();
    }

    /**
     * 请求类
     */
    public static class Stock {

        private String name = "ABC";
        private int quantity = 10;

        public void buy() {
            System.out.println("buy:" + name + quantity);
        }

        public void sell() {
            System.out.println("sell:" + name + quantity);
        }
    }

    public static class Buy implements Order {
        private Stock abcStock;

        public Buy(Stock abcStock){
            this.abcStock = abcStock;
        }
        @Override
        public void excute() {
            abcStock.buy();
        }
    }

    public static class Sell implements Order {
        private Stock abcStock;

        public Sell(Stock abcStock){
            this.abcStock = abcStock;
        }
        @Override
        public void excute() {
            abcStock.sell();
        }
    }

    /**
     * 命令调用者
     */
    public static class Broker {
        private List<Order> orderList = new ArrayList<Order>();

        public void takeOrder(Order order) {
            orderList.add(order);
        }

        public void excOrders() {
            for (Order order : orderList) {
                order.excute();
            }
            orderList.clear();
        }
    }

    public static void main(String[] args) {
        //创建命令任务
        Stock stock = new Stock();
        Buy buyStockOrder = new Buy(stock);
        Sell sellStockOrder = new Sell(stock);

        //执行命令
        Broker broker = new Broker();
        broker.takeOrder(buyStockOrder);
        broker.takeOrder(sellStockOrder);
        broker.excOrders();
    }

}
