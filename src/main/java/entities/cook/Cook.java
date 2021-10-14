package entities.cook;

import entities.order.Food;
import entities.order.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import util.KitchenContext;

import java.util.List;
import java.util.concurrent.*;

import static tomcat.Request.sendReadyOrderToDinningHall;

@Getter
@Setter
@Slf4j
public class Cook implements Runnable {
  private int rank;
  private int proficiency;
  private String name;
  private String catchPhrase;

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
      synchronized (orders) {
        Order order = orders.take();

        if (!order.isReady() && rank < order.getMaxComplexity()) {
          continue;
        }

        semaphore.acquire();
        order.setReady(true);
        semaphore.release();

        foundOrder = order;

        cook(order);

        System.out.println("Sending back order with ID " + foundOrder.getId());
        sendReadyOrderToDinningHall(foundOrder);

        KitchenContext.getInstance().removeOrder(foundOrder);
      }
    }
  }

  private synchronized void cook(Order order) throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(proficiency);

    List<Food> foods = order.getFoodsByItemId();
    for (Food food : foods) {

      executorService.submit(
          () -> {
            try {
              TimeUnit.MILLISECONDS.sleep((long) food.getPreparationTime());
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          });
    }

    executorService.shutdown();
    executorService.awaitTermination((long) order.getMaxWait(), TimeUnit.MILLISECONDS);

    System.out.println(catchPhrase);
  }
}
