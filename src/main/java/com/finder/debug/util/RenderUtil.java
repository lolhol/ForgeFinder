package com.finder.debug.util;

import com.finder.util.Render;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderUtil {

  private static final List<BlockPos> blocksList = Collections.synchronizedList(
    new ArrayList<>()
  );

  public static void addBlockToRenderSync(BlockPos block) {
    synchronized (blocksList) {
      blocksList.add(block);
    }
  }

  public static void clearSync() {
    synchronized (blocksList) {
      blocksList.clear();
    }
  }

  @SubscribeEvent
  public void onRender(RenderWorldLastEvent event) {
    synchronized (blocksList) {
      for (BlockPos b : blocksList) {
        Render.drawBox(
          b.getX(),
          b.getY(),
          b.getZ(),
          Color.BLUE,
          0.5F,
          event.partialTicks
        );
      }
    }
  }
}
