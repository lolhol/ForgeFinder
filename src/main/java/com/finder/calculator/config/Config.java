package com.finder.calculator.config;

import com.finder.calculator.cost.CostConst;
import com.finder.calculator.util.Callback;
import com.finder.calculator.util.CustomCosts;
import com.finder.calculator.util.Node;
import com.finder.util.BlockUtil;
import com.finder.util.MathUtil;
import com.finder.util.TimeUtil;
import net.minecraft.util.Vec3;

// For now this does not account for hypixel skyblock's high jump. Although i am not sure if it makes a difference.
public class Config {

  public Node start;
  public Node end;
  public int maxIter;
  public double blocksPerSecond;
  public Callback callback;
  public CustomCosts costs = null;
  public double walkerErrorCoef = 0;

  public Config(
    Vec3 startBlock,
    Vec3 endBlock,
    int maxIter,
    int blocksPerSecond,
    Callback callback
  ) {
    this.callback = callback;

    double dist = TimeUtil.getTimeReqMS(
      this.blocksPerSecond,
      MathUtil.distanceFromTo(startBlock, endBlock)
    );

    this.start = new Node(dist, null, BlockUtil.fromVecToBP(startBlock));
    this.end = new Node(dist, null, BlockUtil.fromVecToBP(endBlock));
    this.maxIter = maxIter;
    this.blocksPerSecond = blocksPerSecond;
    this.walkerErrorCoef = blocksPerSecond * blocksPerSecond;
  }

  public Config(
    Vec3 startBlock,
    Vec3 endBlock,
    int maxIter,
    int blocksPerSecond,
    Callback callback,
    CustomCosts costs
  ) {
    this.callback = callback;
    double dist = TimeUtil.getTimeReqMS(
      this.blocksPerSecond,
      MathUtil.distanceFromTo(startBlock, endBlock)
    );
    this.start = new Node(dist, null, BlockUtil.fromVecToBP(startBlock));
    this.end = new Node(dist, null, BlockUtil.fromVecToBP(endBlock));
    this.maxIter = maxIter;
    this.blocksPerSecond = blocksPerSecond;
    this.costs = costs;
    this.walkerErrorCoef = blocksPerSecond * blocksPerSecond;
  }

  // by default will be set to a slow val
  public Config(
    Vec3 startBlock,
    Vec3 endBlock,
    int maxIter,
    Callback callback
  ) {
    this.callback = callback;
    double dist = TimeUtil.getTimeReqMS(
      this.blocksPerSecond,
      MathUtil.distanceFromTo(startBlock, endBlock)
    );
    this.start = new Node(dist, null, BlockUtil.fromVecToBP(startBlock));
    this.end = new Node(dist, null, BlockUtil.fromVecToBP(endBlock));
    this.maxIter = maxIter;
    this.blocksPerSecond = CostConst.WALK_ONE_BLOCK_COST * 20;
    this.walkerErrorCoef = blocksPerSecond * blocksPerSecond;
  }
}
