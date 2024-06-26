package com.finder.calculator;

import com.finder.calculator.config.Config;
import com.finder.calculator.errors.NoPathException;
import com.finder.calculator.util.BetterBlockPos;
import com.finder.calculator.util.Node;
import com.finder.calculator.util.NodeUtil;
import com.finder.calculator.util.set.SetManager;
import com.finder.util.ChatUtil;
import java.util.List;
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
    try {
      long timeInit = System.currentTimeMillis();
      BlockPos endBP = new BlockPos(config.end.x, config.end.y, config.end.z);

      //final NodeListManager manager = new NodeListManager(startBP);
      //final HashSet<BetterBlockPos> closedSet = new HashSet<>();
      final SetManager setManager = new SetManager(config.start.getBlockPos());

      PriorityQueue<Node> openSet = new PriorityQueue<>();

      setManager.updateOpenState(
        new BetterBlockPos(config.start.getPosInt()),
        true
      );
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

        for (BetterBlockPos n : best.genNodePosAround()) {
          if (setManager.isClosedNode(n) || setManager.isOpenNode(n)) continue;

          // NOTE: I the process of calc for mining and just walking without mining is very different in terms of calculations (i think)
          // The mining calc is much slower. Thus, instead of running it every time, we only run it when mine mode is
          // selected
          if (!config.canMineBlocks) {
            boolean[] interactions = NodeUtil.isAbleToInteract(
              new int[] { n.x, n.y, n.z },
              best
            );

            if (
              !interactions[0] && !interactions[1] && !interactions[2]
            ) continue;

            Node node = new Node(0, best, n);
            node.generateCostsForNode(
              endBP,
              interactions,
              config.blocksPerSecond
            );

            if (config.costs != null) {
              Triple<Double, Double, Double> costs = config.costs.addCost(node);
              node.totalCost +=
                costs.getRight() + costs.getLeft() + costs.getMiddle();
            }

            openSet.add(node);
          } else {
            List<List<BlockPos>> interactions =
              NodeUtil.getBlocksWithInteractions(
                new int[] { n.x, n.y, n.z },
                best
              );

            //ChatUtil.sendChat(interactions);

            if (
              interactions.get(0) == null &&
              interactions.get(1) == null &&
              interactions.get(2) == null
            ) continue;

            //RenderUtil.addBlockToRenderSync(new BlockPos(n.x, n.y, n.z));

            Node node = new Node(0, best, n);

            node.generateCostsForNode(
              endBP,
              config.blocksPerSecond,
              interactions
            );

            if (config.costs != null) {
              Triple<Double, Double, Double> costs = config.costs.addCost(node);
              node.totalCost +=
                costs.getRight() + costs.getLeft() + costs.getMiddle();
            }

            openSet.add(node);
          }

          setManager.updateOpenState(n, true);
          //
          //Thread.sleep(50);
        }

        setManager.add(best);
        setManager.updateOpenState(best, false);

        i++;
        nodesConsidered++;
      }

      timeTaken = System.currentTimeMillis() - timeInit;
      isDone = true;
    } catch (Exception e) {
      isDone = true;
      e.printStackTrace();
    }

    return null;
  }
}
