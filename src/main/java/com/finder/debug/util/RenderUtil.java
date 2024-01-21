package com.finder.debug.util;

import com.finder.util.Render;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderUtil {

  private static final List<BlockPos> blocksList = Collections.synchronizedList(
    new ArrayList<>()
  );

  private static final List<Tuple<Vec3, Vec3>> linesToRender =
    Collections.synchronizedList(new ArrayList<>());

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

  public static void renderLineSync(Vec3 start, Vec3 end) {
    synchronized (linesToRender) {
      linesToRender.add(new Tuple<>(start, end));
    }
  }

  public static void clearLinesSync() {
    synchronized (linesToRender) {
      linesToRender.clear();
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

    synchronized (linesToRender) {
      for (Tuple<Vec3, Vec3> a : linesToRender) {
        Render.drawLine(
          a.getFirst(),
          a.getSecond(),
          0.5F,
          Color.RED,
          event.partialTicks
        );
      }
    }
  }
}
