package com.finder.calculator.util;

import com.finder.util.ChatUtil;
import com.finder.util.MathUtil;
import java.util.BitSet;
import java.util.HashSet;
import net.minecraft.util.BlockPos;

public class NodeListManager {

  private final BitSet closedSet = new BitSet();
  private final HashSet<int[]> openSet = new HashSet<>();
  private BlockPos initPos;

  public NodeListManager(BlockPos startPos) {
    this.initPos = startPos;
  }

  public void addNodeClosed(Node node) {
    try {
      int pos = MathUtil.getPositionIndex(
        initPos.getX() - node.x,
        initPos.getY() - node.y,
        initPos.getZ() - node.z
      );

      closedSet.set(pos, true);
    } catch (Exception e) {
      ChatUtil.sendChat("ERRRRRR!!! (NodeListManager)");
    }
  }

  public boolean isClosed(Node node) {
    int pos = MathUtil.getPositionIndex(
      initPos.getX() - node.x,
      initPos.getY() - node.y,
      initPos.getZ() - node.z
    );

    return closedSet.length() <= pos || !closedSet.get(pos);
  }

  public void addOpenHash(Node node) {
    openSet.add(new int[] { node.x, node.y, node.z });
  }

  public boolean isNodeInOpen(Node node) {
    return openSet.contains(new int[] { node.x, node.y, node.z });
  }

  public void clear() {
    closedSet.clear();
    openSet.clear();
    initPos = null;
  }
}
