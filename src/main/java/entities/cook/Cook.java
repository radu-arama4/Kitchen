package entities.cook;

import entities.cooking.apparatus.Apparatus;
import entities.cooking.apparatus.CookingApparatus;
import entities.order.Food;
import entities.order.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import util.KitchenContext;
import util.Properties;

import java.util.List;
import java.util.concurrent.*;

import static tomcat.Request.sendReadyOrderToDinningHall;

@Getter
@Setter
public class Cook implements Runnable {
  private int rank;
  private int proficiency;
  private String name;
  private String catchPhrase;
  private static final Logger log = LogManager.getLogger(Cook.class);

  public Cook(int rank, int proficiency, String name, String catchPhrase, Semaphore semaphore) {
    this.rank = rank;
    this.proficiency = proficiency;
    this.name = name;
    this.catchPhrase = catchPhrase;
    this.semaphore = semaphore;
  }

  private Semaphore semaphore;

  @SneakyThrows
  @Override
  @SuppressWarnings("InfiniteLoopStatement")
  public void run() {
    Order foundOrder;

    PriorityBlockingQueue<Order> orders = KitchenContext.getInstance().getOrderList();

    while (true) {
      Order order = orders.take();

      if (!order.isReady() && rank < order.getMaxComplexity()) {
        continue;
      }

      order.setReady(true);

      foundOrder = order;

      cook(order);

      log.info("Sending back order with ID " + foundOrder.getId());
      sendReadyOrderToDinningHall(foundOrder);

      KitchenContext.getInstance().removeOrder(foundOrder);
    }
  }

  private synchronized void cook(Order order) throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(proficiency);

    List<Food> foods = order.getFoodsByItemId();

    Apparatus apparatus = null;

    Semaphore semaphore = new Semaphore(1);

    for (Food food : foods) {
      if (food.getCookingApparatus() != null && food.getCookingApparatus().equals("stove")) {
//        synchronized (KitchenContext.getInstance().getAvailableApparatus()) {

          apparatus = KitchenContext.getInstance().findFreeApparatus(CookingApparatus.STOVE);
          semaphore.acquire();
          apparatus.use();
//        }
      } else if (food.getCookingApparatus() != null && food.getCookingApparatus().equals("oven")) {
//        synchronized (KitchenContext.getInstance().getAvailableApparatus()) {

          apparatus = KitchenContext.getInstance().findFreeApparatus(CookingApparatus.OVEN);
          semaphore.acquire();
          apparatus.use();
//        }
      }
      executorService.submit(
          () -> {
            try {
              TimeUnit.MILLISECONDS.sleep((long) food.getPreparationTime() * Properties.TIME_UNIT);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          });

      if (apparatus != null) {
        apparatus.free();
        semaphore.release();
      }
    }

    log.info(catchPhrase);
  }
}
