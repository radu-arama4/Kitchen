package entities.cook;

import entities.cooking.apparatus.Apparatus;
import entities.cooking.apparatus.CookingApparatus;
import entities.order.Food;
import entities.order.Order;
import lombok.SneakyThrows;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import util.KitchenContext;
import util.Properties;

import java.util.concurrent.*;

import static tomcat.Request.sendReadyOrderToDinningHall;

public class Cook2 implements Runnable {
  private int rank;
  private int proficiency;
  private String name;
  private String catchPhrase;
  private static final Logger log = LogManager.getLogger(Cook.class);

  public Cook2(int rank, int proficiency, String name, String catchPhrase, Semaphore semaphore) {
    this.rank = rank;
    this.proficiency = proficiency;
    this.name = name;
    this.catchPhrase = catchPhrase;
    this.semaphore = semaphore;
  }

  private Semaphore semaphore;

  @SneakyThrows
  @Override
  public void run() {
    Order foundOrder;

    PriorityBlockingQueue<Order> orders = KitchenContext.getInstance().getOrderList();

    while (true) {
      Order order = orders.take();

      semaphore.acquire();
      Food food = order.getUndoneItem();
      semaphore.release();

      ExecutorService executorService = Executors.newFixedThreadPool(proficiency);

      while (food != null) {
        Food finalFood = food;
        executorService.execute(() -> cook(finalFood));
        semaphore.acquire();
        order.setItemDone(food);
        semaphore.release();
        food = order.getUndoneItem();
      }

      foundOrder = order;

      Order finalFoundOrder = foundOrder;
      Thread thread = new Thread(() -> sendReadyOrderToDinningHall(finalFoundOrder));
      thread.start();

      KitchenContext.getInstance().removeOrder(foundOrder);

      log.info(catchPhrase);
    }
  }

  @SneakyThrows
  private void cook(Food food) {
    Apparatus apparatus = null;

    synchronized (KitchenContext.getInstance()) {
      if (food.getCookingApparatus() != null && food.getCookingApparatus().equals("stove")) {
        synchronized (KitchenContext.getInstance().getAvailableApparatus()) {
          apparatus = KitchenContext.getInstance().findFreeApparatus(CookingApparatus.STOVE);
          apparatus.use();
        }
      } else if (food.getCookingApparatus() != null && food.getCookingApparatus().equals("oven")) {
        synchronized (KitchenContext.getInstance().getAvailableApparatus()) {
          apparatus = KitchenContext.getInstance().findFreeApparatus(CookingApparatus.OVEN);
          apparatus.use();
        }
      }
    }
    TimeUnit.MILLISECONDS.sleep((long) food.getPreparationTime() * Properties.TIME_UNIT);

    if (apparatus != null) {
      apparatus.free();
    }
  }
}
