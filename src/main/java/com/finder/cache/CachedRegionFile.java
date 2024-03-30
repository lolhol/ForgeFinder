package com.finder.cache;

import com.finder.ForgeFinder;
import com.finder.calculator.util.BetterBlockPos;
import com.finder.util.ChunkPosInt;
import com.finder.util.FileUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @apiNote this is used as an interface for the file writing and reading
 * @fileNotation => -1 == null, \n == separator
 */
public class CachedRegionFile {

  public final BetterBlockPos positionRelative;
  public final BetterBlockPos positionChunkOrigin;
  public boolean storeLocaly;

  private final File MCFolder = new File(
    System.getProperty("user.home") + ".minecraft"
  );

  private final File dataFileFolder = new File(
    MCFolder.getPath() + File.separator + "ForgeFinderData"
  );
  private final File filePath;

  /**
   * @ChunkPosInt the the chunk position in the world (relative)
   * @Integer[] the starting/ending char in the file
   */
  public final Map<ChunkPosInt, Integer[]> globalChunkStorage = new HashMap<>();
  public final CachedChunk[] localData = new CachedChunk[256];

  /**
   * @param positionRelative such as [1, 1] one 1 = 16 x 16 chunks
   */
  public CachedRegionFile(
    BetterBlockPos positionRelative,
    BetterBlockPos positionCenterBlockPos
  ) {
    this.positionRelative = positionRelative;
    this.positionChunkOrigin = positionCenterBlockPos;

    filePath =
      new File(
        dataFileFolder.getPath() +
        File.separator +
        "ff_" +
        ForgeFinder.MC.theWorld.getProviderName() +
        "_" +
        positionRelative.x +
        "-" +
        positionRelative.z
      );

    if (!dataFileFolder.exists()) {
      dataFileFolder.mkdir();
    }

    if (!filePath.exists()) {
      try {
        filePath.createNewFile();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void addChunkToRegion(ChunkPosInt chunkPos, CachedChunk chunk) {
    localData[getPositionFromChunkPos(chunkPos)] = chunk;
  }

  private int getPositionFromChunkPos(ChunkPosInt chunk) {
    int xPosInner = positionChunkOrigin.x - chunk.x;
    int zPosInner = positionChunkOrigin.z - chunk.y;

    return xPosInner * 16 + zPosInner;
  }

  public void resyncData() {
    try {
      String fileContent = FileUtil.readFile(filePath);
      String[] result = fileContent.split("\n");

      int cCount = 0;
      // TODO: fix this shit
      // This gotta be the shittiest code in the whole world
      // I literally got dementia because of this shit
      for (String curStr : result) {
        if (curStr.charAt(0) == '/') {
          localData[cCount] = null;
        } else {
          ChunkPosInt chunkPos = new ChunkPosInt(0, 0);

          int i = 0;
          StringBuilder intString = new StringBuilder();
          while (curStr.charAt(i) != ' ') {
            intString.append(curStr.charAt(i));
            i++;
          }
          i++;

          int resultInt = Integer.parseInt(intString.toString());
          chunkPos.x = resultInt;

          intString = new StringBuilder();
          while (curStr.charAt(i) != ' ') {
            intString.append(curStr.charAt(i));
            i++;
          }
          i++;

          resultInt = Integer.parseInt(intString.toString());
          chunkPos.x = resultInt;

          int chunkPos2D = getPositionFromChunkPos(chunkPos);

          BitSet bits = new BitSet();
          for (int j = i; j < curStr.length(); j++) {
            bits.set(256 * 16 * 16);
          }
          //localData[chunkPos2D];
        }

        for (char c : curStr.toCharArray()) {}
        //CachedChunk curChunk = new CachedChunk();
        cCount++;
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void writeToFileData() {
    resyncData();

    try (
      FileWriter fileWriter = new FileWriter(filePath);
      BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
    ) {
      for (int chunkPos = 0; chunkPos < localData.length; chunkPos++) {
        CachedChunk chunk = localData[chunkPos];
        if (chunk == null) {
          bufferedWriter.write("/");
        } else {
          ChunkPosInt chunkPosInt = chunk.getChunkPosition();

          bufferedWriter.write(chunkPosInt.x + " " + chunkPosInt.y + " ");

          for (int bit : chunk.getBlockDataFlat().stream().toArray()) {
            bufferedWriter.write(bit);
          }
        }

        if (chunkPos + 1 != localData.length) bufferedWriter.write("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean equals(Object other) {
    CachedRegionFile cachedRegion = (CachedRegionFile) other;
    return (
      cachedRegion.positionRelative.x == positionRelative.x &&
      cachedRegion.positionRelative.y == positionRelative.y
    );
  }
}
