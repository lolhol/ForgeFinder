package com.finder.calculator.util;

import com.finder.calculator.cost.CostConst;
import com.finder.debug.util.RenderUtil;
import com.finder.util.BlockUtil;
import com.finder.util.ChatUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.jetbrains.annotations.NotNull;

public class Node extends NodeUtil implements Comparable<Node> {

  public BlockPos blockPos;

  public Node parent;

  // gCost and hCost are in milliseconds
  public double gCost;
  public double hCost;
  public double totalCost;

  public double blocksPerSecond;
  public boolean isSlab = false;

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
      absAbsSubtract(blockPos.getX(), parent.blockPos.getX()) >= 1 &&
      absAbsSubtract(blockPos.getZ(), parent.blockPos.getZ()) >= 1
    );
  }

  public List<Node> genNodesAround(Node endNode) {
    List<Node> nodeList = new ArrayList<>();

    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        for (int z = -1; z <= 1; z++) {
          if (x == 0 && y == 0 && z == 0) continue;
          BlockPos bp = new BlockPos(
            blockPos.getX() + x,
            blockPos.getY() + y,
            blockPos.getZ() + z
          );
          nodeList.add(makeNode(bp, this, endNode));
          while (getBlock(bp.down()) == Blocks.air) {
            bp = bp.down();
            nodeList.add(makeNode(bp, this, endNode));
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
      centofLine.xCoord - perpNorm.xCoord,
      centofLine.yCoord + 1,
      centofLine.zCoord - perpNorm.zCoord
    );

    if (this.blockPos.getX() == -81 && this.blockPos.getZ() == 308) {
      RenderUtil.addBlockToRenderSync(b01);
      RenderUtil.addBlockToRenderSync(b02);
      RenderUtil.addBlockToRenderSync(b11);
      RenderUtil.addBlockToRenderSync(b12);
      ChatUtil.sendChat("!!!!!!!!");
    }

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

  public void generateCostsForNode(BlockPos endBlock, boolean[] interactions) {
    double distanceEnd = distanceFromTo(blockPos, endBlock);

    if (blocksPerSecond != 0) {
      distanceEnd = distanceEnd / blocksPerSecond * 1000;
    }

    hCost = distanceEnd;

    double yDiff = Math.abs(blockPos.getY() - parent.blockPos.getY());

    if (interactions[0]) {
      gCost +=
        CostConst.calculateTimeToWalkOneBlock(
          this.blocksPerSecond,
          blockPos,
          parent.blockPos
        );
    } else if (interactions[1]) {
      gCost += (CostConst.FALL_1_25_BLOCKS_COST) * yDiff * 50;
    } else if (interactions[2]) {
      gCost += CostConst.JUMP_ONE_BLOCK_COST * yDiff * 50;
      ChatUtil.sendChat(String.valueOf(gCost));
    } else {
      gCost = CostConst.COST_INF;
    }

    double radius = Math.sqrt(blocksPerSecond);

    Iterable<BlockPos> blocksAround = BlockPos.getAllInBox(
      new BlockPos(
        blockPos.getX() + radius,
        blockPos.getY() + radius,
        blockPos.getZ() + radius
      ),
      new BlockPos(
        blockPos.getX() - radius,
        blockPos.getY() - radius,
        blockPos.getZ() - radius
      )
    );

    AtomicInteger atomInt = new AtomicInteger();
    AtomicInteger underInt = new AtomicInteger();
    blocksAround.forEach(a -> {
      if (isBlockSolid(a) && a.getY() >= blockPos.getY()) {
        atomInt.incrementAndGet();
      } else if (a.getY() < blockPos.getY() && !isBlockSolid(a)) {
        underInt.incrementAndGet();
      }
    });

    gCost += atomInt.get() * 20;
    gCost += underInt.get() * 10;

    if (parent != null) {
      gCost += parent.gCost;
    }

    totalCost = hCost + gCost;
    //ChatUtil.sendChat(String.valueOf(totalCost));
  }
}
