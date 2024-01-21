package com.finder.util;

import java.awt.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class Render {

  public static void drawBox(
    double x,
    double y,
    double z,
    Color color,
    float width,
    float partialTicks
  ) {
    Entity viewer = Minecraft.getMinecraft().getRenderViewEntity();
    double x1 =
      x -
      (
        viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partialTicks
      );
    double y1 =
      y -
      (
        viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partialTicks
      );
    double z1 =
      z -
      (
        viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partialTicks
      );
    GlStateManager.pushMatrix();
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();

    //DDSdfsf
    GlStateManager.disableDepth();
    GlStateManager.disableCull();
    GlStateManager.disableLighting();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GL11.glLineWidth(width);

    AxisAlignedBB bb = new AxisAlignedBB(x1, y1, z1, x1 + 1, y1 + 1, z1 + 1);

    RenderGlobal.drawOutlinedBoundingBox(
      bb,
      color.getRed(),
      color.getGreen(),
      color.getBlue(),
      color.getAlpha()
    );

    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.popMatrix();
    GlStateManager.enableLighting();
    GlStateManager.enableDepth();
    GlStateManager.enableCull();
  }

  public static void drawLine(
    Vec3 vec1,
    Vec3 vec2,
    float width,
    Color color,
    float partialTicks
  ) {
    Minecraft mc = Minecraft.getMinecraft();

    Vec3 playerPos = mc.thePlayer.getPositionVector();
    GL11.glPushMatrix();
    GL11.glTranslated(-playerPos.xCoord, -playerPos.yCoord, -playerPos.zCoord);

    GL11.glLineWidth(width); // Set line width (2.0F in this example)
    GL11.glDisable(GL11.GL_TEXTURE_2D); // Disable texture rendering
    GL11.glEnable(GL11.GL_BLEND); // Enable alpha blending
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    GL11.glBegin(GL11.GL_LINES);
    GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), 1); // Set line color (red in this example)

    // Render line
    GL11.glVertex3d(vec1.xCoord, vec1.yCoord, vec1.zCoord);
    GL11.glVertex3d(vec2.xCoord, vec2.yCoord, vec2.zCoord);

    GL11.glEnd();

    GL11.glDisable(GL11.GL_BLEND);
    GL11.glEnable(GL11.GL_TEXTURE_2D);

    GL11.glPopMatrix();
  }
}
