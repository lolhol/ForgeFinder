package com.finder.calculator.util;

import java.util.List;

public interface Callback {
  void finderDone(List<Node> path, long amtTime);
  void finderNoPath();
  // impl l8tr?
  // void finderError();
}
