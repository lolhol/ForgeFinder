package com.finder.calculator.util.set;

import com.finder.calculator.util.BetterBlockPos;

public class ChunkPos {

  public final byte x;
  public final byte z;

  public ChunkPos(byte x, byte z) {
    this.x = x;
    this.z = z;
  }

  public ChunkPos(byte[] pos) {
    this.x = pos[0];
    this.z = pos[1];
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ChunkPos that = (ChunkPos) o;
    return x == that.x && z == that.z;
  }

  @Override
  public int hashCode() {
    int result = 17;
    result = 31 * result + x;
    result = 31 * result + z;
    return result;
  }
}
