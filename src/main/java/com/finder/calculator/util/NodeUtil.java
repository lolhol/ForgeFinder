package com.finder.calculator.util;

import com.finder.util.BlockUtil;
import com.finder.util.MathUtil;
import java.util.ArrayList;
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
    returnList.add(canWalkOn(parentBP, bp));
    returnList.add(canJumpOnMining(bp, parentBP));
    returnList.add(canFallMining(bp, parentBP));
    return returnList;
  }

  private static List<BlockPos> canWalkOn(BlockPos parent, BlockPos block) {
    double yDif = Math.abs(parent.getY() - block.getY());

    List<BlockPos> blocksToMine = new ArrayList<>();
    BlockPos blockAbove1 = block.add(0, 1, 0);
    BlockPos blockBelow1 = block.add(0, -1, 0);

    boolean isOnSide = BlockUtil.isOnSide(block, parent);
    boolean isWalkableSlab =
      (BlockUtil.getBlock(blockBelow1).getRegistryName().contains("slab"));

    if (!isWalkableSlab && !BlockUtil.isBlockSolid(blockBelow1)) {
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

    if (BlockUtil.isBlockSolid(blockAbove1)) {
      blocksToRem.add(blockAbove1);
    }

    if (BlockUtil.isBlockSolid(block)) {
      blocksToRem.add(block);
    }

    return blocksToMine;
  }

  private static List<BlockPos> canJumpOnMining(
    BlockPos block,
    BlockPos parentBlock
  ) {
    double yDiff = block.getY() - parentBlock.getY();

    if (yDiff != 1) {
      return null;
    }

    BlockPos blockBelow1 = block.add(0, -1, 0);
    if (!BlockUtil.isBlockSolid(blockBelow1)) {
      return null;
    }

    List<BlockPos> blocksToRemove = new ArrayList<>();

    BlockPos blockAbove1 = block.add(0, 1, 0);
    if (BlockUtil.isBlockSolid(blockAbove1)) {
      blocksToRemove.add(blockAbove1);
    }

    if (BlockUtil.isBlockSolid(block)) {
      blocksToRemove.add(block);
    }

    BlockPos blockAboveOneParent = parentBlock.add(0, 1, 0);
    if (BlockUtil.isBlockSolid(blockAboveOneParent)) {
      blocksToRemove.add(blockAboveOneParent);
    }

    BlockPos blockAboveTwoParent = parentBlock.add(0, 2, 0);
    if (BlockUtil.isBlockSolid(blockAboveTwoParent)) {
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
    BlockPos parentBlock
  ) {
    double yDiff = block.getY() - parentBlock.getY();

    BlockPos blockBelow1 = block.add(0, -1, 0);
    if (yDiff > -1 || !BlockUtil.isBlockSolid(blockBelow1)) {
      return null;
    }

    List<BlockPos> blocksToRemove = new ArrayList<>();

    BlockPos blockAbove1 = block.add(0, Math.abs(yDiff) + 1, 0);
    if (BlockUtil.isBlockSolid(blockAbove1)) {
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
      if (BlockUtil.isBlockSolid(curBlock)) {
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

    if (
      yDif <= 0.1 &&
      !BlockUtil.isBlockSolid(blockAbove1) &&
      BlockUtil.isBlockSolid(blockBelow1) &&
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

    if (
      yDiff == 1 &&
      BlockUtil.isBlockSolid(blockBelow1) &&
      !BlockUtil.isBlockSolid(blockAbove1) &&
      !BlockUtil.isBlockSolid(blockAboveOneParent) &&
      !BlockUtil.isBlockSolid(blockAboveTwoParent) &&
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

  /**
   * @apiNote this MUST be used for only JUMPING and WALKING!
   * @param block
   * @param parent
   * @return
   */
  public static List<BlockPos> getBlocksNeededToMineToGetTo(
    BlockPos block,
    BlockPos parent
  ) {
    List<BlockPos> blocksNeededToBeRemoved = new ArrayList<>();
    BlockPos blockAbove1 = block.add(0, 1, 0);
    boolean isNotOnSide = MathUtil.distanceFromToXZ(block, parent) <= 1;
    if (isNotOnSide) {
      if (BlockUtil.isBlockSolid(blockAbove1)) {
        blocksNeededToBeRemoved.add(blockAbove1);
      }

      if (BlockUtil.isBlockSolid(block)) {
        blocksNeededToBeRemoved.add(block);
      }

      return blocksNeededToBeRemoved;
    }

    List<BlockPos> blocksOnSides = BlockUtil.getBlocksOnSidesSolid(
      block,
      parent
    );

    if (blocksOnSides.isEmpty()) {
      return blocksNeededToBeRemoved;
    } else {
      blocksNeededToBeRemoved.addAll(blocksOnSides);
    }

    return blocksNeededToBeRemoved;
  }

  private static boolean canFall(BlockPos block, BlockPos parentBlock) {
    double yDiff = block.getY() - parentBlock.getY();

    BlockPos blockBelow1 = block.add(0, -1, 0);
    BlockPos blockAbove1 = block.add(0, Math.abs(yDiff) + 1, 0);

    if (
      yDiff < 0 &&
      yDiff > -4 &&
      BlockUtil.isBlockSolid(blockBelow1) &&
      !BlockUtil.isBlockSolid(blockAbove1) &&
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

  private boolean isAllClearToY(int y1, int y2, BlockPos block) {
    boolean isGreater = y1 < y2;
    int rem = 0;

    while (y1 != y2) {
      BlockPos curBlock = block.add(0, rem, 0);

      if (!BlockUtil.isBlockSolid(curBlock)) return false;
      y2--;
      rem--;
    }

    return true;
  }
}
