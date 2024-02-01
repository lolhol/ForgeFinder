package com.finder.util;

public class TimeUtil {

  public static double getTimeReqMS(double blocksPerSecond, double dist) {
    double blocksMs = (double) 1000 / blocksPerSecond;
    return dist * blocksMs;
  }
}
