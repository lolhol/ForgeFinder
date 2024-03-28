package com.finder.cache;

import com.finder.calculator.util.BetterBlockPos;

/**
 * @apiNote this is used as an interface for the file writing and reading
 */
public class CachedRegionFile {

  public final BetterBlockPos positionRelative;
  public final BetterBlockPos positionCenterBlockPos;

  /**
   * @param positionRelative such as [1, 1] one 1 = 16 x 16 chunks
   */
  public CachedRegionFile(
    BetterBlockPos positionRelative,
    BetterBlockPos positionCenterBlockPos
  ) {
    this.positionRelative = positionRelative;
    this.positionCenterBlockPos = positionCenterBlockPos;
  }
}
