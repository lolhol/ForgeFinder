package com.finder.calculator.util.set;

import com.finder.calculator.util.BetterBlockPos;
import com.finder.calculator.util.Node;
import java.util.BitSet;
import net.minecraft.util.BlockPos;

public class SetChunk {

  private final BitSet[] chunkData = new BitSet[256];
  private final int[] pos;
  private static final double c = 16;

  public SetChunk(int[] chunkPos) {
    this.pos = chunkPos;
  }

  public SetChunk(int[] chunkPos, BlockPos initAdd) {
    this.pos = chunkPos;
    addClosed(initAdd, true);
  }

  public SetChunk(int[] chunkPosBP, Node initAdd) {
    this.pos = chunkPosBP;
    addClosed(initAdd, true);
  }

  public boolean isPositionSet() {
    return pos != null;
  }

  public int getChunkPos(int x, int y) {
    return (int) (x * c + y);
  }

  public void addToChunkOpen(int posY, int pos, boolean isOpen) {
    chunkData[posY].set(511 - pos, isOpen);
  }

  public void addToChunk(int posY, int pos, boolean isClosed) {
    chunkData[posY].set(pos, isClosed);
    chunkData[posY].set(511 - pos, false);
  }

  public void addClosed(BlockPos bpPos, boolean isClosed) {
    if (chunkData[bpPos.getY()] == null) {
      chunkData[bpPos.getY()] = new BitSet(512);
    }

    int chunkX = bpPos.getX() - pos[0];
    int chunkY = bpPos.getZ() - pos[1];

    addToChunk(bpPos.getY(), getChunkPos(chunkX, chunkY), isClosed);
  }

  public void addClosed(int[] bpPos, boolean isClosed) {
    if (chunkData[bpPos[1]] == null) {
      chunkData[bpPos[1]] = new BitSet(16 * 16);
    }

    int chunkX = bpPos[0] - pos[0];
    int chunkY = bpPos[2] - pos[1];

    addToChunk(bpPos[1], getChunkPos(chunkX, chunkY), isClosed);
  }

  public void addClosed(Node bpPos, boolean isClosed) {
    if (chunkData[bpPos.y] == null) {
      chunkData[bpPos.y] = new BitSet(16 * 16);
    }

    int chunkX = bpPos.x - pos[0];
    int chunkY = bpPos.z - pos[1];

    addToChunk(bpPos.y, getChunkPos(chunkX, chunkY), isClosed);
  }

  public void addOpen(Node bpPos, boolean isOpen) {
    if (chunkData[bpPos.y] == null) {
      chunkData[bpPos.y] = new BitSet(16 * 16);
    }

    int chunkX = bpPos.x - pos[0];
    int chunkY = bpPos.z - pos[1];

    addToChunkOpen(bpPos.y, getChunkPos(chunkX, chunkY), isOpen);
  }

  public void addOpen(BetterBlockPos bpPos, boolean isOpen) {
    if (chunkData[bpPos.y] == null) {
      chunkData[bpPos.y] = new BitSet(16 * 16);
    }

    int chunkX = bpPos.x - pos[0];
    int chunkY = bpPos.z - pos[1];

    addToChunkOpen(bpPos.y, getChunkPos(chunkX, chunkY), isOpen);
  }

  public boolean isOpen(BetterBlockPos bpPos) {
    if (chunkData[bpPos.y] == null) return false;

    int chunkX = bpPos.x - pos[0];
    int chunkY = bpPos.z - pos[1];

    return chunkData[bpPos.y].get(511 - getChunkPos(chunkX, chunkY));
  }

  public boolean isClosed(BlockPos bpPos) {
    if (chunkData[bpPos.getY()] == null) return false;

    int chunkX = bpPos.getX() - pos[0];
    int chunkY = bpPos.getZ() - pos[1];

    return chunkData[bpPos.getY()].get(getChunkPos(chunkX, chunkY));
  }

  public boolean isClosed(BetterBlockPos bpPos) {
    if (chunkData[bpPos.y] == null) return false;

    int chunkX = bpPos.x - pos[0];
    int chunkY = bpPos.z - pos[1];

    return chunkData[bpPos.y].get(getChunkPos(chunkX, chunkY));
  }

  public boolean isClosed(BlockPos bpPos, int[] chunkPos) {
    if (chunkData[bpPos.getY()] == null) return false;

    int chunkX = bpPos.getX() - pos[0];
    int chunkY = bpPos.getZ() - pos[1];

    return chunkData[bpPos.getY()].get(getChunkPos(chunkX, chunkY));
  }
}
