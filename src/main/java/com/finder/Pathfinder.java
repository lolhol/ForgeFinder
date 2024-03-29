package com.finder;

import com.finder.calculator.AStarPathfinder;
import com.finder.calculator.config.Config;
import com.finder.calculator.errors.NoPathException;
import com.finder.exec.PathExec;

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
          config.callback.finderNoPath(e.nodesConsidered);
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
          config.callback.finderNoPath(e.nodesConsidered);
        }
      });

    thread.start();
  }
}
