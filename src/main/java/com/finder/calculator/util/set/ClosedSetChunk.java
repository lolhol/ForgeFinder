package com.finder.calculator.util.set;

import java.util.BitSet;
import net.minecraft.util.BlockPos;

public class ClosedSetChunk {

  private final BitSet[] chunkData = new BitSet[16 * 15];
  private int[] pos = null;

  public ClosedSetChunk(int[] chunkPos) {
    this.pos = chunkPos;
  }

  public ClosedSetChunk(int[] chunkPos, BlockPos initAdd) {
    this.pos = chunkPos;
    add(initAdd, true);
  }

  public boolean isPositionSet() {
    return pos != null;
  }

  public void add(BlockPos bpPos, boolean isClosed) {
    if (chunkData[bpPos.getY()] == null) {
      chunkData[bpPos.getY()] = new BitSet(16 * 16);
    }

    int chunkX = pos[0] - bpPos.getX();
    int chunkY = pos[1] - bpPos.getZ();

    chunkData[bpPos.getY()].set(chunkX * 16 + chunkY, isClosed);
  }

  public boolean isClosed(BlockPos bpPos) {
    if (chunkData[bpPos.getY()] == null) return false;

    int chunkX = pos[0] - bpPos.getX();
    int chunkY = pos[1] - bpPos.getZ();

    return chunkData[bpPos.getY()].get(chunkX * 16 + chunkY);
  }

  public boolean isClosed(BlockPos bpPos, int[] chunkPos) {
    if (chunkData[bpPos.getY()] == null) return false;

    int chunkX = pos[0] - bpPos.getX();
    int chunkY = pos[1] - bpPos.getZ();

    return chunkData[bpPos.getY()].get(chunkX * 16 + chunkY);
  }
}
