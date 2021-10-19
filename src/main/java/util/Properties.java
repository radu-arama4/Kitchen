package util;

import lombok.SneakyThrows;

import java.io.InputStream;

public class Properties {
  public static int TIME_UNIT;
  public static int NR_OF_COOKS;
  public static int NR_OF_STOVE;
  public static int NR_OF_OVEN;

  @SneakyThrows
  public static void readProperties() {
    InputStream s = Properties.class.getResourceAsStream("/application.properties");

    java.util.Properties props = new java.util.Properties();
    props.load(s);

    TIME_UNIT = Integer.parseInt(props.getProperty("time_unit"));
    NR_OF_COOKS = Integer.parseInt(props.getProperty("nr_of_cooks"));
    NR_OF_STOVE = Integer.parseInt(props.getProperty("nr_of_stove"));
    NR_OF_OVEN = Integer.parseInt(props.getProperty("nr_of_oven"));
  }
}
