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
      FileUtil.writeToFile("FF" + "\n" + "true", CTFileComm);
    } catch (Exception e) {
      e.printStackTrace();
    }

    isOnline = true;
  }

  @SubscribeEvent
  public void onTick(TickEvent.ClientTickEvent event) {
    if (!isOnline) return;

    final String recieved = isRecieved();
    if (recieved == null) return;
  }

  public String isRecieved() {
    try {
      return FileUtil.readFile(CTFileComm);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
}
