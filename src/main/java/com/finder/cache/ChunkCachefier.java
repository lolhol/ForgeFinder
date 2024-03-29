package com.finder.cache;

import static com.finder.util.BlockUtil.isBlockAir;

import com.finder.ForgeFinder;
import com.finder.calculator.util.BetterBlockPos;
import com.finder.util.ChatUtil;
import com.finder.util.ChunkPosInt;
import com.finder.util.MathUtil;
import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;

public class ChunkCachefier extends Thread {

  int bitsCached = 0;
  int t = 0;
  private static final HashSet<Block> BLOCKS_TO_KEEP_TRACK_OF = new HashSet<>(
    Arrays.asList(
      Blocks.lava,
      Blocks.flowing_lava,
      Blocks.water,
      Blocks.flowing_water
    )
  );
  final Map<ChunkPosInt, CachedChunk> cachedChunksMap;
  final List<Chunk> chunksWorkLoad = Collections.synchronizedList(
    new ArrayList<>()
  );

  final HashSet<BetterBlockPos> blocksToCacheLater = new HashSet<>();

  public ChunkCachefier(
    final Map<ChunkPosInt, CachedChunk> cachedChunksMap,
    Chunk chunkToCache
  ) {
    this.cachedChunksMap = cachedChunksMap;
    this.chunksWorkLoad.add(chunkToCache);
  }

  @Override
  public void run() {
    while (true) {
      if (chunksWorkLoad.isEmpty()) {
        if (!blocksToCacheLater.isEmpty()) {
          for (BetterBlockPos block : blocksToCacheLater) {
            int chunkX = block.x >> 4;
            int chunkZ = block.z >> 4;

            if (cachedChunksMap.containsKey(new ChunkPosInt(chunkX, chunkZ))) {
              cachedChunksMap
                .get(new ChunkPosInt(chunkX, chunkZ))
                .setBlockState(
                  new BetterBlockPos(
                    Math.abs(block.x - chunkX),
                    block.y,
                    Math.abs(block.z - chunkZ)
                  ),
                  isBlockAir(
                    ForgeFinder.MC.theWorld
                      .getBlockState(new BlockPos(block.x, block.y, block.z))
                      .getBlock()
                  )
                );

              ChatUtil.sendChat(
                "Cached block at " +
                block.x +
                ", " +
                block.y +
                ", " +
                block.z +
                " in chunk " +
                chunkX +
                ", " +
                chunkZ
              );
            }
          }

          blocksToCacheLater.clear();
        } else {
          try {
            Thread.sleep(500);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        }

        continue;
      } else {
        ChatUtil.sendChat("Still working on chunks!");
      }

      Chunk chunk;
      synchronized (chunksWorkLoad) {
        chunk = chunksWorkLoad.remove(0);
      }

      ChunkPosInt chunkPosInt = new ChunkPosInt(
        chunk.xPosition,
        chunk.zPosition
      );

      BlockPos bp = new BlockPos(chunk.xPosition << 4, 0, chunk.zPosition << 4);
      BitSet[] blockData = new BitSet[16];
      final Map<String, List<BetterBlockPos>> blocksToKeepTrackMap =
        new HashMap<>();
      int realY = 0;
      for (int i = 0; i < 16; i++) {
        int cAir = 0;

        BitSet set = new BitSet(4096);
        for (int y = 0; y < 16; y++) {
          for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
              bitsCached++;

              Block block = chunk.getBlock(bp.add(x, realY, z));
              if (BLOCKS_TO_KEEP_TRACK_OF.contains(block)) {
                if (
                  !blocksToKeepTrackMap.containsKey(block.getRegistryName())
                ) {
                  List<BetterBlockPos> blocks = new ArrayList<>();
                  blocks.add(new BetterBlockPos(new int[] { x, realY, z }));
                  blocksToKeepTrackMap.put(
                    block.getRegistryName(),
                    new ArrayList<>()
                  );
                } else {
                  blocksToKeepTrackMap
                    .get(block.getRegistryName())
                    .add(new BetterBlockPos(new int[] { x, realY, z }));
                }
              }

              if (!isBlockAir(block)) {
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
          bitsCached -= 4096;
          blockData[i] = null;
        } else {
          blockData[i] = set;
        }
      }

      synchronized (cachedChunksMap) {
        cachedChunksMap.put(
          chunkPosInt,
          new CachedChunk(
            blockData,
            new int[] { chunkPosInt.x << 4, chunkPosInt.y << 4 },
            blocksToKeepTrackMap
          )
        );
      }
    }
  }

  public void addChunkToCacheLater(Chunk chunk) {
    synchronized (chunksWorkLoad) {
      chunksWorkLoad.add(chunk);
    }
  }

  public void addBlockToCacheLater(BetterBlockPos block) {
    this.blocksToCacheLater.add(block);
  }

  private boolean isBlockSolid(Block blockType) {
    return (blockType != Blocks.water && blockType != Blocks.lava);
  }
}
