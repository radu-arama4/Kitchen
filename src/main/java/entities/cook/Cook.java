package entities.cook;

import com.google.gson.Gson;
import entities.order.Food;
import entities.order.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import util.KitchenContext;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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
  public synchronized void run() {
    Order foundOrder;

    PriorityQueue<Order> orders = KitchenContext.getInstance().getOrderList();

    while (true) {
      synchronized (orders) {
        if (!orders.isEmpty()) {
          this.notifyAll();

          List<Order> result = new ArrayList<>(orders.size());
          while (!orders.isEmpty()) {
            result.add(orders.poll());
          }

          List<Order> syncOrders = Collections.synchronizedList(result);

          synchronized (syncOrders) {
            for (Order order : syncOrders) {
              if (!order.isReady()) {
                if (rank < order.getMaxComplexity()) {
                  continue;
                }
                semaphore.acquire();
                order.setReady(true);
                foundOrder = order;
                semaphore.release();

                cook(order);

                System.out.println("Sending back order with ID " + foundOrder.getId());
                sendReadyOrder(foundOrder);

                KitchenContext.getInstance().removeOrder(foundOrder);
              }
            }
          }
        }
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

  private synchronized void sendReadyOrder(Order order) {
    HttpURLConnection con = null;
    // host.docker.internal
    try {
      URL url = new URL("http://localhost:8080/home");
      con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("POST");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
    con.setRequestProperty("Content-Type", "application/json; utf-8");
    con.setRequestProperty("Accept", "application/json");

    con.setDoOutput(true);

    Gson gson = new Gson();

    String json = gson.toJson(order);

    try (OutputStream os = con.getOutputStream()) {
      byte[] input = json.getBytes("utf-8");
      os.write(input, 0, input.length);
    } catch (IOException e) {
      log.error(e.getMessage());
    }

    try (BufferedReader br =
        new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
      StringBuilder response = new StringBuilder();
      String responseLine = null;
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
      System.out.println(response.toString());
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
