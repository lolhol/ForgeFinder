package com.finder.calculator.util;

import com.finder.calculator.cost.CostConst;
import com.finder.util.BlockUtil;
import com.finder.util.MathUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.BlockPos;
import org.jetbrains.annotations.NotNull;

public class Node implements Comparable<Node> {

  public final int x;
  public final int y;
  public final int z;

  public Node parent;

  // gCost and hCost are in milliseconds
  public double totalCost;
  public boolean isSlab = false;
  public double gCost = 0;

  public Node(double totalCost, Node parent, BlockPos bp) {
    this.totalCost = totalCost;
    this.parent = parent;
    this.x = bp.getX();
    this.y = bp.getY();
    this.z = bp.getZ();
  }

  public Node(double totalCost, Node parent, Integer[] pos) {
    this.totalCost = totalCost;
    this.parent = parent;
    this.x = pos[0];
    this.y = pos[1];
    this.z = pos[2];
  }

  public Node(double totalCost, Node parent, BetterBlockPos bp) {
    this.totalCost = totalCost;
    this.parent = parent;
    this.x = bp.x;
    this.y = bp.y;
    this.z = bp.z;
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

    return Double.compare(totalCost, o.totalCost);
  }

  public List<BetterBlockPos> genNodePosAround() {
    List<BetterBlockPos> list = new ArrayList<>();

    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        for (int z = -1; z <= 1; z++) {
          if (x == 0 && z == 0) continue;
          list.add(
            new BetterBlockPos(new int[] { this.x + x, this.y + y, this.z + z })
          );
        }
      }
    }

    return list;
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
    double blocksPerSecond,
    double prevNodeGCost
  ) {
    double distanceEnd = MathUtil.distanceFromTo(x, y, z, endBlock);
    double gCost = 0;

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
      gCost += Math.abs((CostConst.FALL_1_25_BLOCKS_COST) * yDiff * 50);
    } else if (interactions[2]) {
      gCost += Math.abs(CostConst.FALL_N_BLOCKS_COST[(int) yDiff]) * 50;
    } else {
      gCost = CostConst.COST_INF;
    }

    double radius = 1;

    Iterable<BlockPos> blocksAround = BlockPos.getAllInBox(
      new BlockPos(x + radius, y + radius, z + radius),
      new BlockPos(x - radius, y - radius, z - radius)
    );

    AtomicInteger atomInt = new AtomicInteger();
    AtomicInteger underInt = new AtomicInteger();
    blocksAround.forEach(a -> {
      if (BlockUtil.isBlockSolid(a) && a.getY() >= y) {
        atomInt.incrementAndGet();
      } else if (a.getY() < y && !BlockUtil.isBlockSolid(a)) {
        underInt.incrementAndGet();
      }
    });

    //gCost += atomInt.get();
    //gCost += underInt.get();
    gCost += parent.gCost;
    this.gCost = gCost;

    //ChatUtil.sendChat(hCost + " | " + gCost);

    totalCost = hCost + gCost;
  }

  public BlockPos getBlockPos() {
    return new BlockPos(this.x, this.y, this.z);
  }

  public int[] getPosInt() {
    return new int[] { this.x, this.y, this.z };
  }

  public BetterBlockPos getBetterBP() {
    return new BetterBlockPos(new int[] { x, y, z });
  }
}
