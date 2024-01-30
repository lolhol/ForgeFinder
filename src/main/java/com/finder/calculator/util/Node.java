package com.finder.calculator.util;

import com.finder.calculator.cost.CostConst;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.jetbrains.annotations.NotNull;

public class Node extends NodeUtil implements Comparable<Node> {

  public final int x;
  public final int y;
  public final int z;

  public Node parent;

  // gCost and hCost are in milliseconds
  public double totalCost;
  public boolean isSlab = false;

  public Node(double totalCost, Node parent, BlockPos bp) {
    this.totalCost = totalCost;
    this.parent = parent;
    this.x = bp.getX();
    this.y = bp.getY();
    this.z = bp.getZ();
  }

  public boolean equals(Node o) {
    return o.x == this.x && o.y == this.y && o.z == this.z;
  }

  @Override
  public int compareTo(@NotNull Node o) {
    /*int compare = Double.compare(hCost, o.hCost);

    if (compare == 0) {
      int comp1 = Double.compare(totalCost, o.totalCost);
      return comp1 == 0 ? Double.compare(gCost, o.gCost) : comp1;
    }*/

    return totalCost != o.totalCost ? totalCost < o.totalCost ? -1 : 1 : 0;
  }

  public boolean isOnSide() {
    if (parent == null) return false;
    return (
      absAbsSubtract(x, parent.x) >= 1 && absAbsSubtract(z, parent.z) >= 1
    );
  }

  public List<Node> genNodesAround(Node endNode) {
    List<Node> nodeList = new ArrayList<>();

    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        for (int z = -1; z <= 1; z++) {
          if (x == 0 && y == 0 && z == 0) continue;
          BlockPos bp = new BlockPos(this.x + x, this.y + y, this.z + z);
          nodeList.add(makeNode(bp, this));
          while (getBlock(bp.down()) == Blocks.air) {
            bp = bp.down();
            nodeList.add(makeNode(bp, this));
          }
        }
      }
    }

    return nodeList;
  }

  public boolean isClearOnSides() {
    if (this.parent == null) return false;

    double changeX = parent.x - x;
    double changeZ = parent.z - z;

    BlockPos blockPos = new BlockPos(x, y, z);
    return (
      !isBlockSolid(blockPos.add(0, 0, changeZ)) &&
      !isBlockSolid(blockPos.add(changeX, 0, 0)) &&
      !isBlockSolid(blockPos.add(0, 1, changeZ)) &&
      !isBlockSolid(blockPos.add(changeX, 1, 0))
    );
  }

  public List<Node> makeList() {
    List<Node> retList = new ArrayList<>();

    Node cur = this;
    while (cur.parent != null) {
      retList.add(cur);
      cur = cur.parent;
    }

    Collections.reverse(retList);

    return retList;
  }

  public void generateCostsForNode(
    BlockPos endBlock,
    boolean[] interactions,
    double blocksPerSecond
  ) {
    double gCost = 0;

    double distanceEnd = distanceFromTo(x, y, z, endBlock);

    if (blocksPerSecond != 0) {
      distanceEnd = distanceEnd / blocksPerSecond * 1000;
    }

    double hCost = distanceEnd;

    double yDiff = Math.abs(y - parent.y);

    if (interactions[0]) {
      gCost +=
        CostConst.calculateTimeToWalkOneBlock(
          blocksPerSecond,
          x,
          z,
          parent.x,
          parent.z
        );
    } else if (interactions[1]) {
      gCost += (CostConst.FALL_1_25_BLOCKS_COST) * yDiff * 50;
    } else if (interactions[2]) {
      gCost += CostConst.JUMP_ONE_BLOCK_COST * yDiff * 50;
      //ChatUtil.sendChat(String.valueOf(gCost));
    } else {
      gCost = CostConst.COST_INF;
    }

    double radius = Math.sqrt(blocksPerSecond);

    Iterable<BlockPos> blocksAround = BlockPos.getAllInBox(
      new BlockPos(x + radius, y + radius, z + radius),
      new BlockPos(x - radius, y - radius, z - radius)
    );

    AtomicInteger atomInt = new AtomicInteger();
    AtomicInteger underInt = new AtomicInteger();
    blocksAround.forEach(a -> {
      if (isBlockSolid(a) && a.getY() >= y) {
        atomInt.incrementAndGet();
      } else if (a.getY() < y && !isBlockSolid(a)) {
        underInt.incrementAndGet();
      }
    });

    gCost += atomInt.get() * 20;
    gCost += underInt.get() * 10;

    /*if (parent != null) {
      gCost += parent.gCost;
    }*/

    totalCost = hCost + gCost;
    //ChatUtil.sendChat(String.valueOf(totalCost));
  }

  public BlockPos getBlockPos() {
    return new BlockPos(this.x, this.y, this.z);
  }
}
