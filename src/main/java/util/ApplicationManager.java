package util;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleException;
import tomcat.TomcatManager;

@Slf4j
public class ApplicationManager {
  private static TomcatManager tomcatManager = new TomcatManager();

  public static void startApplication() {
    Thread serverThread = new Thread(tomcatManager);
    serverThread.start();
  }

  public static void closeApplication() {
    try {
      tomcatManager.stopServer();
    } catch (LifecycleException e) {
      log.error(e.getMessage());
    }
  }
}
