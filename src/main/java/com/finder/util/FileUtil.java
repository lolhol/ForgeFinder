package com.finder.util;

import com.finder.ct.CTPortCommMsgType;
import java.io.*;
import java.util.Objects;

public class FileUtil {

  public static void writeToFile(String text, File file) throws IOException {
    FileWriter fileWriter = new FileWriter(file);
    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    bufferedWriter.write(text);
  }

  public static String readFile(String filePath) throws IOException {
    return readFile(new File(filePath));
  }

  public static String readFile(File filePath) throws IOException {
    StringBuilder content = new StringBuilder();

    try (
      BufferedReader bufferedReader = new BufferedReader(
        new FileReader(filePath)
      )
    ) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        content.append(line).append(System.lineSeparator());
      }
    }

    return content.toString();
  }

  public static CTPortCommMsgType parseCTPortCommMsgType(String[] msg) {
    if (!Objects.equals(msg[0], "FF")) return CTPortCommMsgType.UNREADABLE;

    switch (msg[1]) {
      case "ready":
        return CTPortCommMsgType.READY;
      case "start_pathing_pos":
        return CTPortCommMsgType.START_PATHING_POS;
      case "start_pathing_block":
        return CTPortCommMsgType.START_PATHING_BLOCK;
      case "abort_pathing":
        return CTPortCommMsgType.ABORT_PATHING;
      case "get_pathing_progress":
        return CTPortCommMsgType.GET_PATHING_PROGRESS;
      case "get_path":
        return CTPortCommMsgType.GET_PATH;
      default:
        return CTPortCommMsgType.UNREADABLE;
    }
  }
}
