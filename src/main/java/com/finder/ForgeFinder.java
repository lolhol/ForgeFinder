package com.finder;

import com.finder.cache.CacheManager;
import com.finder.debug.Test1;
import com.finder.debug.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "ForgeFinder", useMetadata = true)
public class ForgeFinder {

  public static final CacheManager CACHE_MANAGER = new CacheManager(false);
  public static final Minecraft MC = Minecraft.getMinecraft();

  @Mod.EventHandler
  public void init(FMLInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(new Test1());
    MinecraftForge.EVENT_BUS.register(new RenderUtil());
    MinecraftForge.EVENT_BUS.register(CACHE_MANAGER);
    CACHE_MANAGER.isCaching = true;
  }
}
