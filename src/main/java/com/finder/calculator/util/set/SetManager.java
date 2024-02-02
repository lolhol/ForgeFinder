package com.finder.calculator.util.set;

import com.finder.calculator.util.BetterBlockPos;
import com.finder.calculator.util.Node;
import com.finder.util.MathUtil;
import java.util.HashMap;
import net.minecraft.util.BlockPos;

public class SetManager {

  private final int[] START_CHUNK_POS;
  private final HashMap<ChunkPos, SetChunk> chunks = new HashMap<>();

  // above the max of 256 chunks. IK IK this not gon be efficient if you have > 256 chunks pathing but i mean... we can jst copy
  // the chunks when we get to > 256 i mean ehhh thats like 65k chunks worth of data like bruh...

  /**
   * @TM soon
   */
  private HashMap<int[], SetChunk> chunksAb256 = new HashMap<>();

  public SetManager(BlockPos startPosition) {
    START_CHUNK_POS =
      new int[] {
        MathUtil.getPositionChunk(startPosition.getX()),
        MathUtil.getPositionChunk(startPosition.getZ()),
      };
  }

  public void add(Node node) {
    ChunkPos nodePosChunk = getPositionChunk(node);
    if (chunks.containsKey(nodePosChunk)) {
      chunks.get(nodePosChunk).addClosed(node, true);
    } else {
      chunks.put(
        nodePosChunk,
        new SetChunk(
          new int[] {
            MathUtil.getPositionChunk(node.x) << 4,
            MathUtil.getPositionChunk(node.z) << 4,
          },
          node
        )
      );
      /*ChatUtil.sendChat(
        String.valueOf(chunks.containsKey(getPositionChunk(node)))
      );*/
    }
  }

  public ChunkPos getPositionChunk(Node node) {
    return new ChunkPos(
      new byte[] {
        (byte) (MathUtil.getPositionChunk(node.x) - START_CHUNK_POS[0]),
        (byte) (MathUtil.getPositionChunk(node.z) - START_CHUNK_POS[1]),
      }
    );
  }

  public ChunkPos getPositionChunk(BetterBlockPos node) {
    return new ChunkPos(
      new byte[] {
        (byte) (MathUtil.getPositionChunk(node.x) - START_CHUNK_POS[0]),
        (byte) (MathUtil.getPositionChunk(node.z) - START_CHUNK_POS[1]),
      }
    );
  }

  public boolean isClosedNode(BetterBlockPos bp) {
    ChunkPos chunkPosition = getPositionChunk(bp);
    if (chunks.containsKey(chunkPosition)) {
      return chunks.get(chunkPosition).isClosed(bp);
    }

    return false;
  }

  public void updateOpenState(BetterBlockPos bp, boolean state) {
    ChunkPos nodePosChunk = getPositionChunk(bp);
    if (chunks.containsKey(nodePosChunk)) {
      chunks.get(nodePosChunk).addOpen(bp, true);
    } else {
      SetChunk chunk = new SetChunk(
        new int[] {
          MathUtil.getPositionChunk(bp.x) << 4,
          MathUtil.getPositionChunk(bp.z) << 4,
        }
      );
      chunks.put(nodePosChunk, chunk);
      chunk.addOpen(bp, state);
    }
  }

  public void updateOpenState(Node bp, boolean state) {
    ChunkPos nodePosChunk = getPositionChunk(bp);
    if (chunks.containsKey(nodePosChunk)) {
      chunks.get(nodePosChunk).addOpen(bp, true);
    } else {
      SetChunk chunk = new SetChunk(
        new int[] {
          MathUtil.getPositionChunk(bp.x) << 4,
          MathUtil.getPositionChunk(bp.z) << 4,
        }
      );
      chunks.put(nodePosChunk, chunk);
      chunk.addOpen(bp, state);
    }
  }

  public boolean isOpenNode(BetterBlockPos bp) {
    ChunkPos chunkPosition = getPositionChunk(bp);
    if (chunks.containsKey(chunkPosition)) {
      return chunks.get(chunkPosition).isOpen(bp);
    }

    return false;
  }
}
