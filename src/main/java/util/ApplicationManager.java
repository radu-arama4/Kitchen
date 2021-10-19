package util;

import org.apache.catalina.LifecycleException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tomcat.TomcatManager;

public class ApplicationManager {
  private static final Logger log = LogManager.getLogger(ApplicationManager.class);
  private static TomcatManager tomcatManager = new TomcatManager();

  public static void startApplication() {
    Thread serverThread = new Thread(tomcatManager);
    Properties.readProperties();
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
