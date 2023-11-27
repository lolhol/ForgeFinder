package com.finder.calculator.util;

import com.finder.util.BlockUtil;
import com.finder.util.MathUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.jetbrains.annotations.NotNull;

public class Node extends NodeUtil implements Comparable<Node> {

  public BlockPos blockPos;

  public Node parent;

  public double gCost;
  public double hCost;
  public double totalCost;

  public double blocksPerSecond;

  public Node(
    double gCost,
    double hCost,
    double totalCost,
    Node parent,
    BlockPos bp,
    double blocksPerSecond
  ) {
    this.gCost = gCost;
    this.hCost = hCost;
    this.totalCost = totalCost;
    this.parent = parent;
    this.blockPos = bp;
    this.blocksPerSecond = blocksPerSecond;
  }

  @Override
  public int compareTo(@NotNull Node o) {
    int compare = Double.compare(totalCost, o.totalCost);

    if (compare == 0) {
      int comp1 = Double.compare(hCost, o.hCost);
      return comp1 == 0 ? Double.compare(gCost, o.gCost) : comp1;
    }

    return compare;
  }

  public boolean isOnSide() {
    if (parent == null) return false;
    return (
      absAbsSubtract(blockPos.getX(), parent.blockPos.getX()) >= 1 &&
      absAbsSubtract(blockPos.getZ(), parent.blockPos.getZ()) >= 1
    );
  }

  public List<Node> genNodesAround(Node endNode) {
    List<Node> nodeList = new ArrayList<>();

    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        for (int z = -1; z <= 1; z++) {
          BlockPos bp = this.blockPos.add(x, y, z);
          nodeList.add(makeNode(bp, this.parent, endNode));

          while (getBlock(bp.down()) == Blocks.air) {
            bp = bp.down();
            nodeList.add(makeNode(bp, this.parent, endNode));
          }
        }
      }
    }

    return nodeList;
  }

  public boolean isClearOnSides() {
    if (this.parent == null) return false;

    BlockPos blockPos = this.parent.blockPos;

    Vec3 perpNorm = getNormalVecBetweenVecsRev(
      fromBPToVec(this.blockPos),
      fromBPToVec(blockPos)
    );

    Vec3 centofLine = new Vec3(
      (double) (blockPos.getX() + this.blockPos.getX()) / 2,
      (double) (blockPos.getY() + this.blockPos.getY()) / 2,
      (double) (blockPos.getZ() + this.blockPos.getZ()) / 2
    );

    BlockPos b01 = new BlockPos(
      centofLine.xCoord + perpNorm.xCoord,
      centofLine.yCoord,
      centofLine.zCoord + perpNorm.zCoord
    );
    BlockPos b02 = new BlockPos(
      centofLine.xCoord - perpNorm.xCoord,
      centofLine.yCoord,
      centofLine.zCoord - perpNorm.zCoord
    );

    BlockPos b11 = new BlockPos(
      centofLine.xCoord + perpNorm.xCoord,
      centofLine.yCoord + 1,
      centofLine.zCoord + perpNorm.zCoord
    );
    BlockPos b12 = new BlockPos(
      centofLine.xCoord - (perpNorm.xCoord),
      centofLine.yCoord + 1,
      centofLine.zCoord - (perpNorm.zCoord)
    );

    return (
      !isBlockSolid(b01) &&
      !isBlockSolid(b02) &&
      !isBlockSolid(b11) &&
      !isBlockSolid(b12)
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
}
