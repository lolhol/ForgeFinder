package com.finder.exec;

import com.finder.ForgeFinder;
import com.finder.util.KeyBindHandler;
import com.finder.util.MathUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PathExec {

  boolean isOnline = false;
  final MathUtil MATH = new MathUtil();
  List<BlockPos> blocksOnPath = new ArrayList<>();
  Vec3 curVecGoing = null;

  public void run(List<BlockPos> blocksOnPath, boolean state) {
    this.blocksOnPath = blocksOnPath;
    isOnline = state;
  }

  @SubscribeEvent
  public void onRender(RenderWorldLastEvent event) {
    if (!isOnline || curVecGoing == null) return;
    if (
      MATH.distanceFromToXZ(
        ForgeFinder.MC.thePlayer.getPositionVector(),
        curVecGoing
      ) <=
      0.5
    ) {
      if (blocksOnPath.isEmpty()) {
        isOnline = false;
      } else {
        BlockPos block = blocksOnPath.remove(0);
        this.curVecGoing =
          new Vec3(block.getX() + 0.5, block.getY(), block.getZ() + 0.5);
      }

      return;
    }
  }
}
