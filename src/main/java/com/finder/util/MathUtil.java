package com.finder.util;

import com.finder.calculator.util.Node;
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

  public static double calculateAngle(Node point1, Node point2, Node point3) {
    double vec1x = point2.x - point1.x;
    double vec1y = point2.z - point1.z;
    double vec2x = point3.x - point2.x;
    double vec2y = point3.z - point2.z;

    double dotProduct = vec1x * vec2x + vec1y * vec2y;

    double magnitudeVec1 = Math.sqrt(vec1x * vec1x + vec1y * vec1y);
    double magnitudeVec2 = Math.sqrt(vec2x * vec2x + vec2y * vec2y);

    double angleRad = Math.acos(dotProduct / (magnitudeVec1 * magnitudeVec2));

    return Math.toDegrees(angleRad);
  }

  public static int getPositionIndex3DList(
    int x,
    int y,
    int z,
    int height,
    int width
  ) {
    return z * (width * height) + y * width + x;
  }

  public static int[] getCoordinatesFromPositionIndex(
    int positionIndex,
    int height,
    int width
  ) {
    int[] coordinates = new int[3];
    coordinates[2] = positionIndex / (width * height);
    int remainingPosition = positionIndex % (width * height);
    coordinates[1] = remainingPosition / width;
    coordinates[0] = remainingPosition % width;

    return coordinates;
  }
}
