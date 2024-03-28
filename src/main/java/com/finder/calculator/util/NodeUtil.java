package com.finder.calculator.util;

import com.finder.util.BlockUtil;
import com.finder.util.MathUtil;
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
