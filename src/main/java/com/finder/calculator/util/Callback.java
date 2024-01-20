package com.finder.calculator.util;

import java.util.List;

public interface Callback {
  void finderDone(List<Node> path, long amountOfTimeTaken);
  void finderNoPath(int nodesConsidered);
  // impl l8tr?
  // void finderError();
}
