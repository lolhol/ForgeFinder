package com.finder;

import com.finder.calculator.config.Config;
import com.finder.calculator.util.Callback;
import com.finder.calculator.util.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "ForgeFinder", useMetadata = true)
public class ForgeFinder {

  public static final Minecraft MC = Minecraft.getMinecraft();
  private static final Pathfinder finder = new Pathfinder();

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(this);
  }

  // PS: ik theres the Command base from the essentials but i dont have wifi atm so :/
  @SubscribeEvent
  public void onChatReceived(ClientChatReceivedEvent event) {
    String message = event.message.getFormattedText();

    if (message.contains("/makePath ")) {
      Pattern pattern = Pattern.compile("\\d+");
      Matcher matcher = pattern.matcher(message);

      List<Integer> ints = new ArrayList<>();
      while (matcher.find()) {
        String number = matcher.group();
        ints.add(Integer.valueOf(number));
      }

      finder.runAStar(
        new Config(
          MC.thePlayer.getPositionVector(),
          new Vec3(ints.get(0), ints.get(1), ints.get(2)),
          1000,
          5,
          new Callback() {
            @Override
            public void finderDone(List<Node> path, long amtTime) {
              // smh
            }

            @Override
            public void finderNoPath() {
              // mh
            }
          }
        )
      );
    }
  }
}
