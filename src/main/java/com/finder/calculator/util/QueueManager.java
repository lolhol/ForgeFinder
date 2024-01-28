package com.finder.calculator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class QueueManager {

  List<PriorityQueue<Node>> queues = new ArrayList<>();

  public QueueManager(int maxNumberQueues) {
    for (int i = 0; i <= maxNumberQueues; i++) {
      queues.add(new PriorityQueue<>());
    }
  }

  public void add(Node node) {
    int best = Integer.MAX_VALUE;
    int posBest = 0;
    for (int i = 0; i < queues.size(); i++) {
      PriorityQueue<Node> cur = queues.get(i);
      Node bestCurNode = cur.peek();
      if (bestCurNode == null) {
        cur.add(node);
        return;
      }

      int curBest = cur.size() + node.compareTo(bestCurNode);
      if (curBest < best) {
        best = curBest;
        posBest = i;
      }
    }

    queues.get(posBest).add(node);
  }

  public Node getBest() {
    Node bestCur = null;
    int posBest = 0;
    int i = 0;

    for (PriorityQueue<Node> cur : queues) {
      i++;
      if (cur.isEmpty()) continue;
      Node peeked = cur.peek();
      if (bestCur == null || bestCur.compareTo(peeked) < 0) {
        bestCur = peeked;
        posBest = i;
      }
    }

    queues.get(i).poll();
    return bestCur;
  }

  public void clear() {
    queues.clear();
  }

  /**
   * @param pos position
   * @return
   * @apiNote for debug
   */
  public int getLen(int pos) {
    return queues.get(pos).size();
  }
}
