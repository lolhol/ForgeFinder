package com.finder.exec;

import com.finder.ForgeFinder;
import com.finder.calculator.util.Node;
import com.finder.util.ChatUtil;
import com.finder.util.KeyBindHandler;
import com.finder.util.MathUtil;
import com.finder.util.RotationUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PathExec {

  boolean isOnline = false;
  List<BlockPos> blocksOnPath = new ArrayList<>();
  Vec3 curVecGoing = null;
  int jumpPresses = 0;

  public void run(List<BlockPos> blocksOnPath, boolean state) {
    this.blocksOnPath = blocksOnPath;
    isOnline = state;
  }

  public void runWithDifList(List<Node> blocksOnPath, boolean state) {
    List<BlockPos> blocks = new ArrayList<>();
    for (Node n : blocksOnPath) {
      blocks.add(n.getBlockPos());
    }
    this.blocksOnPath = blocks;
    BlockPos first = this.blocksOnPath.get(0);
    this.curVecGoing =
      new Vec3(first.getX() + 0.5, first.getY(), first.getZ() + 0.5);
    isOnline = state;
  }

  @SubscribeEvent
  public void onRender(RenderWorldLastEvent event) {
    if (!isOnline || curVecGoing == null) return;
    double dist = MathUtil.distanceFromToXZ(
      ForgeFinder.MC.thePlayer.getPositionVector(),
      curVecGoing
    );

    RotationUtils.smoothLook(
      RotationUtils.getRotation(
        new Vec3(
          curVecGoing.xCoord,
          ForgeFinder.MC.thePlayer.posY + ForgeFinder.MC.thePlayer.eyeHeight,
          curVecGoing.zCoord
        )
      ),
      1
    );

    if (dist <= 0.5) {
      if (blocksOnPath.isEmpty()) {
        isOnline = false;
        KeyBindHandler.updateKeys(
          false,
          false,
          false,
          false,
          false,
          false,
          false,
          false
        );
        ChatUtil.sendChat("Done with path!");
      } else {
        BlockPos block = blocksOnPath.remove(0);
        this.curVecGoing =
          new Vec3(block.getX() + 0.5, block.getY(), block.getZ() + 0.5);
      }

      return;
    }

    HashSet<KeyBinding> neededKeyPresses = KeyBindHandler.getNeededKeyPresses(
      ForgeFinder.MC.thePlayer.getPositionVector(),
      curVecGoing
    );

    for (KeyBinding k : KeyBindHandler.getListKeybinds()) {
      KeyBinding.setKeyBindState(k.getKeyCode(), neededKeyPresses.contains(k));
    }

    ForgeFinder.MC.thePlayer.setSprinting(true);

    KeyBinding.setKeyBindState(
      ForgeFinder.MC.gameSettings.keyBindJump.getKeyCode(),
      ForgeFinder.MC.thePlayer.getPositionVector().yCoord <
      curVecGoing.yCoord &&
      dist < 1.5 &&
      ForgeFinder.MC.thePlayer.onGround
    );
  }
}
