package com.finder.calculator.util.set;

import com.finder.calculator.util.Node;
import com.finder.util.MathUtil;
import java.util.HashMap;
import net.minecraft.util.BlockPos;

public class ClosedSetManager {

  private final int[] START_CHUNK_POS;
  private final HashMap<byte[], ClosedSetChunk> chunks = new HashMap<>();

  // above the max of 256 chunks. IK IK this not gon be efficient if you have > 256 chunks pathing but i mean... we can jst copy
  // the chunks when we get to > 256 i mean ehhh thats like 65k chunks worth of data like bruh...

  /**
   * @TM soon
   */
  private HashMap<int[], ClosedSetChunk> chunksAb256 = new HashMap<>();

  public ClosedSetManager(BlockPos startPosition) {
    START_CHUNK_POS =
      new int[] {
        MathUtil.getPositionChunk(startPosition.getX()),
        MathUtil.getPositionChunk(startPosition.getZ()),
      };
  }

  public void add(Node node) {
    byte[] nodePosChunk = new byte[] {
      (byte) (MathUtil.getPositionChunk(node.x) - START_CHUNK_POS[0]),
      (byte) (MathUtil.getPositionChunk(node.z) - START_CHUNK_POS[1]),
    };

    BlockPos nodeBP = node.getBlockPos();
    if (chunks.containsKey(nodePosChunk)) {
      chunks.get(nodePosChunk).add(nodeBP, true);
    } else {
      chunks.put(
        nodePosChunk,
        new ClosedSetChunk(
          new int[] {
            MathUtil.getPositionChunk(node.x),
            MathUtil.getPositionChunk(node.z),
          },
          nodeBP
        )
      );
    }
  }
}
