package com.finder.debug;

import com.finder.ForgeFinder;
import com.finder.Pathfinder;
import com.finder.calculator.config.Config;
import com.finder.calculator.util.Callback;
import com.finder.calculator.util.Node;
import com.finder.debug.util.RenderUtil;
import com.finder.exec.PathExec;
import com.finder.util.ChatUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Test1 {

  private static final Pathfinder finder = new Pathfinder();
  private static final PathExec executer = new PathExec();

  public Test1() {
    MinecraftForge.EVENT_BUS.register(executer);
  }

  @SubscribeEvent
  public void onChatReceived(ClientChatReceivedEvent event) {
    String message = event.message.getFormattedText();

    if (message.contains("/makePath ")) {
      Pattern pattern = Pattern.compile("-?\\d+");
      Matcher matcher = pattern.matcher(message);

      List<Integer> ints = new ArrayList<>();
      while (matcher.find()) {
        String number = matcher.group();
        ints.add(Integer.valueOf(number));
      }

      ChatUtil.sendChat(
        "Running finder for point: " +
        ints.get(1) +
        ", " +
        ints.get(2) +
        ", " +
        ints.get(3) +
        "!"
      );

      RenderUtil.clearSync();
      RenderUtil.clearLinesSync();

      //ChatUtil.sendChat(message);

      finder.runAStar(
        new Config(
          ForgeFinder.MC.thePlayer.getPositionVector(),
          new Vec3(ints.get(1), ints.get(2), ints.get(3)),
          10000,
          5,
          new Callback() {
            @Override
            public void finderDone(List<Node> path, long amtTime) {
              //RenderUtil.clear();
              for (Node node : path) {
                RenderUtil.addBlockToRenderSync(node.blockPos);
              }

              executer.runWithDifList(path, true);

              ChatUtil.sendChat(
                "Path found with length: " +
                path.size() +
                ". Took: " +
                amtTime +
                "ms."
              );
            }

            @Override
            public void finderNoPath(int nodesConsidered) {
              //RenderUtil.clear();
              ChatUtil.sendChat(
                "No Path Found! Nodes considered: " + nodesConsidered
              );
            }
          }
        )
      );
    }
  }
  //./makePath -196 64 304
  // pc:
  //
  //./makePath -75 65 296
}
