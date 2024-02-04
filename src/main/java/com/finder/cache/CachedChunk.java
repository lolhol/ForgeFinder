package com.finder.cache;

import com.finder.cache.util.CacheState;
import com.finder.calculator.util.BetterBlockPos;
import com.finder.debug.util.RenderUtil;
import com.finder.util.ChatUtil;
import com.finder.util.MathUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

public class CachedChunk {

  private final BitSet[] blockData;
  private final Int2ObjectOpenHashMap<String> special;
  private final int[] position;

  public CachedChunk(
    BitSet[] blockData,
    int[] position,
    Map<String, List<BetterBlockPos>> keepTrackOfBlockLocations
  ) {
    this.blockData = blockData;
    this.position = position;

    if (!keepTrackOfBlockLocations.isEmpty()) {
      special = new Int2ObjectOpenHashMap<>();
      for (Map.Entry<String, List<BetterBlockPos>> entry : keepTrackOfBlockLocations.entrySet()) {
        for (BetterBlockPos bp : entry.getValue()) {
          special.put(
            MathUtil.getPositionIndex3DList(bp.x, bp.y, bp.z, 256, 16),
            entry.getKey()
          );
        }
      }
    } else {
      special = null;
    }
  }

  public CacheState isBlockSolidInChunk(int xChunk, int yChunk, int zChunk) {
    int listPos = yChunk >> 4;
    if (blockData[listPos] == null) {
      return CacheState.NOEXISTANCE;
    }

    int chunkX = xChunk - this.position[0];
    int chunkZ = zChunk - this.position[1];

    int position = MathUtil.getPositionIndex3DList(
      chunkX,
      (yChunk) - (listPos << 4),
      chunkZ,
      16,
      16
    );

    if (special != null) {
      if (
        special.get(
          MathUtil.getPositionIndex3DList(chunkX, yChunk, chunkZ, 256, 16)
        ) !=
        null
      ) {
        return CacheState.NOT_SOLID_NOT_AIR;
      }
    }

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
