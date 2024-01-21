package com.finder.calculator.util;

import com.finder.ForgeFinder;
import com.finder.util.BlockUtil;
import com.finder.util.ChatUtil;
import net.minecraft.block.state.BlockState;
import net.minecraft.util.BlockPos;

public class NodeUtil extends BlockUtil {

  public Node makeNode(BlockPos block, Node parent, Node endNode) {
    return new Node(0, 0, 0, parent, block, parent.blocksPerSecond);
  }

  // [walk, fall, jump]
  public boolean[] isAbleToInteract(Node node) {
    return new boolean[] { canWalkOn(node), canFall(node), canJumpOn(node) };
  }

  private boolean canWalkOn(Node node) {
    BlockPos block = node.blockPos;
    Node parent = node.parent;

    double yDif = Math.abs(parent.blockPos.getY() - block.getY());

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

  private boolean canJumpOn(Node node) {
    if (node.parent != null && node.parent.isSlab) return false;
    BlockPos block = node.blockPos;
    Node parentBlock = node.parent;
    double yDiff = block.getY() - parentBlock.blockPos.getY();

    BlockPos blockAbove1 = block.add(0, 1, 0);
    BlockPos blockBelow1 = block.add(0, -1, 0);

    BlockPos blockAboveOneParent = parentBlock.blockPos.add(0, 1, 0);
    BlockPos blockAboveTwoParent = parentBlock.blockPos.add(0, 2, 0);

    if (
      yDiff == 1 &&
      isBlockSolid(blockBelow1) &&
      !isBlockSolid(blockAbove1) &&
      !isBlockSolid(blockAboveOneParent) &&
      !isBlockSolid(blockAboveTwoParent) &&
      isBlockWalkable(block)
    ) {
      if (distanceFromToXZ(block, parentBlock.blockPos) <= 1) {
        return true;
      }

      return node.isClearOnSides();
    }

    return false;
  }

  private boolean canFall(Node node) {
    BlockPos block = node.blockPos;
    Node parentBlock = node.parent;

    double yDiff = block.getY() - parentBlock.blockPos.getY();

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
      if (distanceFromToXZ(block, parentBlock.blockPos) <= 1) {
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
