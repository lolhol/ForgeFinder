package com.finder.util;

import com.finder.ForgeFinder;
import com.finder.cache.util.CacheState;
import com.finder.calculator.util.Node;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class BlockUtil {

  public static BlockPos fromVecToBP(Vec3 vec) {
    return new BlockPos(vec.xCoord, vec.yCoord, vec.zCoord);
  }

  public static Vec3 fromBPToVec(BlockPos bp) {
    return new Vec3(bp.getX(), bp.getY(), bp.getZ());
  }

  public static Block getBlock(BlockPos block) {
    return ForgeFinder.MC.theWorld.getBlockState(block).getBlock();
  }

  public static Vec3[] getFourPointsAbout(
    Vec3 vec1,
    Vec3 vec2,
    double distBetween
  ) {
    double d1 = vec2.xCoord - vec1.xCoord;
    double d2 = vec2.zCoord - vec1.zCoord;

    double dist = Math.sqrt(d1 * d1 + d2 * d2);

    double revX = -d2;
    double revY = d1;

    double X = (revX / dist) * distBetween;
    double Y = (revY / dist) * distBetween;

    Vec3[] vecs = new Vec3[4];

    vecs[0] = new Vec3(vec1.xCoord - X, vec1.yCoord, vec1.zCoord - Y);
    vecs[2] = new Vec3(X + vec1.xCoord, vec1.yCoord, Y + vec1.zCoord);

    vecs[1] = new Vec3(vec2.xCoord - X, vec2.yCoord, vec2.zCoord - Y);
    vecs[3] = new Vec3(X + vec2.xCoord, vec2.yCoord, Y + vec2.zCoord);

    return vecs;
  }

  public static boolean isBlockSolid(BlockPos block) {
    //RenderUtil.addBlockToRenderSync(new BlockPos(n.x, n.y, n.z));
    //ChatUtil.sendChat("!!!");
    boolean res =
      ForgeFinder.CACHE_MANAGER.getBlockInfoCached(
        block.getX(),
        block.getY(),
        block.getZ()
      ) ==
      CacheState.EXISTS_YES;

    if (!res) {
      //RenderUtil.addBlockToRenderSync(block);
    }

    return (res);
    /*Block blockType = getBlock(block);
    return (
      blockType != Blocks.water &&
      blockType != Blocks.lava &&
      blockType != Blocks.air &&
      blockType != Blocks.red_flower &&
      blockType != Blocks.tallgrass &&
      blockType != Blocks.yellow_flower &&
      blockType != Blocks.double_plant &&
      blockType != Blocks.flowing_water
    );*/
  }

  public static boolean isBlockWalkable(BlockPos block) {
    return (
      ForgeFinder.CACHE_MANAGER.getBlockInfoCached(
        block.getX(),
        block.getY(),
        block.getZ()
      ) ==
      CacheState.EXISTS_NO
    );
    /*Block blockType = getBlock(block);
    return (
      blockType == Blocks.air ||
      blockType == Blocks.red_flower ||
      blockType == Blocks.tallgrass ||
      blockType == Blocks.yellow_flower ||
      blockType == Blocks.double_plant
    );*/
  }

  public static BlockPos bresenham(Vec3 start, Vec3 end) {
    int x1 = MathHelper.floor_double(end.xCoord);
    int y1 = MathHelper.floor_double(end.yCoord);
    int z1 = MathHelper.floor_double(end.zCoord);
    int x0 = MathHelper.floor_double(start.xCoord);
    int y0 = MathHelper.floor_double(start.yCoord);
    int z0 = MathHelper.floor_double(start.zCoord);

    if (isBlockSolid(new BlockPos(x0, y0, z0))) {
      return new BlockPos(x0, y0, z0);
    }

    int iterations = 200;

    while (iterations-- >= 0) {
      //RenderMultipleBlocksMod.renderMultipleBlocks(BlockUtils.fromBPToVec(new BlockPos(x0, y0, z0)), true);
      if (x0 == x1 && y0 == y1 && z0 == z1) {
        return null; //new BlockPos(end);
      }

      boolean hasNewX = true;
      boolean hasNewY = true;
      boolean hasNewZ = true;

      double newX = 999.0;
      double newY = 999.0;
      double newZ = 999.0;

      if (x1 > x0) {
        newX = (double) x0 + 1.0;
      } else if (x1 < x0) {
        newX = (double) x0 + 0.0;
      } else {
        hasNewX = false;
      }
      if (y1 > y0) {
        newY = (double) y0 + 1.0;
      } else if (y1 < y0) {
        newY = (double) y0 + 0.0;
      } else {
        hasNewY = false;
      }
      if (z1 > z0) {
        newZ = (double) z0 + 1.0;
      } else if (z1 < z0) {
        newZ = (double) z0 + 0.0;
      } else {
        hasNewZ = false;
      }

      double stepX = 999.0;
      double stepY = 999.0;
      double stepZ = 999.0;

      double dx = end.xCoord - start.xCoord;
      double dy = end.yCoord - start.yCoord;
      double dz = end.zCoord - start.zCoord;

      if (hasNewX) {
        stepX = (newX - start.xCoord) / dx;
      }
      if (hasNewY) {
        stepY = (newY - start.yCoord) / dy;
      }
      if (hasNewZ) {
        stepZ = (newZ - start.zCoord) / dz;
      }
      if (stepX == -0.0) {
        stepX = -1.0E-4;
      }
      if (stepY == -0.0) {
        stepY = -1.0E-4;
      }
      if (stepZ == -0.0) {
        stepZ = -1.0E-4;
      }

      EnumFacing enumfacing;
      if (stepX < stepY && stepX < stepZ) {
        enumfacing = x1 > x0 ? EnumFacing.WEST : EnumFacing.EAST;
        start =
          new Vec3(newX, start.yCoord + dy * stepX, start.zCoord + dz * stepX);
      } else if (stepY < stepZ) {
        enumfacing = y1 > y0 ? EnumFacing.DOWN : EnumFacing.UP;
        start =
          new Vec3(start.xCoord + dx * stepY, newY, start.zCoord + dz * stepY);
      } else {
        enumfacing = z1 > z0 ? EnumFacing.NORTH : EnumFacing.SOUTH;
        start =
          new Vec3(start.xCoord + dx * stepZ, start.yCoord + dy * stepZ, newZ);
      }
      x0 =
        MathHelper.floor_double(start.xCoord) -
        (enumfacing == EnumFacing.EAST ? 1 : 0);
      y0 =
        MathHelper.floor_double(start.yCoord) -
        (enumfacing == EnumFacing.UP ? 1 : 0);
      z0 =
        MathHelper.floor_double(start.zCoord) -
        (enumfacing == EnumFacing.SOUTH ? 1 : 0);

      //RenderMultipleBlocksMod.renderMultipleBlocks(BlockUtils.fromBPToVec(new BlockPos(x0, y0, z0)), true);
      BlockPos bp = new BlockPos(x0, y0, z0);
      if (
        isBlockSolid(bp) && !getBlock(bp).getRegistryName().contains("slab")
      ) {
        return new BlockPos(x0, y0, z0);
      }
    }

    return null;
  }

  public static List<Node> shortenPath(List<Node> init) {
    List<Node> newList = new ArrayList<>();
    //newList.add(init.get(init.size() - 1));
    Node curNode = null;
    for (Node n : init) {
      if (curNode == null) {
        newList.add(n);
        curNode = n;
        continue;
      }

      Vec3 vecCurNode = new Vec3(curNode.x + 0.5, curNode.y, curNode.z + 0.5);

      Vec3 vecN = new Vec3(n.x + 0.5, n.y, n.z + 0.5);

      if (n.y != curNode.y || bresenham(vecCurNode, vecN) != null) {
        newList.add(n);
        curNode = n;
      }
    }

    return newList;
  }

  public static boolean isOnSide(int[] block1, int[] block2) {
    return (
      MathUtil.absAbsSubtract(block1[0], block2[0]) >= 1 &&
      MathUtil.absAbsSubtract(block1[2], block2[2]) >= 1
    );
  }

  public static boolean isClearOnSides(int[] bp1, int[] bp2) {
    double changeX = bp2[0] - bp1[0];
    double changeZ = bp2[2] - bp1[2];

    BlockPos blockPos = new BlockPos(bp1[0], bp1[1], bp1[2]);
    //NodeUtil
    return (
      !BlockUtil.isBlockSolid(blockPos.add(0, 0, changeZ)) &&
      !BlockUtil.isBlockSolid(blockPos.add(changeX, 0, 0)) &&
      !BlockUtil.isBlockSolid(blockPos.add(0, 1, changeZ)) &&
      !BlockUtil.isBlockSolid(blockPos.add(changeX, 1, 0))
    );
  }
}
