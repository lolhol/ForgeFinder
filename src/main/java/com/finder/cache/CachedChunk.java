package com.finder.cache;

import com.finder.cache.util.CacheState;
import com.finder.calculator.util.BetterBlockPos;
import com.finder.util.ChunkPosInt;
import com.finder.util.MathUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

public class CachedChunk {

  private final BitSet blockDataFlat;
  private final Int2ObjectOpenHashMap<String> special;
  private final int[] position;

  public CachedChunk(
    BitSet[] blockData,
    int[] position,
    Map<String, List<BetterBlockPos>> keepTrackOfBlockLocations
  ) {
    this.blockDataFlat = new BitSet(256 * 16 * 16);

    for (int i = 0; i < blockData.length; i++) {
      BitSet bits = blockData[i];
      if (bits == null) {
        continue;
      }

      int realChunkPosY = i << 4;
      for (int j = 0; j < bits.length(); j++) {
        int[] posC = MathUtil.getCoordinatesFromPositionIndex(j, 16, 16);
        blockDataFlat.set(
          MathUtil.getPositionIndex3DList(
            posC[0],
            posC[1] + realChunkPosY,
            posC[2],
            256,
            16
          ),
          bits.get(j)
        );
      }
    }

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

  public BitSet getBlockDataFlat() {
    return blockDataFlat;
  }

  public Int2ObjectOpenHashMap<String> getSpecial() {
    return special;
  }

  public ChunkPosInt getChunkPosition() {
    return new ChunkPosInt(position[0], position[1]);
  }

  public CacheState isBlockSolidInChunk(int xChunk, int yChunk, int zChunk) {
    int chunkX = xChunk - this.position[0];
    int chunkZ = zChunk - this.position[1];

    int position = MathUtil.getPositionIndex3DList(
      chunkX,
      yChunk,
      chunkZ,
      256,
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

    return blockDataFlat.get(position)
      ? CacheState.OBSTRUCTED
      : CacheState.UNOBSTRUCTED;
  }

  public boolean getBlockStateChunk(BetterBlockPos blockPosition) {
    int positionChunkX = position[0] - blockPosition.x;
    int positionChunkZ = position[1] - blockPosition.z;

    return blockDataFlat.get(
      MathUtil.getPositionIndex3DList(
        positionChunkX,
        blockPosition.y,
        positionChunkZ,
        256,
        16
      )
    );
  }

  public void setBlockState(BetterBlockPos blockPosChunk, boolean state) {
    blockDataFlat.set(
      MathUtil.getPositionIndex3DList(
        blockPosChunk.x - this.position[0],
        blockPosChunk.y,
        blockPosChunk.z - this.position[1],
        256,
        16
      ),
      state
    );
  }
}
