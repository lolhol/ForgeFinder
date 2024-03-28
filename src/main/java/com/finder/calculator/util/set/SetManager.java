package com.finder.calculator.util.set;

import com.finder.calculator.util.BetterBlockPos;
import com.finder.calculator.util.Node;
import com.finder.util.ChunkPosByte;
import com.finder.util.MathUtil;
import java.util.HashMap;
import net.minecraft.util.BlockPos;

// FIXME: chunk is 16 BLOCK WIDE!!! NOT 8!
public class SetManager {

  private final HashMap<ChunkPosByte, SetChunk> chunks = new HashMap<>();

  // above the max of 256 chunks. IK IK this not gon be efficient if you have > 256 chunks pathing but i mean... we can jst copy
  // the chunks when we get to > 256 i mean ehhh thats like 65k chunks worth of data like bruh...

  /**
   * @TM soon
   */
  private HashMap<int[], SetChunk> chunksAb256 = new HashMap<>();

  public SetManager(BlockPos startPosition) {}

  public void add(Node node) {
    ChunkPosByte nodePosChunk = getPositionChunk(node);
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

  public ChunkPosByte getPositionChunk(Node node) {
    return new ChunkPosByte(
      new byte[] {
        (byte) (MathUtil.getPositionChunk(node.x)),
        (byte) (MathUtil.getPositionChunk(node.z)),
      }
    );
  }

  public ChunkPosByte getPositionChunk(BetterBlockPos node) {
    return new ChunkPosByte(
      new byte[] {
        (byte) (MathUtil.getPositionChunk(node.x)),
        (byte) (MathUtil.getPositionChunk(node.z)),
      }
    );
  }

  public boolean isClosedNode(BetterBlockPos bp) {
    ChunkPosByte chunkPositionByte = getPositionChunk(bp);
    if (chunks.containsKey(chunkPositionByte)) {
      return chunks.get(chunkPositionByte).isClosed(bp);
    }

    return false;
  }

  public void updateOpenState(BetterBlockPos bp, boolean state) {
    ChunkPosByte nodePosChunk = getPositionChunk(bp);
    if (chunks.containsKey(nodePosChunk)) {
      chunks.get(nodePosChunk).addOpen(bp, state);
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
    ChunkPosByte nodePosChunk = getPositionChunk(bp);
    if (chunks.containsKey(nodePosChunk)) {
      chunks.get(nodePosChunk).addOpen(bp, state);
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
    ChunkPosByte chunkPositionByte = getPositionChunk(bp);
    if (chunks.containsKey(chunkPositionByte)) {
      return chunks.get(chunkPositionByte).isOpen(bp);
    }

    return false;
  }
}
