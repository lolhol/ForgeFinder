package com.finder.cache;

import com.finder.cache.util.CacheState;
import com.finder.calculator.util.BetterBlockPos;
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

    // FIXME: error!
    int position = MathUtil.getPositionIndex3DList(
      xChunk - this.position[0],
      Math.abs(listPos << 4 - yChunk),
      zChunk - this.position[1],
      16,
      16
    );

    //RenderUtil.addBlockToRenderSync(new BlockPos(xChunk, yChunk, zChunk));

    //ChatUtil.sendChat(String.valueOf(zChunk - this.position[1]) + " !");

    return blockData[listPos].get(position)
      ? CacheState.EXISTS_YES
      : CacheState.EXISTS_NO;
  }

  public boolean getBlockStateChunk(BetterBlockPos blockPosition) {
    int positionChunkX = position[0] - blockPosition.x;
    int positionChunkY = blockPosition.y >> 4;
    int positionChunkZ = position[1] - blockPosition.z;

    return blockData[positionChunkY].get(
        MathUtil.getPositionIndex3DList(
          positionChunkX,
          Math.abs(positionChunkY << 4 - blockPosition.y),
          positionChunkZ,
          16,
          16
        )
      );
  }
}
