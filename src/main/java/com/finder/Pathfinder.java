package com.finder;

import com.finder.calculator.AStarPathfinder;
import com.finder.calculator.config.Config;
import com.finder.calculator.errors.NoPathException;
import com.finder.calculator.util.Node;
import com.finder.exec.PathExec;
import java.util.List;

public class Pathfinder {

  public final PathExec EXEC = new PathExec();
  public final AStarPathfinder CALCULATOR = new AStarPathfinder();

  private Thread thread = null;
  private boolean pathException = false;

  public void runAStar(Config config) {
    pathException = false;

    if (thread != null) {
      thread.stop();
      thread = null;
    }

    thread =
      new Thread(() -> {
        try {
          CALCULATOR.run(config);
        } catch (NoPathException e) {
          config.callback.finderNoPath();
        }
      });

    thread.start();
  }
}
