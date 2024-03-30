package com.finder.util;

import java.io.*;

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
}
