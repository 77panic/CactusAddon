package com.cactus.addon.settings;

import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class RejectsUtils {

    public static boolean inFov(Entity entity, double fov) {
        if (fov >= 360) return true;

        // Get the angle between the player and the target entity
        float[] angle = PlayerUtils.calculateAngle(entity.getBoundingBox().getCenter());

        assert mc.player != null;

        // Calculate the difference in yaw and pitch
        double yawDifference = getAngleDifference(angle[0], mc.player.getYaw());
        double pitchDifference = getAngleDifference(angle[1], mc.player.getPitch());

        // Calculate the distance of the angle differences
        double angleDistance = Math.hypot(yawDifference, pitchDifference);

        // Return true if the angle is within the specified FOV
        return angleDistance <= fov;
    }

    // Helper method to calculate the angle difference
    private static double getAngleDifference(double angle1, double angle2) {
        return MathHelper.wrapDegrees(angle1 - angle2);
    }
}
