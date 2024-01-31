package com.finder.calculator;

import com.finder.calculator.config.Config;
import com.finder.calculator.errors.NoPathException;
import com.finder.calculator.util.Node;
import com.finder.calculator.util.NodeListManager;
import com.finder.debug.util.RenderUtil;
import com.finder.util.ChatUtil;
import java.util.PriorityQueue;
import net.minecraft.util.BlockPos;
import org.apache.commons.lang3.tuple.Triple;

public class AStarPathfinder {

  public long timeTaken = System.currentTimeMillis();
  public int nodesConsidered = 0;
  boolean isDone = false;

  public void run(Config config) throws NoPathException {
    Node starRes = runStar(config);

    int tmp = nodesConsidered;
    nodesConsidered = 0;

    if (starRes == null) {
      throw new NoPathException("No Path Found!", tmp);
    }

    config.callback.finderDone(starRes.makeList(), timeTaken, tmp);
  }

  public void run(Config config, boolean printProgress) throws NoPathException {
    if (printProgress) {
      new Thread(() -> {
        while (!isDone) {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }

          ChatUtil.sendChat("Nodes considered: " + nodesConsidered);
        }
      })
        .start();
    }

    Node starRes = runStar(config);

    int tmp = nodesConsidered;
    nodesConsidered = 0;

    if (starRes == null) {
      throw new NoPathException("No Path Found!", tmp);
    }

    config.callback.finderDone(starRes.makeList(), timeTaken, tmp);
  }

  public Node runStar(Config config) {
    long timeInit = System.currentTimeMillis();

    BlockPos startBP = new BlockPos(
      config.start.x,
      config.start.y,
      config.start.z
    );
    BlockPos endBP = new BlockPos(config.end.x, config.end.y, config.end.z);

    final NodeListManager manager = new NodeListManager(startBP);
    PriorityQueue<Node> openSet = new PriorityQueue<>();

    manager.addOpenHash(config.start);
    openSet.add(config.start);

    int i = 0;
    isDone = false;
    nodesConsidered = 0;
    while (!openSet.isEmpty() && i < config.maxIter) {
      Node best = openSet.poll();

      if (config.end.equals(best)) {
        timeTaken = System.currentTimeMillis() - timeInit;
        isDone = true;
        return best;
      }

      for (Node n : best.genNodesAround(config.end)) {
        if (manager.isClosed(n) || manager.isNodeInOpen(n)) continue;

        boolean[] interactions = n.isAbleToInteract(n);
        if (!interactions[0] && !interactions[1] && !interactions[2]) {
          continue;
        }

        n.generateCostsForNode(endBP, interactions, config.blocksPerSecond);

        if (config.costs != null) {
          Triple<Double, Double, Double> costs = config.costs.addCost(n);
          n.totalCost += costs.getRight() + costs.getLeft() + costs.getMiddle();
        }

        openSet.add(n);
        manager.addOpenHash(n);
      }

      manager.addNodeClosed(best);
      manager.removeNodeOpen(best);
      RenderUtil.addBlockToRenderSync(best.getBlockPos());

      i++;
      nodesConsidered++;
    }

    timeTaken = System.currentTimeMillis() - timeInit;
    isDone = true;
    return null;
  }
}
