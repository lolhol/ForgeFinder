package com.finder.util;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class MathUtil extends TimeUtil {

  public double subtractMathAbs(double one, double two) {
    return Math.abs(one) - Math.abs(two);
  }

  public double absAbsSubtract(double one, double two) {
    return Math.abs(subtractMathAbs(one, two));
  }

  public double distanceFromTo(Vec3 a, Vec3 b) {
    double distX = a.xCoord - b.xCoord;
    double distY = a.yCoord - b.yCoord;
    double distZ = a.zCoord - b.zCoord;

    return Math.sqrt(distX * distX + distY * distY + distZ * distZ);
  }

  public double distanceFromTo(BlockPos a, BlockPos b) {
    double distX = a.getX() - b.getX();
    double distY = a.getY() - b.getY();
    double distZ = a.getZ() - b.getZ();

    return Math.sqrt(distX * distX + distY * distY + distZ * distZ);
  }

  public double distanceFromToXZ(BlockPos a, BlockPos b) {
    double distX = a.getX() - b.getX();
    double distZ = a.getZ() - b.getZ();

    return Math.sqrt(distX * distX + distZ * distZ);
  }

  public double distanceFromToXZ(Vec3 a, Vec3 b) {
    double distX = a.xCoord - b.xCoord;
    double distZ = a.zCoord - b.zCoord;

    return Math.sqrt(distX * distX + distZ * distZ);
  }

  public Vec3 getNormalVecBetweenVecsRev(Vec3 vec1, Vec3 vec2) {
    return vec2.subtract(vec1).normalize().rotateYaw(90);
  }
}
