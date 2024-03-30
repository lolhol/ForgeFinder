package com.finder;

import com.finder.calculator.AStarPathfinder;
import com.finder.calculator.config.Config;
import com.finder.calculator.errors.NoPathException;
import com.finder.exec.PathExec;
import net.minecraft.util.Vec3;

public class Pathfinder {

  public final PathExec EXEC = new PathExec();
  public final AStarPathfinder CALCULATOR = new AStarPathfinder();

  private Thread thread = null;

  public void runAStar(Config config) {
    if (thread != null) {
      //thread.stop();
      thread = null;
    }

    thread =
      new Thread(() -> {
        try {
          CALCULATOR.run(config);
        } catch (NoPathException e) {
          if (config.callback != null) config.callback.finderNoPath(
            e.nodesConsidered
          );
        }
      });

    thread.start();
  }

  public void runAStar(Config config, boolean isPrint) {
    if (thread != null) {
      //thread.stop();
      thread = null;
    }

    thread =
      new Thread(() -> {
        try {
          CALCULATOR.run(config, isPrint);
        } catch (NoPathException e) {
          if (config.callback != null) config.callback.finderNoPath(
            e.nodesConsidered
          );
        }
      });

    thread.start();
  }

  public void runAStar(
    double[] startBlock,
    double[] endBlock,
    int maxIter,
    int blocksPerSecond,
    boolean canMineBlocks,
    boolean isPrint
  ) {
    if (thread != null) {
      thread = null;
    }

    Config conf = new Config(
      new Vec3(startBlock[0], startBlock[1], startBlock[2]),
      new Vec3(endBlock[0], endBlock[1], endBlock[2]),
      maxIter,
      blocksPerSecond,
      canMineBlocks
    );

    thread =
      new Thread(() -> {
        try {
          CALCULATOR.run(conf, isPrint);
        } catch (NoPathException e) {
          if (conf.callback != null) conf.callback.finderNoPath(
            e.nodesConsidered
          );
        }
      });

    thread.start();
  }
}
