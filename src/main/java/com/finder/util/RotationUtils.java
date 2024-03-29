package com.finder.util;

import com.finder.ForgeFinder;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//THIS IS NOT MY CODE LOL (GOT IT FROM GTC)
//(I'M TOO BRAIN-DEAD TO MAKE SMH LIKE THIS...)

public class RotationUtils {

  public static boolean isDoneRotate = false;

  public static Rotation startRot;
  public static Rotation endRot;
  public static float currentFakeYaw;
  public static float currentFakePitch;
  public static boolean done = true;
  public static boolean excludePitch;
  private static long startTime;
  private static long endTime;
  private static float serverPitch;
  private static float serverYaw;
  private static RotationType rotationType;

  public static double wrapAngleTo180(double angle) {
    return angle - Math.floor(angle / 360 + 0.5) * 360;
  }

  public static float wrapAngleTo180(float angle) {
    return (float) (angle - Math.floor(angle / 360 + 0.5) * 360);
  }

  public static float fovToVec3(Vec3 vec) {
    double x = vec.xCoord - ForgeFinder.MC.thePlayer.posX;
    double z = vec.zCoord - ForgeFinder.MC.thePlayer.posZ;
    double yaw = Math.atan2(x, z) * 57.2957795;
    return (float) (yaw * -1.0);
  }

  public static Rotation getRotation(final Vec3 from, final Vec3 to) {
    double diffX = to.xCoord - from.xCoord;
    double diffY = to.yCoord - from.yCoord;
    double diffZ = to.zCoord - from.zCoord;
    double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

    float pitch = (float) -Math.atan2(dist, diffY);
    float yaw = (float) Math.atan2(diffZ, diffX);
    pitch = (float) wrapAngleTo180((pitch * 180F / Math.PI + 90) * -1);
    yaw = (float) wrapAngleTo180((yaw * 180 / Math.PI) - 90);

    return new Rotation(pitch, yaw);
  }

  public static Rotation getRotation(Vec3 vec3) {
    return getRotation(
      new Vec3(
        ForgeFinder.MC.thePlayer.posX,
        ForgeFinder.MC.thePlayer.posY + ForgeFinder.MC.thePlayer.getEyeHeight(),
        ForgeFinder.MC.thePlayer.posZ
      ),
      vec3
    );
  }

  public static Rotation getRotation(BlockPos block) {
    return getRotation(
      new Vec3(block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5)
    );
  }

  public static Rotation getRotation(Entity entity) {
    return getRotation(
      new Vec3(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ)
    );
  }

  public static Rotation getRotation(Entity entity, Vec3 offset) {
    return getRotation(
      new Vec3(
        entity.posX + offset.xCoord,
        entity.posY + offset.yCoord,
        entity.posZ + offset.zCoord
      )
    );
  }

  public static Rotation getNeededChange(Rotation startRot, Rotation endRot) {
    float yawDiff = wrapAngleTo180(endRot.yaw) - wrapAngleTo180(startRot.yaw);

    if (yawDiff <= -180) {
      yawDiff += 360;
    } else if (yawDiff > 180) {
      yawDiff -= 360;
    }

    return new Rotation(endRot.pitch - startRot.pitch, yawDiff);
  }

  public static Vec3 getVectorForRotation(final float pitch, final float yaw) {
    final float f2 = -MathHelper.cos(-pitch * 0.017453292f);
    return new Vec3(
      MathHelper.sin(-yaw * 0.017453292f - 3.1415927f) * f2,
      MathHelper.sin(-pitch * 0.017453292f),
      MathHelper.cos(-yaw * 0.017453292f - 3.1415927f) * f2
    );
  }

  public static Vec3 getLook(final Vec3 vec) {
    final double diffX = vec.xCoord - ForgeFinder.MC.thePlayer.posX;
    final double diffY =
      vec.yCoord -
      (ForgeFinder.MC.thePlayer.posY + ForgeFinder.MC.thePlayer.getEyeHeight());
    final double diffZ = vec.zCoord - ForgeFinder.MC.thePlayer.posZ;
    final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
    return getVectorForRotation(
      (float) (-(MathHelper.atan2(diffY, dist) * 180.0 / 3.141592653589793)),
      (float) (
        MathHelper.atan2(diffZ, diffX) * 180.0 / 3.141592653589793 - 90.0
      )
    );
  }

  public static Rotation getNeededChange(Rotation endRot) {
    return getNeededChange(
      new Rotation(
        ForgeFinder.MC.thePlayer.rotationPitch,
        ForgeFinder.MC.thePlayer.rotationYaw
      ),
      endRot
    );
  }

  public static Rotation getServerNeededChange(Rotation endRotation) {
    return endRot == null
      ? getNeededChange(endRotation)
      : getNeededChange(endRot, endRotation);
  }

  private static float interpolate(float start, float end) {
    return (
      (end - start) *
      easeOutCubic(
        (float) (System.currentTimeMillis() - startTime) / (endTime - startTime)
      ) +
      start
    );
  }

  public static float easeOutCubic(double number) {
    return (float) Math.max(0, Math.min(1, 1 - Math.pow(1 - number, 3)));
  }

  public static void smoothLookRelative(Rotation rotation, long time) {
    rotationType = RotationType.NORMAL;
    done = false;

    startRot =
      new Rotation(
        ForgeFinder.MC.thePlayer.rotationPitch,
        ForgeFinder.MC.thePlayer.rotationYaw
      );

    endRot =
      new Rotation(
        startRot.pitch + rotation.pitch,
        startRot.yaw + rotation.yaw
      );

    startTime = System.currentTimeMillis();
    endTime = System.currentTimeMillis() + time;
  }

  public static void smoothLook(Rotation rotation, long time) {
    reset();
    rotationType = RotationType.NORMAL;
    isDoneRotate = false;
    done = false;
    startRot =
      new Rotation(
        ForgeFinder.MC.thePlayer.rotationPitch,
        ForgeFinder.MC.thePlayer.rotationYaw
      );

    Rotation neededChange = getNeededChange(startRot, rotation);

    endRot =
      new Rotation(
        startRot.pitch + neededChange.pitch,
        startRot.yaw + neededChange.yaw
      );

    startTime = System.currentTimeMillis();
    endTime = System.currentTimeMillis() + time;
  }

  public static Rotation getRotToBlock(Vec3 vec) {
    return getNeededChange(
      new Rotation(
        ForgeFinder.MC.thePlayer.rotationPitch,
        ForgeFinder.MC.thePlayer.rotationYaw
      ),
      RotationUtils.getRotation(vec)
    );
  }

  public static void smartSmoothLook(Rotation rotation, int msPer180) {
    float rotationDifference = wrapAngleTo180(
      Math.max(
        Math.abs(rotation.pitch - ForgeFinder.MC.thePlayer.rotationPitch),
        Math.abs(rotation.yaw - ForgeFinder.MC.thePlayer.rotationYaw)
      )
    );
    smoothLook(rotation, (int) (rotationDifference / 180 * msPer180));
  }

  public static void serverSmoothLookRelative(Rotation rotation, long time) {
    rotationType = RotationType.SERVER;
    done = false;

    if (currentFakePitch == 0) currentFakePitch =
      ForgeFinder.MC.thePlayer.rotationPitch;
    if (currentFakeYaw == 0) currentFakeYaw =
      ForgeFinder.MC.thePlayer.rotationYaw;

    startRot = new Rotation(currentFakePitch, currentFakeYaw);

    endRot =
      new Rotation(
        startRot.pitch + rotation.pitch,
        startRot.yaw + rotation.yaw
      );

    startTime = System.currentTimeMillis();
    endTime = System.currentTimeMillis() + time;
  }

  public static void serverSmoothLook(Rotation rotation, long time) {
    rotationType = RotationType.SERVER;
    done = false;

    if (currentFakePitch == 0) currentFakePitch =
      ForgeFinder.MC.thePlayer.rotationPitch;
    if (currentFakeYaw == 0) currentFakeYaw =
      ForgeFinder.MC.thePlayer.rotationYaw;

    startRot = new Rotation(currentFakePitch, currentFakeYaw);

    Rotation neededChange = getNeededChange(startRot, rotation);

    endRot =
      new Rotation(
        startRot.pitch + neededChange.pitch,
        startRot.yaw + neededChange.yaw
      );

    startTime = System.currentTimeMillis();
    endTime = System.currentTimeMillis() + time;
  }

  public static void updateServerLookResetting() {
    if (System.currentTimeMillis() <= endTime) {
      ForgeFinder.MC.thePlayer.rotationYaw =
        interpolate(startRot.getYaw(), endRot.getYaw());
      ForgeFinder.MC.thePlayer.rotationPitch =
        interpolate(startRot.getPitch(), endRot.getPitch());

      currentFakeYaw = ForgeFinder.MC.thePlayer.rotationYaw;
      currentFakePitch = ForgeFinder.MC.thePlayer.rotationPitch;
    } else {
      if (!done) {
        ForgeFinder.MC.thePlayer.rotationYaw = endRot.getYaw();
        ForgeFinder.MC.thePlayer.rotationPitch = endRot.getPitch();

        currentFakeYaw = ForgeFinder.MC.thePlayer.rotationYaw;
        currentFakePitch = ForgeFinder.MC.thePlayer.rotationPitch;

        reset();
      }
    }
  }

  public static void updateServerLook() {
    if (System.currentTimeMillis() <= endTime) {
      ForgeFinder.MC.thePlayer.rotationYaw =
        interpolate(startRot.getYaw(), endRot.getYaw());
      ForgeFinder.MC.thePlayer.rotationPitch =
        interpolate(startRot.getPitch(), endRot.getPitch());

      currentFakeYaw = ForgeFinder.MC.thePlayer.rotationYaw;
      currentFakePitch = ForgeFinder.MC.thePlayer.rotationPitch;
    } else {
      if (!done) {
        ForgeFinder.MC.thePlayer.rotationYaw = endRot.getYaw();
        ForgeFinder.MC.thePlayer.rotationPitch = endRot.getPitch();

        currentFakeYaw = ForgeFinder.MC.thePlayer.rotationYaw;
        currentFakePitch = ForgeFinder.MC.thePlayer.rotationPitch;
      }
    }
  }

  public static void look(Rotation rotation) {
    ForgeFinder.MC.thePlayer.rotationPitch = rotation.pitch;
    ForgeFinder.MC.thePlayer.rotationYaw = rotation.yaw;
  }

  public static void reset() {
    done = true;
    startRot = null;
    endRot = null;
    startTime = 0;
    endTime = 0;
    currentFakeYaw = 0;
    currentFakePitch = 0;
    excludePitch = false;
  }

  @SubscribeEvent
  public void onRenderWorld(RenderWorldLastEvent event) {
    try {
      if (rotationType != RotationType.NORMAL) return;
      if (System.currentTimeMillis() <= endTime) {
        if (!excludePitch) ForgeFinder.MC.thePlayer.rotationPitch =
          interpolate(startRot.pitch, endRot.pitch);
        ForgeFinder.MC.thePlayer.rotationYaw =
          interpolate(startRot.yaw, endRot.yaw);
      } else {
        if (!done && endRot != null) {
          if (!excludePitch) ForgeFinder.MC.thePlayer.rotationPitch =
            endRot.pitch;
          ForgeFinder.MC.thePlayer.rotationYaw = endRot.yaw;
        }

        reset();
      }
    } catch (NullPointerException e) {}
  }

  /*@SubscribeEvent(priority = EventPriority.HIGHEST)
  public void onUpdatePre(PlayerMoveEvent.Pre pre) {
    serverPitch = ForgeFinder.MC.thePlayer.rotationPitch;
    serverYaw = ForgeFinder.MC.thePlayer.rotationYaw;
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onUpdatePost(PlayerMoveEvent.Post post) {
    ForgeFinder.MC.thePlayer.rotationPitch = serverPitch;
    ForgeFinder.MC.thePlayer.rotationYaw = serverYaw;
  }*/

  private enum RotationType {
    NORMAL,
    SERVER,
  }

  public static class Rotation {

    public float pitch;
    public float yaw;

    public Rotation(float pitch, float yaw) {
      this.pitch = pitch;
      this.yaw = yaw;
    }

    public float getValue() {
      return Math.abs(this.yaw) + Math.abs(this.pitch);
    }

    public float getPitch() {
      return this.pitch;
    }

    public void setPitch(float pitch) {
      this.pitch = pitch;
    }

    public float getYaw() {
      return this.yaw;
    }

    @Override
    public String toString() {
      return "pitch=" + pitch + ", yaw=" + yaw;
    }
  }
}
