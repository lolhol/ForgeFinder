package com.finder.cache;

import com.finder.cache.util.CacheState;
import com.finder.util.MathUtil;
import java.util.BitSet;

public class CachedChunk {

  private final BitSet[] blockData;
  private final int[] position;

  public CachedChunk(BitSet[] blockData, int[] position) {
    this.blockData = blockData;
    this.position = position;
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
