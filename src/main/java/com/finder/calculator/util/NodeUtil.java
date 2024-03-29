package com.finder.calculator.util;

import com.finder.cache.util.CacheState;
import com.finder.util.BlockUtil;
import com.finder.util.MathUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.util.BlockPos;

public class NodeUtil {

  public static Node makeNode(BlockPos block, Node parent) {
    return new Node(0, parent, block);
  }

  // [walk, fall, jump]
  public static boolean[] isAbleToInteract(int[] node, Node parent) {
    BlockPos bp = new BlockPos(node[0], node[1], node[2]);
    BlockPos parentBP = new BlockPos(parent.x, parent.y, parent.z);
    return new boolean[] {
      canWalkOn(parent, bp),
      canFall(bp, parentBP),
      canJumpOn(bp, parentBP),
    };
  }

  public static List<List<BlockPos>> getBlocksWithInteractions(
    int[] node,
    Node parent
  ) {
    BlockPos bp = new BlockPos(node[0], node[1], node[2]);
    BlockPos parentBP = new BlockPos(parent.x, parent.y, parent.z);
    List<List<BlockPos>> returnList = new ArrayList<>();
    returnList.add(canWalkOn(parentBP, bp, parent.blocksNeededToBeMined));
    returnList.add(canJumpOnMining(bp, parentBP, parent.blocksNeededToBeMined));
    returnList.add(canFallMining(bp, parentBP, parent.blocksNeededToBeMined));
    return returnList;
  }

  /**
   * Determines if a block can be walked on based on its position and parent block.
   *
   * @param  parent          the parent block
   * @param  block           the block to check
   * @param  parentMined     a set of integers representing the parent blocks that have been mined
   * @return                 a list of BlockPos objects representing the blocks that can be removed
   */
  private static List<BlockPos> canWalkOn(
    BlockPos parent,
    BlockPos block,
    HashSet<Integer> parentMined
  ) {
    double yDif = Math.abs(parent.getY() - block.getY());

    BlockPos blockAbove1 = block.add(0, 1, 0);
    BlockPos blockBelow1 = block.add(0, -1, 0);

    boolean isOnSide = BlockUtil.isOnSide(block, parent);
    boolean isWalkableSlab =
      (BlockUtil.getBlock(blockBelow1).getRegistryName().contains("slab"));

    CacheState stateBelow1 = BlockUtil.getCacheState(blockBelow1);

    if (
      !isWalkableSlab &&
      (
        stateBelow1 == CacheState.UNOBSTRUCTED ||
        stateBelow1 == CacheState.NOT_SOLID_NOT_AIR ||
        parentMined.contains(BlockUtil.getHashCode(blockBelow1))
      )
    ) {
      return null;
    }

    if (yDif >= 0.1 && !isWalkableSlab) {
      return null;
    }

    List<BlockPos> blocksToRem = new ArrayList<>();

    if (isOnSide) {
      if (isWalkableSlab) {
        List<BlockPos> blocksOnSides = BlockUtil.getBlocksOnSidesSolid(
          block,
          parent.add(0, 1, 0)
        );

        blocksToRem.addAll(blocksOnSides);
      } else if (BlockUtil.isBlockSolid(blockBelow1)) {
        List<BlockPos> blocksOnSides = BlockUtil.getBlocksOnSidesSolid(
          block,
          parent
        );

        blocksToRem.addAll(blocksOnSides);
      }
    }

    CacheState blockAboveOneCacheState = BlockUtil.getCacheState(blockAbove1);
    if (blockAboveOneCacheState == CacheState.NOT_SOLID_NOT_AIR) {
      return null;
    }

    if (blockAboveOneCacheState == CacheState.OBSTRUCTED) {
      blocksToRem.add(blockAbove1);
    }

    CacheState curBlockCacheState = BlockUtil.getCacheState(block);

    if (curBlockCacheState == CacheState.NOT_SOLID_NOT_AIR) return null;

    if (curBlockCacheState == CacheState.OBSTRUCTED) {
      blocksToRem.add(block);
    }

    return blocksToRem;
  }

  /**
   * Checks if the given block can be mined based on its position relative to the parent block and the set of already mined blocks.
   *
   * @param  block            the block to check for mining
   * @param  parentBlock      the parent block of the given block
   * @param  parentMined      the set of already mined blocks
   * @return                  a list of blocks to remove if the given block can be mined, otherwise null
   */
  private static List<BlockPos> canJumpOnMining(
    BlockPos block,
    BlockPos parentBlock,
    HashSet<Integer> parentMined
  ) {
    double yDiff = block.getY() - parentBlock.getY();

    if (yDiff != 1) {
      return null;
    }

    BlockPos blockBelow1 = block.add(0, -1, 0);
    CacheState blockBelow1CacheState = BlockUtil.getCacheState(blockBelow1);
    if (
      blockBelow1CacheState == CacheState.UNOBSTRUCTED ||
      blockBelow1CacheState == CacheState.NOT_SOLID_NOT_AIR ||
      parentMined.contains(BlockUtil.getHashCode(blockBelow1))
    ) {
      return null;
    }

    List<BlockPos> blocksToRemove = new ArrayList<>();

    BlockPos blockAbove1 = block.add(0, 1, 0);
    CacheState blockAbove1CacheState = BlockUtil.getCacheState(blockAbove1);

    if (blockAbove1CacheState == CacheState.NOT_SOLID_NOT_AIR) return null;

    if (blockAbove1CacheState == CacheState.OBSTRUCTED) {
      blocksToRemove.add(blockAbove1);
    }

    CacheState curBlockCacheState = BlockUtil.getCacheState(block);

    if (curBlockCacheState == CacheState.NOT_SOLID_NOT_AIR) return null;

    if (curBlockCacheState == CacheState.OBSTRUCTED) {
      blocksToRemove.add(block);
    }

    BlockPos blockAboveOneParent = parentBlock.add(0, 1, 0);

    CacheState blockAboveParentCacheState = BlockUtil.getCacheState(
      blockAboveOneParent
    );

    if (blockAboveParentCacheState == CacheState.NOT_SOLID_NOT_AIR) return null;

    if (blockAboveParentCacheState == CacheState.OBSTRUCTED) {
      blocksToRemove.add(blockAboveOneParent);
    }

    BlockPos blockAboveTwoParent = parentBlock.add(0, 2, 0);

    CacheState blockAboveTwoParentCacheState = BlockUtil.getCacheState(
      blockAboveTwoParent
    );

    if (
      blockAboveTwoParentCacheState == CacheState.NOT_SOLID_NOT_AIR
    ) return null;

    if (blockAboveTwoParentCacheState == CacheState.OBSTRUCTED) {
      blocksToRemove.add(blockAboveTwoParent);
    }

    boolean isOnSide = MathUtil.distanceFromToXZ(block, parentBlock) > 1;
    if (isOnSide) {
      List<BlockPos> blocksOnSides = BlockUtil.getBlocksOnSidesSolid(
        block,
        parentBlock.add(0, 1, 0)
      );

      blocksToRemove.addAll(blocksOnSides);
    }

    return blocksToRemove;
  }

  private static List<BlockPos> canFallMining(
    BlockPos block,
    BlockPos parentBlock,
    HashSet<Integer> parentMined
  ) {
    double yDiff = block.getY() - parentBlock.getY();

    BlockPos blockBelow1 = block.add(0, -1, 0);
    CacheState blockBelowCacheState = BlockUtil.getCacheState(blockBelow1);
    if (
      yDiff > -1 ||
      blockBelowCacheState == CacheState.UNOBSTRUCTED ||
      blockBelowCacheState == CacheState.NOT_SOLID_NOT_AIR ||
      parentMined.contains(BlockUtil.getHashCode(blockBelow1))
    ) {
      return null;
    }

    List<BlockPos> blocksToRemove = new ArrayList<>();

    BlockPos blockAbove1 = block.add(0, Math.abs(yDiff) + 1, 0);

    CacheState blockAbove1CacheState = BlockUtil.getCacheState(blockAbove1);

    if (blockAbove1CacheState == CacheState.NOT_SOLID_NOT_AIR) return null;

    if (blockAbove1CacheState == CacheState.OBSTRUCTED) {
      blocksToRemove.add(blockAbove1);
    }

    boolean isBlockOnSide = MathUtil.distanceFromToXZ(block, parentBlock) > 1;
    if (isBlockOnSide) {
      List<BlockPos> blocksOnSides = BlockUtil.getBlocksOnSidesSolid(
        block,
        parentBlock
      );

      blocksToRemove.addAll(blocksOnSides);
    }

    for (int y = 0; y < blockAbove1.getY() - block.getY(); y++) {
      BlockPos curBlock = block.add(0, y, 0);
      CacheState cacheState = BlockUtil.getCacheState(curBlock);

      if (cacheState == CacheState.NOT_SOLID_NOT_AIR) return null;

      if (cacheState == CacheState.OBSTRUCTED) {
        blocksToRemove.add(curBlock);
      }
    }

    return blocksToRemove;
  }

  private static boolean canWalkOn(Node parent, BlockPos block) {
    double yDif = Math.abs(parent.y - block.getY());

    BlockPos blockAbove1 = block.add(0, 1, 0);
    BlockPos blockBelow1 = block.add(0, -1, 0);

    int[][] blockParent = new int[][] {
      new int[] { block.getX(), block.getY(), block.getZ() },
      new int[] { parent.x, parent.y, parent.z },
    };

    boolean isWalkableSlab =
      (
        BlockUtil.getBlock(blockBelow1).getRegistryName().contains("slab") &&
        BlockUtil.isOnSide(blockParent[0], blockParent[1])
      );

    CacheState blockAbove1State = BlockUtil.getCacheState(blockAbove1);
    CacheState blockBelow1State = BlockUtil.getCacheState(blockBelow1);

    if (
      blockAbove1State == CacheState.NOT_SOLID_NOT_AIR ||
      blockBelow1State == CacheState.NOT_SOLID_NOT_AIR
    ) return false;

    if (
      yDif <= 0.1 &&
      blockAbove1State == CacheState.UNOBSTRUCTED &&
      blockBelow1State == CacheState.OBSTRUCTED &&
      ((BlockUtil.isBlockWalkable(block) || isWalkableSlab))
    ) {
      if (!BlockUtil.isOnSide(blockParent[0], blockParent[1])) {
        return true;
      }

      return BlockUtil.isClearOnSides(blockParent[0], blockParent[1]);
    }

    return false;
  }

  private static boolean canJumpOn(BlockPos block, BlockPos parentBlock) {
    double yDiff = block.getY() - parentBlock.getY();

    BlockPos blockAbove1 = block.add(0, 1, 0);
    BlockPos blockBelow1 = block.add(0, -1, 0);

    BlockPos blockAboveOneParent = parentBlock.add(0, 1, 0);
    BlockPos blockAboveTwoParent = parentBlock.add(0, 2, 0);

    CacheState blockAbove1State = BlockUtil.getCacheState(blockAbove1);
    if (blockAbove1State == CacheState.NOT_SOLID_NOT_AIR) return false;

    CacheState blockBelow1CacheState = BlockUtil.getCacheState(blockBelow1);
    if (blockBelow1CacheState == CacheState.NOT_SOLID_NOT_AIR) return false;

    CacheState blockAboveOneParentState = BlockUtil.getCacheState(
      blockAboveOneParent
    );
    if (blockAboveOneParentState == CacheState.NOT_SOLID_NOT_AIR) return false;

    CacheState blockAboveTwoParentState = BlockUtil.getCacheState(
      blockAboveTwoParent
    );
    if (blockAboveTwoParentState == CacheState.NOT_SOLID_NOT_AIR) return false;

    CacheState blockCache = BlockUtil.getCacheState(block);

    if (
      yDiff == 1 &&
      blockAbove1State == CacheState.UNOBSTRUCTED &&
      blockBelow1CacheState == CacheState.OBSTRUCTED &&
      blockAboveOneParentState == CacheState.UNOBSTRUCTED &&
      blockAboveTwoParentState == CacheState.UNOBSTRUCTED &&
      blockCache == CacheState.OBSTRUCTED
    ) {
      if (MathUtil.distanceFromToXZ(block, parentBlock) <= 1) {
        return true;
      }

      return BlockUtil.isClearOnSides(
        new int[] { block.getX(), block.getY(), block.getZ() },
        new int[] { parentBlock.getX(), parentBlock.getY(), parentBlock.getZ() }
      );
    }

    return false;
  }

  private static boolean canFall(BlockPos block, BlockPos parentBlock) {
    double yDiff = block.getY() - parentBlock.getY();

    BlockPos blockBelow1 = block.add(0, -1, 0);
    BlockPos blockAbove1 = block.add(0, Math.abs(yDiff) + 1, 0);

    CacheState blockBelow1State = BlockUtil.getCacheState(blockBelow1);
    CacheState blockAbove1State = BlockUtil.getCacheState(blockAbove1);

    if (
      yDiff < 0 &&
      yDiff > -4 &&
      blockBelow1State == CacheState.OBSTRUCTED &&
      blockAbove1State == CacheState.UNOBSTRUCTED &&
      BlockUtil.isBlockWalkable(block)
    ) {
      if (MathUtil.distanceFromToXZ(block, parentBlock) <= 1) {
        return true;
      }

      return BlockUtil.isClearOnSides(
        new int[] { block.getX(), block.getY(), block.getZ() },
        new int[] { parentBlock.getX(), parentBlock.getY(), parentBlock.getZ() }
      );
    }

    return false;
  }
}
