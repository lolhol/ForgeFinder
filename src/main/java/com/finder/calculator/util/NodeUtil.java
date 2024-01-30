package com.finder.calculator.util;

import com.finder.util.BlockUtil;
import net.minecraft.util.BlockPos;

public class NodeUtil extends BlockUtil {

  public Node makeNode(BlockPos block, Node parent) {
    return new Node(0, parent, block);
  }

  // [walk, fall, jump]
  public boolean[] isAbleToInteract(Node node) {
    BlockPos bp = new BlockPos(node.x, node.y, node.z);
    BlockPos parentBP = new BlockPos(
      node.parent.x,
      node.parent.y,
      node.parent.z
    );
    return new boolean[] {
      canWalkOn(node, bp),
      canFall(node, bp, parentBP),
      canJumpOn(node, bp, parentBP),
    };
  }

  private boolean canWalkOn(Node node, BlockPos block) {
    Node parent = node.parent;

    double yDif = Math.abs(parent.y - block.getY());

    BlockPos blockAbove1 = block.add(0, 1, 0);
    BlockPos blockBelow1 = block.add(0, -1, 0);

    boolean isWalkableSlab =
      (
        !node.isOnSide() &&
        getBlock(blockBelow1).getRegistryName().contains("slab")
      );

    if (
      yDif <= 0.001 &&
      !isBlockSolid(blockAbove1) &&
      isBlockSolid(blockBelow1) &&
      (
        (isBlockWalkable(block) || isWalkableSlab) ||
        (node.parent != null && node.parent.isSlab)
      )
    ) {
      node.isSlab = isWalkableSlab;
      if (!node.isOnSide()) {
        return true;
      }

      return node.isClearOnSides();
    }

    return false;
  }

  private boolean canJumpOn(Node node, BlockPos block, BlockPos parentBlock) {
    if (node.parent != null && node.parent.isSlab) return false;
    double yDiff = block.getY() - parentBlock.getY();

    BlockPos blockAbove1 = block.add(0, 1, 0);
    BlockPos blockBelow1 = block.add(0, -1, 0);

    BlockPos blockAboveOneParent = parentBlock.add(0, 1, 0);
    BlockPos blockAboveTwoParent = parentBlock.add(0, 2, 0);

    if (
      yDiff == 1 &&
      isBlockSolid(blockBelow1) &&
      !isBlockSolid(blockAbove1) &&
      !isBlockSolid(blockAboveOneParent) &&
      !isBlockSolid(blockAboveTwoParent) &&
      isBlockWalkable(block)
    ) {
      if (distanceFromToXZ(block, parentBlock) <= 1) {
        return true;
      }

      return node.isClearOnSides();
    }

    return false;
  }

  private boolean canFall(Node node, BlockPos block, BlockPos parentBlock) {
    double yDiff = block.getY() - parentBlock.getY();

    BlockPos blockBelow1 = block.add(0, -1, 0);
    BlockPos blockAbove1 = block.add(0, 1, 0);

    if (
      (
        yDiff < 0 &&
        yDiff > -4 &&
        isBlockSolid(blockBelow1) &&
        !isBlockSolid(blockAbove1)
      ) &&
      isBlockWalkable(block)
    ) {
      if (distanceFromToXZ(block, parentBlock) <= 1) {
        return true;
      }

      return node.isClearOnSides();
    }

    return false;
  }

  private boolean isAllClearToY(int y1, int y2, BlockPos block) {
    boolean isGreater = y1 < y2;
    int rem = 0;

    while (y1 != y2) {
      BlockPos curBlock = block.add(0, rem, 0);

      if (!isBlockSolid(curBlock)) return false;
      y2--;
      rem--;
    }

    return true;
  }
}
