package com.finder.calculator;

import com.finder.calculator.config.Config;
import com.finder.calculator.errors.NoPathException;
import com.finder.calculator.util.Node;
import com.finder.calculator.util.NodeUtil;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

public class AStarPathfinder {

  public List<Node> path;
  public long timeTaken = System.currentTimeMillis();

  public void run(Config config) throws NoPathException {
    Node starRes = runStar(config);

    if (starRes == null) {
      throw new NoPathException("No Path Found!");
    }

    config.callback.finderDone(starRes.makeList(), timeTaken);
  }

  public Node runStar(Config config) {
    long timeInit = System.currentTimeMillis();

    PriorityQueue<Node> openSet = new PriorityQueue<>();
    // PS: Idk if we need dis but ima assume that u cant .contains in priority queue fast enough...
    HashSet<Node> openHash = new HashSet<>();
    HashSet<Node> closedSet = new HashSet<>();
    int i = 0;
    openSet.add(config.start);
    openHash.add(config.start);

    while (!openSet.isEmpty() && i < config.maxIter) {
      Node best = openSet.poll();

      if (config.end.blockPos.equals(best.blockPos)) {
        timeTaken = System.currentTimeMillis() - timeInit;
        return best;
      }

      closedSet.add(best);
      openHash.remove(best);

      for (Node n : best.genNodesAround(config.end)) {
        if (closedSet.contains(n) || !n.isAbleToInteract(n)) continue;

        if (!openHash.contains(n)) {
          openHash.add(n);
          openSet.add(n);
        }
      }

      i++;
    }

    timeTaken = System.currentTimeMillis() - timeInit;
    return null;
  }
}
