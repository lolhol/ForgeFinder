package com.finder.calculator.config;

import com.finder.calculator.util.Callback;
import com.finder.calculator.util.CustomCosts;
import com.finder.calculator.util.Node;
import com.finder.util.BlockUtil;
import net.minecraft.util.Vec3;

public class Config extends BlockUtil {

  public Node start;
  public Node end;
  public int maxIter;
  public int blocksPerSecond;
  public Callback callback;
  public CustomCosts costs = null;

  public Config(
    Vec3 startBlock,
    Vec3 endBlock,
    int maxIter,
    int blocksPerSecond,
    Callback callback
  ) {
    this.callback = callback;
    double dist = getTimeReqMS(
      this.blocksPerSecond,
      distanceFromTo(startBlock, endBlock)
    );
    this.start =
      new Node(0, dist, dist, null, fromVecToBP(startBlock), blocksPerSecond);
    this.end =
      new Node(dist, 0, dist, null, fromVecToBP(endBlock), blocksPerSecond);
    this.maxIter = maxIter;
    this.blocksPerSecond = blocksPerSecond;
  }

  public Config(
    Vec3 startBlock,
    Vec3 endBlock,
    int maxIter,
    int blocksPerSecond,
    Callback callback,
    CustomCosts consts
  ) {
    this.callback = callback;
    double dist = getTimeReqMS(
      this.blocksPerSecond,
      distanceFromTo(startBlock, endBlock)
    );
    this.start =
      new Node(0, dist, dist, null, fromVecToBP(startBlock), blocksPerSecond);
    this.end =
      new Node(dist, 0, dist, null, fromVecToBP(endBlock), blocksPerSecond);
    this.maxIter = maxIter;
    this.blocksPerSecond = blocksPerSecond;
  }
}
