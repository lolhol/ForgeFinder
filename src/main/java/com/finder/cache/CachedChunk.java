package com.finder.cache;

import com.finder.cache.util.CacheState;
import com.finder.util.MathUtil;
import java.util.BitSet;

public class CachedChunk {

  private BitSet[] blockData;
  private final int[] position;

  public CachedChunk(BitSet[] blockData, int[] xyz) {
    this.blockData = blockData;
    this.position = xyz;
  }

  public CacheState isBlockSolidInChunk(int xChunk, int yChunk, int zChunk) {
    int listPos = yChunk >> 4;
    if (blockData[listPos] == null) {
      return CacheState.NOEXISTANCE;
    }

    int position = MathUtil.getPositionIndex(xChunk, listPos, zChunk);
    return blockData[position].isEmpty()
      ? CacheState.EXISTS_NO
      : CacheState.EXISTS_YES;
  }
}
