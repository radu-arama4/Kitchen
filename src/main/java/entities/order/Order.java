package entities.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import util.KitchenContext;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class Order {
  private int id;
  private List<Integer> items;
  private boolean[] itemsDone;
  private boolean[] itemsUsed;
  private int priority;
  private float maxWait;
  private boolean ready;
  private float preparingTime;
  private Timestamp pickUpTime;
  private Timestamp servingTime;

  public Order(int id, List<Integer> items, int priority) {
    this.id = id;
    this.items = items;
    this.priority = priority;
  }

  public void initialize() {
    itemsDone = new boolean[items.size()];
    itemsUsed = new boolean[items.size()];
    for (Boolean item : itemsDone) {
      item = false;
    }
    for (Boolean item : itemsUsed) {
      item = false;
    }
  }

  public List<Food> getFoodsByItemId() {
    List<Food> foods = KitchenContext.getInstance().getFoods();
    List<Food> returnedFoods = new LinkedList<>();

    foods.forEach(
        food -> {
          if (items.contains(food.getId())) {
            returnedFoods.add(food);
          }
        });

    return returnedFoods;
  }

  @SneakyThrows
  public void setItemDone(Food item) {
    for (int itm : items) {
      if (itm == item.getId()) {
        itemsDone[items.indexOf(itm)] = true;
      }
    }
  }

  @SneakyThrows
  public Food getUndoneItem() {
    for (int item : items) {
      int indx = items.indexOf(item);
      if (!itemsDone[indx] && !itemsUsed[indx]) {
        itemsUsed[indx] = true;
        return KitchenContext.getInstance().findItemById(item);
      }
    }
    return null;
  }

  public int getMaxComplexity() {
    List<Food> foods = getFoodsByItemId();
    int maxComplexity = 0;
    for (Food food : foods) {
      if (food.getComplexity() > maxComplexity) {
        maxComplexity = food.getComplexity();
      }
    }
    return maxComplexity;
  }
}
