package com.finder.ct;

import com.finder.util.FileUtil;
import java.io.File;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CTPortManager {

  private boolean isOnline;
  private final File MCFolder = new File(
    System.getProperty("user.home") + ".minecraft"
  );

  private final File CTFileFolder = new File(
    MCFolder.getPath() + File.separator + "ForgeFinder"
  );
  private final File CTFileComm = new File(
    CTFileFolder.getPath() + File.separator + "CTJavaComm.txt"
  );

  public CTPortManager() {
    if (!CTFileFolder.exists()) {
      CTFileFolder.mkdir();
    }

    if (!CTFileComm.exists()) {
      try {
        CTFileComm.createNewFile();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    try {
      // write base text to file to signify that Forge Finder is ready to intake commands
      FileUtil.writeToFile("FF" + "\n" + "ready", CTFileComm);
    } catch (Exception e) {
      e.printStackTrace();
    }

    isOnline = true;
  }

  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (!isOnline) return;

    final String[] received = isRecieved();
    final CTPortCommMsgType type = FileUtil.parseCTPortCommMsgType(received);
    if (type == CTPortCommMsgType.UNREADABLE) return;
    // more code later here
  }

  public String[] isRecieved() {
    try {
      return FileUtil.readFile(CTFileComm).split("\n");
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new String[] { "" };
  }
}
