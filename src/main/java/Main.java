import entities.cook.Cook;
import entities.cook.Cook2;
import entities.cooking.apparatus.Apparatus;
import entities.cooking.apparatus.Oven;
import entities.cooking.apparatus.Stove;
import entities.order.Food;
import util.ApplicationManager;
import util.KitchenContext;
import util.Properties;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Main {
  static Semaphore semaphore = new Semaphore(1);

  public static void main(String[] args) {
    ApplicationManager.startApplication();

    KitchenContext kitchenContext = KitchenContext.getInstance();

    List<Cook> cooks = generateCooks(Properties.NR_OF_COOKS);

//    kitchenContext.setCooks(cooks);
    kitchenContext.setFoods(generateFood());
    kitchenContext.setAvailableApparatus(generateApparatus(Properties.NR_OF_STOVE, Properties.NR_OF_OVEN));

//    for (Cook2 cook : cooks) {
//      Thread thread = new Thread(cook);
//      thread.start();
//    }

    for (Cook cook : cooks) {
      Thread thread = new Thread(cook);
      thread.start();
    }

    // ApplicationManager.closeApplication();
  }

  private static List<Cook> generateCooks(int nrOfCooks) {
    Cook gordon =
        new Cook(3, 3, "Gordon Romsay", "Hey, panini head, are you listening to me?", semaphore);
    Cook jora = new Cook(2, 2, "Jora Jora", "Done!", semaphore);
    Cook vadik = new Cook(1, 1, "Vadik Vadik", "Bruh!", semaphore);
    Cook cook = new Cook(3, 4, "Cook", "Let me cook!", semaphore);
    return new LinkedList<>(Arrays.asList(gordon, jora, vadik, cook));
  }

  private static List<Cook2> generateCooks2(int nrOfCooks) {
    Cook2 gordon =
            new Cook2(3, 3, "Gordon Romsay", "Hey, panini head, are you listening to me?", semaphore);
    Cook2 jora = new Cook2(2, 2, "Jora Jora", "Done!", semaphore);
    Cook2 vadik = new Cook2(1, 1, "Vadik Vadik", "Bruh!", semaphore);
    Cook2 cook = new Cook2(3, 4, "Cook", "Let me cook!", semaphore);
    return new LinkedList<>(Arrays.asList(gordon, jora, vadik, cook));
  }

  public static List<Food> generateFood() {
    Food pizza = new Food(1, "pizza", 20, 2, "oven");
    Food salad = new Food(2, "salad", 10, 1, null);
    Food zeama = new Food(3, "zeama", 7, 1, "stove");
    Food scallopSashimiWithMeyerLemonConfit =
        new Food(4, "Scallop Sashimi with Meyer Lemon Confit", 32, 3, null);
    Food islandDuckWithMulberryMustard =
        new Food(5, "Island Duck with Mulberry Mustard", 35, 3, "oven");
    Food waffles = new Food(6, "Waffles", 10, 1, "stove");
    Food aubergine = new Food(7, "Aubergine", 20, 2, null);
    Food lasagna = new Food(8, "Lasagna", 30, 2, "oven");
    Food burger = new Food(9, "Burger", 15, 1, "oven");
    Food gyros = new Food(10, "Gyros", 15, 1, null);

    return new LinkedList<>(
        Arrays.asList(
            pizza,
            salad,
            zeama,
            scallopSashimiWithMeyerLemonConfit,
            islandDuckWithMulberryMustard,
            waffles,
            aubergine,
            lasagna,
            burger,
            gyros));
  }

  public static List<Apparatus> generateApparatus(int nrOfStoves, int nrOfOvens) {
    List<Apparatus> generatedApparatus = new LinkedList<>();

    for (int i = 0; i < nrOfOvens; i++) {
      generatedApparatus.add(new Oven());
    }

    for (int i = 0; i < nrOfStoves; i++) {
      generatedApparatus.add(new Stove());
    }

    return generatedApparatus;
  }
}
