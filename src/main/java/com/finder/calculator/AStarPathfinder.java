package com.finder.calculator;

import com.finder.calculator.config.Config;
import com.finder.calculator.errors.NoPathException;
import com.finder.calculator.util.Node;
import java.util.HashSet;
import java.util.PriorityQueue;
import net.minecraft.util.BlockPos;
import org.apache.commons.lang3.tuple.Triple;

public class AStarPathfinder {

  public long timeTaken = System.currentTimeMillis();
  public int nodesConsidered = 0;

  public void run(Config config) throws NoPathException {
    Node starRes = runStar(config);

    int tmp = nodesConsidered;
    nodesConsidered = 0;

    if (starRes == null) {
      throw new NoPathException("No Path Found!", tmp);
    }

    config.callback.finderDone(starRes.makeList(), timeTaken);
  }

  public Node runStar(Config config) {
    long timeInit = System.currentTimeMillis();

    PriorityQueue<Node> openSet = new PriorityQueue<>();
    HashSet<BlockPos> openHash = new HashSet<>();
    // PS: Idk if we need dis but ima assume that u cant .contains in priority queue fast enough...
    HashSet<BlockPos> closedSet = new HashSet<>();
    int i = 0;
    nodesConsidered = 0;
    openSet.add(config.start);
    openHash.add(config.start.blockPos);

    while (!openSet.isEmpty() && i < config.maxIter) {
      Node best = openSet.poll();

      //ChatUtil.sendChat(String.valueOf(i));

      if (config.end.blockPos.equals(best.blockPos)) {
        timeTaken = System.currentTimeMillis() - timeInit;
        return best;
      }

      closedSet.add(best.blockPos);

      for (Node n : best.genNodesAround(config.end)) {
        if (
          closedSet.contains(n.blockPos) || openHash.contains(n.blockPos)
        ) continue;

        boolean[] interactions = n.isAbleToInteract(n);
        if (!interactions[0] && !interactions[1] && !interactions[2]) continue;

        n.generateCostsForNode(config.end.blockPos, interactions);

        // Add custom costs
        if (config.costs != null) {
          Triple<Double, Double, Double> costs = config.costs.addCost(n);
          n.gCost += costs.getLeft();
          n.hCost += costs.getMiddle();
          n.totalCost += costs.getRight();
        }

        if (!openHash.contains(n.blockPos)) {
          openHash.add(n.blockPos);
          openSet.add(n);
          //RenderUtil.addBlockToRenderSync(n.blockPos);
        }
      }
      i++;
      nodesConsidered++;
    }

    timeTaken = System.currentTimeMillis() - timeInit;
    //ChatUtil.sendChat(String.valueOf(closedSet.size()));
    return null;
  }
}
