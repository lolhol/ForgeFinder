package com.finder.cache;

import com.finder.util.ChatUtil;
import com.finder.util.ChunkPosInt;
import com.finder.util.MathUtil;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class ChunkCachefier extends Thread {

  int bitsCached = 0;
  final Map<ChunkPosInt, CachedChunk> cachedChunksMap;
  List<Chunk> chunksWorkLoad = new ArrayList<>();

  public ChunkCachefier(
    final Map<ChunkPosInt, CachedChunk> cachedChunksMap,
    Chunk chunkToCache
  ) {
    this.cachedChunksMap = cachedChunksMap;
    this.chunksWorkLoad.add(chunkToCache);
  }

  @Override
  public void run() {
    while (!chunksWorkLoad.isEmpty()) {
      Chunk chunk = chunksWorkLoad.remove(0);

      ChunkPosInt chunkPosInt = new ChunkPosInt(
        chunk.xPosition,
        chunk.zPosition
      );

      BlockPos bp = new BlockPos(chunk.xPosition << 4, 0, chunk.zPosition << 4);
      BitSet[] blockData = new BitSet[16];
      int realY = 0;
      for (int i = 0; i < 16; i++) {
        int cAir = 0;

        BitSet set = new BitSet(4096);
        for (int y = 0; y < 16; y++) {
          for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
              bitsCached++;

              if (isBlockSolid(chunk.getBlock(bp.add(x, realY, z)))) {
                set.set(MathUtil.getPositionIndex3DList(x, y, z, 16, 16), true);
              } else {
                cAir++;
                set.set(
                  MathUtil.getPositionIndex3DList(x, y, z, 16, 16),
                  false
                );
              }
            }
          }

          realY++;
        }

        if (cAir == 4096) {
          set.clear();
          blockData[i] = null;
        } else {
          blockData[i] = set;
        }

        ChatUtil.sendChat("Bits cached so far: " + bitsCached);
      }

      synchronized (cachedChunksMap) {
        cachedChunksMap.put(
          chunkPosInt,
          new CachedChunk(
            blockData,
            new int[] { chunkPosInt.x << 4, chunkPosInt.y << 4 }
          )
        );
      }

      if (chunksWorkLoad.isEmpty()) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public void addChunkToCacheLater(Chunk chunk) {
    chunksWorkLoad.add(chunk);
  }

  private boolean isBlockSolid(Block blockType) {
    return (
      blockType != Blocks.water &&
      blockType != Blocks.lava &&
      blockType != Blocks.air &&
      blockType != Blocks.red_flower &&
      blockType != Blocks.tallgrass &&
      blockType != Blocks.yellow_flower &&
      blockType != Blocks.double_plant &&
      blockType != Blocks.flowing_water
    );
  }
}
