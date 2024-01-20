package com.finder.util;

import com.finder.ForgeFinder;

public class ChatUtil {

  public static void sendChat(String msg) {
    ForgeFinder.MC.thePlayer.sendChatMessage(msg);
  }
}
