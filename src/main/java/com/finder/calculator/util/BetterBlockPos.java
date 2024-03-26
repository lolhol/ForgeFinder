package com.finder.calculator.util;

import net.minecraft.util.BlockPos;

/**
 * @apiNote this is a BlockPos basicvaly without all of the other shit that the normal BP gives
 */
public class BetterBlockPos {

  public final int x;
  public final int y;
  public final int z;

  public BetterBlockPos(int[] position) {
    this.x = position[0];
    this.y = position[1];
    this.z = position[2];
  }

  public BetterBlockPos(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public BetterBlockPos(BlockPos blockPos) {
    this.x = blockPos.getX();
    this.y = blockPos.getY();
    this.z = blockPos.getZ();
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
