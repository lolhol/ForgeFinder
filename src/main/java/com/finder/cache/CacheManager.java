package com.finder.cache;

import com.finder.ForgeFinder;
import com.finder.events.ChunkLoadEvent;
import java.util.BitSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CacheManager {

  private BitSet biteSet = new BitSet();

  @SubscribeEvent
  public void onChunkLoad(ChunkLoadEvent event) {
    Chunk chunk = event.getChunk();
    BlockPos bp = new BlockPos(chunk.xPosition * 16, 0, chunk.zPosition * 16);
    for (int x = 0; x < 16; x++) {
      for (int y = 0; y < 256; y++) {
        for (int z = 0; z < 16; z++) {
          bp.add(x, y, z);

          if (
            isBlockSolid(ForgeFinder.MC.theWorld.getBlockState(bp).getBlock())
          ) {
            if (
              !isBlockSolid(
                ForgeFinder.MC.theWorld.getBlockState(bp.up()).getBlock()
              )
            ) {
              if (
                !isBlockSolid(
                  ForgeFinder.MC.theWorld.getBlockState(bp.up().up()).getBlock()
                )
              ) {}
            }
          }
        }
      }
    }
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

  public int getPositionIndex(int x, int y, int z) {
    return (x << 1) | (z << 5) | (y << 9);
  }
}
