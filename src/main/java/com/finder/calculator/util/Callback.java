package com.finder.calculator.util;

import java.util.List;

public interface Callback {
  void finderDone(List<Node> path, long amountOfTimeTaken, int nodesConsidered);
  void finderNoPath(int nodesConsidered);
  void finderError();
}
