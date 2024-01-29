package com.finder.cache;

import java.util.BitSet;

public class CachedChunk {

  private BitSet blockData;
  private final int[] position;

  public CachedChunk(BitSet blockData, int[] xyz) {
    this.blockData = blockData;
    this.position = xyz;
  }
  //public boolean getBlockInfo(int xChunk, int yChunk, int zChunk) {}
}
