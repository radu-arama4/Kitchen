import entities.Cook;
import util.ApplicationManager;
import util.KitchenContext;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    ApplicationManager.startApplication();

    KitchenContext kitchenContext = KitchenContext.getInstance();

    List<Cook> cooks = generateCooks();
    kitchenContext.setCooks(cooks);

    for (Cook cook:cooks){
      Thread thread = new Thread(cook);
      thread.start();
    }

    //    ApplicationManager.closeApplication();
  }

  private static List<Cook> generateCooks() {
    Cook gordon = new Cook(3, 3, "Gordon Romsay", "Hey, panini head, are you listening to me?");
    Cook jora = new Cook(2, 2, "Jora Jora", "Done!");
    Cook vadik = new Cook(1, 1, "Vadik Vadik", "Bruh!");
    Cook cook = new Cook(3, 4, "Cook", "Let me cook!");
    return new LinkedList<>(Arrays.asList(gordon, jora, vadik, cook));
  }
}
