package util;

import entities.cook.Cook;
import entities.order.Food;
import entities.order.Order;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class KitchenContext {
  private static KitchenContext instance;

  private List<Food> foods = new LinkedList<>();

  private List<Cook> cooks = new LinkedList<>();
  private volatile PriorityQueue<Order> orderList = new PriorityQueue<>(20, new OrderComparator());

  private static int nr = 0;

  private KitchenContext() {}

  public static KitchenContext getInstance() {
    if (instance == null) {
      instance = new KitchenContext();
    }
    return instance;
  }

  public synchronized void addOrder(Order order){
    orderList.add(order);
  }

  public synchronized void removeOrder(Order order){
    orderList.remove(order);
  }

  private class OrderComparator implements Comparator<Order> {
    @Override
    public synchronized int compare(Order o1, Order o2) {
      if (o1.getPriority() > o2.getPriority()) {
        return 1;
      } else if (o2.getPriority() > o1.getPriority()) {
        return -1;
      }
      return 0;
    }
  }

  public PriorityQueue<Order> getOrderList() {
    return orderList;
  }

  public List<Cook> getCooks() {
    return cooks;
  }

  public void setCooks(List<Cook> cooks) {
    this.cooks = cooks;
  }

  public List<Food> getFoods() {
    return foods;
  }

  public void setFoods(List<Food> foods) {
    this.foods = foods;
  }
}
