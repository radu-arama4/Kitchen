package entities;

import com.google.gson.Gson;
import entities.order.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import util.KitchenContext;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Getter
@Setter
@Slf4j
public class Cook implements Runnable {
  private int rank;
  private int proficiency;
  private String name;
  private String catchPhrase;

  @Override
  public void run() {
    PriorityQueue<Order> orders = KitchenContext.getInstance().getOrderList();

    while (true) {
      synchronized (orders) {
        for (Order order : orders) {
          if (!order.isReady()) {
            order.setReady(true);
            try {
              TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
              log.error(e.getMessage());
            }
            System.out.println("Sending back order with ID " + order.getId());
            sendReadyOrder(order);
          }
        }
      }
    }
  }

  private void sendReadyOrder(Order order) {
    // host.docker.internal
    HttpURLConnection con = null;
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
