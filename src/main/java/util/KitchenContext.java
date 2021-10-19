package util;

import entities.cook.Cook;
import entities.cooking.apparatus.Apparatus;
import entities.cooking.apparatus.CookingApparatus;
import entities.cooking.apparatus.Oven;
import entities.cooking.apparatus.Stove;
import entities.order.Food;
import entities.order.Order;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class KitchenContext {
  private static KitchenContext instance;

  private List<Food> foods = new LinkedList<>();

  private List<Cook> cooks = new LinkedList<>();

  private volatile PriorityBlockingQueue<Order> orderList =
      new PriorityBlockingQueue<>(20, new OrderComparator());

  private List<Apparatus> availableApparatus;

  private static int nr = 0;

  private KitchenContext() {}

  public static KitchenContext getInstance() {
    if (instance == null) {
      instance = new KitchenContext();
    }
    return instance;
  }

  public synchronized void addOrder(Order order) {
    orderList.add(order);
  }

  public synchronized void removeOrder(Order order) {
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

  public Food findItemById(int id) {
    for (Food food : foods) {
      if (food.getId() == id) {
        return food;
      }
    }
    return null;
  }

  public Apparatus findFreeApparatus(CookingApparatus apparatus) {
    Apparatus foundApparatus = null;
    synchronized (availableApparatus) {
      switch (apparatus) {
        case OVEN:
          {
            while (foundApparatus == null) {
              for (Apparatus apparat : availableApparatus) {
                if (apparat.isAvailable() && apparat instanceof Oven) {
                  foundApparatus = apparat;
                }
              }
            }
            break;
          }
        case STOVE:
          {
            while (foundApparatus == null) {
              for (Apparatus apparat : availableApparatus) {
                if (apparat.isAvailable() && apparat instanceof Stove) {
                  foundApparatus = apparat;
                }
              }
            }
            break;
          }
      }
    }
    return foundApparatus;
  }

  public PriorityBlockingQueue<Order> getOrderList() {
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

  public void setAvailableApparatus(List<Apparatus> availableApparatus) {
    this.availableApparatus = availableApparatus;
  }

  public List<Apparatus> getAvailableApparatus() {
    return availableApparatus;
  }
}
