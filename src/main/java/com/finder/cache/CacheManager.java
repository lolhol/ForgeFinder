package com.finder.cache;

import com.finder.ForgeFinder;
import com.finder.cache.util.CacheState;
import com.finder.calculator.util.BetterBlockPos;
import com.finder.events.BlockChangeEvent;
import com.finder.events.ChunkLoadEvent;
import com.finder.util.ChunkPosInt;
import java.util.*;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// TODO: re-write @this
public class CacheManager {

  public boolean isCaching;
  public int totalChunks = 0;
  ChunkCachefier chunkCachefierThread = null;
  List<int[]> chunksNotCached = new ArrayList<>();
  private final Map<ChunkPosInt, CachedChunk> cachedChunks =
    Collections.synchronizedMap(new HashMap<>());

  private final HashSet<ChunkPosInt> cachedChunksPositions = new HashSet<>();

  public CacheManager(boolean startCaching) {
    this.isCaching = startCaching;
  }

  @SubscribeEvent
  public void onBlockChangeEvent(BlockChangeEvent event) {
    if (
      chunkCachefierThread != null &&
      isBlockCached(event.pos) &&
      event.old != event.update
    ) {
      chunkCachefierThread.addBlockToCacheLater(new BetterBlockPos(event.pos));
    }
  }

  @SubscribeEvent
  public void onChunkLoad(ChunkLoadEvent event) {
    final Chunk chunk = event.getChunk();
    if (!isCaching) {
      chunksNotCached.add(new int[] { chunk.xPosition, chunk.zPosition });
      return;
    }

    //ChatUtil.sendChat(String.valueOf(cachedChunks.size()));

    ChunkPosInt chunkPosInt = new ChunkPosInt(chunk.xPosition, chunk.zPosition);

    if (!chunksNotCached.isEmpty()) {
      for (int[] c : chunksNotCached) {
        Chunk notCachedChunk = ForgeFinder.MC.theWorld.getChunkFromChunkCoords(
          c[0],
          c[1]
        );
        addChunkToCache(notCachedChunk, chunkPosInt);
      }
    }

    addChunkToCache(chunk, chunkPosInt);
  }

  private void addChunkToCache(Chunk chunk, ChunkPosInt chunkPosInt) {
    if (chunkCachefierThread == null) {
      chunkCachefierThread = new ChunkCachefier(cachedChunks, chunk);
      chunkCachefierThread.start();
    } else {
      chunkCachefierThread.addChunkToCacheLater(chunk);
    }

    cachedChunksPositions.add(chunkPosInt);

    totalChunks++;
  }

  public CachedChunk getCachedChunk(int x, int z) {
    return cachedChunks.get(new ChunkPosInt(x >> 4, z >> 4));
  }

  public CacheState getBlockInfoCached(int xPos, int yPos, int zPos) {
    /*ChatUtil.sendChat(
      String.valueOf(getCachedChunk(xPos, zPos) == null) +
      xPos +
      " " +
      yPos +
      " " +
      zPos
    );*/

    return getCachedChunk(xPos, zPos).isBlockSolidInChunk(xPos, yPos, zPos);
  }

  public boolean isBlockCached(BlockPos bp) {
    return cachedChunksPositions.contains(
      new ChunkPosInt(bp.getX() >> 4, bp.getZ() >> 4)
    );
  }
}
