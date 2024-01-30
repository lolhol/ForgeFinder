package com.finder.util;

import com.finder.ForgeFinder;
import net.minecraft.util.ChatComponentText;

public class ChatUtil {

  public static void sendChat(String msg) {
    ForgeFinder.MC.thePlayer.addChatMessage(new ChatComponentText(msg));
  }
}
