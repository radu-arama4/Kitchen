package entities.cooking.apparatus;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Stove implements Apparatus {
  private static final Logger log = LogManager.getLogger(Stove.class);

  private boolean available;

  public Stove() {
    this.available=true;
  }

  @Override
  public void use() {
    available = false;
    log.info("Stove is being used...");
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
