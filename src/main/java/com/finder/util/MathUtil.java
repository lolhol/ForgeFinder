package com.finder.util;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class MathUtil {

  public static double subtractMathAbs(double one, double two) {
    return Math.abs(one) - Math.abs(two);
  }

  public static double absAbsSubtract(double one, double two) {
    return Math.abs(subtractMathAbs(one, two));
  }

  public static double distanceFromTo(Vec3 a, Vec3 b) {
    double distX = a.xCoord - b.xCoord;
    double distY = a.yCoord - b.yCoord;
    double distZ = a.zCoord - b.zCoord;

    return Math.sqrt(distX * distX + distY * distY + distZ * distZ);
  }

  public static double distanceFromTo(BlockPos a, BlockPos b) {
    double distX = a.getX() - b.getX();
    double distY = a.getY() - b.getY();
    double distZ = a.getZ() - b.getZ();

    return Math.sqrt(distX * distX + distY * distY + distZ * distZ);
  }

  public static double distanceFromTo(int x, int y, int z, BlockPos b) {
    double distX = x - b.getX();
    double distY = y - b.getY();
    double distZ = z - b.getZ();

    return Math.sqrt(distX * distX + distY * distY + distZ * distZ);
  }

  public static double distanceFromToXZ(BlockPos a, BlockPos b) {
    double distX = a.getX() - b.getX();
    double distZ = a.getZ() - b.getZ();

    return Math.sqrt(distX * distX + distZ * distZ);
  }

  public static double distanceFromToXZ(int x, int z, int x1, int z1) {
    double distX = x - x1;
    double distZ = z - z1;

    return Math.sqrt(distX * distX + distZ * distZ);
  }

  public static double distanceFromToXZ(int x, int z, BlockPos b) {
    double distX = x - b.getX();
    double distZ = z - b.getZ();

    return Math.sqrt(distX * distX + distZ * distZ);
  }

  public static double distanceFromToXZ(Vec3 a, Vec3 b) {
    double distX = a.xCoord - b.xCoord;
    double distZ = a.zCoord - b.zCoord;

    return Math.sqrt(distX * distX + distZ * distZ);
  }

  public static Vec3 getNormalVecBetweenVecsRev(Vec3 vec1, Vec3 vec2) {
    return vec2.subtract(vec1).normalize().rotateYaw(90);
  }

  public static int getPositionIndex(int x, int y, int z) {
    return (x << 1) | (z << 5) | (y << 9);
  }

  public static int getPositionChunk(int pos) {
    return pos >> 4;
  }
}
