package entities.cooking.apparatus;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Oven implements Apparatus {
  private static final Logger log = LogManager.getLogger(Oven.class);

  private boolean available;

  public Oven() {
    this.available = true;
  }

  @Override
  public void use() {
    available = false;
    log.info("Oven is being used...");
  }

  @Override
  public boolean isAvailable() {
    return available;
  }

  @Override
  public void free() {
    available = true;
  }
}
