package util;

import entities.Cook;
import entities.order.Order;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class KitchenContext {
  private static KitchenContext instance;

  private List<Cook> cooks = new LinkedList<>();
  private PriorityQueue<Order> orderList = new PriorityQueue<>(20, new OrderComparator());

  private KitchenContext() {}

  public static KitchenContext getInstance() {
    if (instance == null) {
      instance = new KitchenContext();
    }
    return instance;
  }

  public void addOrder(Order order){
    orderList.add(order);
  }

  private class OrderComparator implements Comparator<Order> {
    @Override
    public int compare(Order o1, Order o2) {
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
}
