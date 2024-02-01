package com.finder.calculator.util;

import net.minecraft.util.BlockPos;

public class BetterBlockPos {

  public final int x;
  public final int y;
  public final int z;

  public BetterBlockPos(int[] position) {
    this.x = position[0];
    this.y = position[1];
    this.z = position[2];
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BetterBlockPos that = (BetterBlockPos) o;
    return x == that.x && y == that.y && z == that.z;
  }

  @Override
  public int hashCode() {
    return (this.y + this.z * 31) * 31 + this.x;
  }
}
