package com.finder.cache;

import com.finder.ForgeFinder;
import com.finder.cache.util.CacheState;
import com.finder.events.ChunkLoadEvent;
import java.util.*;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CacheManager {

  ChunkCachefier chunkCachefierThread = null;
  boolean isCaching;
  List<int[]> chunksNotCached = new ArrayList<>();
  private final Map<int[], CachedChunk> cachedChunks =
    Collections.synchronizedMap(new HashMap<>());

  private final HashSet<int[]> cachedChunksPositions = new HashSet<>();

  public CacheManager(boolean startCaching) {
    this.isCaching = startCaching;
  }

  @SubscribeEvent
  public void onChunkLoad(ChunkLoadEvent event) {
    if (!isCaching) {
      Chunk chunk = event.getChunk();
      chunksNotCached.add(new int[] { chunk.xPosition, chunk.zPosition });
      return;
    }

    Chunk chunk = event.getChunk();
    int[] chunkPosInt = new int[] { chunk.xPosition, chunk.zPosition };

    if (!chunksNotCached.isEmpty()) {
      for (int[] c : chunksNotCached) {
        Chunk notCachedChunk = ForgeFinder.MC.theWorld.getChunkFromChunkCoords(
          c[0],
          c[1]
        );
        addChunkToCache(notCachedChunk, c);
      }
    }

    addChunkToCache(chunk, chunkPosInt);
  }

  private void addChunkToCache(Chunk chunk, int[] chunkPosInt) {
    if (!cachedChunksPositions.contains(chunkPosInt)) {
      if (chunkCachefierThread == null || !chunkCachefierThread.isAlive()) {
        chunkCachefierThread = new ChunkCachefier(cachedChunks, chunk);
        chunkCachefierThread.start();
      } else {
        chunkCachefierThread.addChunkToCacheLater(chunk);
      }

      cachedChunksPositions.add(chunkPosInt);
    }
  }

  public CachedChunk getCachedChunk(int x, int z) {
    return cachedChunks.get(new int[] { x >> 4, z >> 4 });
  }

  public CacheState getBlockInfoCached(int xPos, int yPos, int zPos) {
    return getCachedChunk(xPos, zPos).isBlockSolidInChunk(xPos, yPos, zPos);
  }

  public boolean isBlockCached(BlockPos bp) {
    return cachedChunksPositions.contains(
      new int[] { bp.getX() >> 4, bp.getZ() >> 4 }
    );
  }
}
