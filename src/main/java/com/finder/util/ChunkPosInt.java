package com.finder.util;

public class ChunkPosInt {

  public final int x, y;

  public ChunkPosInt(int x1, int y1) {
    this.x = x1;
    this.y = y1;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChunkPosInt that = (ChunkPosInt) o;
    return x == that.x && y == that.y;
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + x;
    result = 31 * result + y;
    return result;
  }
}
