package com.finder.calculator.util;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.jetbrains.annotations.NotNull;

public class Node implements Comparable<Node> {
    public BlockPos blockPos;

    @Override
    public int compareTo(@NotNull Node o) {
        return 0;
    }
}
