package com.finder.calculator.util.set;

import com.finder.calculator.util.BetterBlockPos;
import com.finder.calculator.util.Node;
import com.finder.util.ChatUtil;
import java.util.BitSet;
import net.minecraft.util.BlockPos;

public class ClosedSetChunk {

  private final BitSet[] chunkData = new BitSet[16 * 16];
  private final int[] pos;

  public ClosedSetChunk(int[] chunkPos) {
    this.pos = chunkPos;
  }

  public ClosedSetChunk(int[] chunkPos, BlockPos initAdd) {
    this.pos = chunkPos;
    add(initAdd, true);
  }

  public ClosedSetChunk(int[] chunkPosBP, Node initAdd) {
    this.pos = chunkPosBP;
    add(initAdd, true);
  }

  public boolean isPositionSet() {
    return pos != null;
  }

  public void add(BlockPos bpPos, boolean isClosed) {
    if (chunkData[bpPos.getY()] == null) {
      chunkData[bpPos.getY()] = new BitSet(16 * 16);
    }

    int chunkX = bpPos.getX() - pos[0];
    int chunkY = bpPos.getZ() - pos[1];

    chunkData[bpPos.getY()].set(chunkX * 16 + chunkY, isClosed);
  }

  public void add(int[] bpPos, boolean isClosed) {
    if (chunkData[bpPos[1]] == null) {
      chunkData[bpPos[1]] = new BitSet(16 * 16);
    }

    int chunkX = bpPos[0] - pos[0];
    int chunkY = bpPos[2] - pos[1];

    chunkData[bpPos[1]].set(chunkX * 16 + chunkY, isClosed);
  }

  public void add(Node bpPos, boolean isClosed) {
    if (chunkData[bpPos.y] == null) {
      chunkData[bpPos.y] = new BitSet(16 * 16);
    }

    int chunkX = bpPos.x - pos[0];
    int chunkY = bpPos.z - pos[1];

    chunkData[bpPos.y].set(chunkX * 16 + chunkY, isClosed);
  }

  public boolean isClosed(BlockPos bpPos) {
    if (chunkData[bpPos.getY()] == null) return false;

    int chunkX = bpPos.getX() - pos[0];
    int chunkY = bpPos.getZ() - pos[1];

    return chunkData[bpPos.getY()].get(chunkX * 16 + chunkY);
  }

  public boolean isClosed(BetterBlockPos bpPos) {
    if (chunkData[bpPos.y] == null) return false;

    int chunkX = bpPos.x - pos[0];
    int chunkY = bpPos.z - pos[1];

    return chunkData[bpPos.y].get(chunkX * 16 + chunkY);
  }

  public boolean isClosed(BlockPos bpPos, int[] chunkPos) {
    if (chunkData[bpPos.getY()] == null) return false;

    int chunkX = bpPos.getX() - pos[0];
    int chunkY = bpPos.getZ() - pos[1];

    return chunkData[bpPos.getY()].get(chunkX * 16 + chunkY);
  }
}
