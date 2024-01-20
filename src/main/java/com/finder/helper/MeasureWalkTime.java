package com.finder.helper;

import com.finder.ForgeFinder;
import com.finder.util.MathUtil;
import net.minecraft.util.Vec3;

public class MeasureWalkTime extends MathUtil {

  // WARNING! This works only if you walk IN A STRAIGHT LINE on a FLAT surface.

  private static Vec3 start, end;
  private static long startTimeMS, endTimeMS;

  public static void startMeasuring() {
    start = ForgeFinder.MC.thePlayer.getPositionVector();
    startTimeMS = System.currentTimeMillis();
  }

  public static void stopMeasuring() {
    endTimeMS = System.currentTimeMillis();
    end = ForgeFinder.MC.thePlayer.getPositionVector();
  }

  public static double getAvgBlocksPerSeconds() {
    double timeSec = (double) (endTimeMS - startTimeMS) / 1000.;
    double distance = start.distanceTo(end);
    return distance / timeSec;
  }
}
