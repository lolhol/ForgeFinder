package com.finder.calculator.util;

import com.finder.util.ChatUtil;
import com.finder.util.MathUtil;
import java.util.BitSet;
import java.util.HashSet;
import net.minecraft.util.BlockPos;

public class NodeListManager {

  private final HashSet<Integer[]> closed = new HashSet<>();
  private final HashSet<Integer[]> openSet = new HashSet<>();
  private BlockPos initPos;

  public NodeListManager(BlockPos startPos) {
    this.initPos = startPos;
  }

  public void addNodeClosed(Node node) {
    closed.add(new Integer[] { node.x, node.y, node.z });
  }

  public void removeNodeOpen(Node node) {
    openSet.remove(new Integer[] { node.x, node.y, node.z });
  }

  public boolean isClosed(Node node) {
    return closed.contains(new Integer[] { node.x, node.y, node.z });
  }

  public void addOpenHash(Node node) {
    openSet.add(new Integer[] { node.x, node.y, node.z });
  }

  public boolean isNodeInOpen(Node node) {
    return openSet.contains(new Integer[] { node.x, node.y, node.z });
  }

  public void clear() {
    closed.clear();
    openSet.clear();
    initPos = null;
  }
}
