package com.cactus.addon.mixin;

import com.cactus.addon.AddonCactus;
import com.cactus.addon.modules.KillSound;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import meteordevelopment.meteorclient.systems.modules.Modules;

@Mixin(LivingEntity.class)
public abstract class EntityDeathMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onEntityDeath(DamageSource source, CallbackInfo info) {
        // Check if the killer is the player (client player)
        if (source.getAttacker() instanceof LivingEntity && source.getAttacker().equals(MinecraftClient.getInstance().player)) {
            // Check if the KillSoundModule is active
            KillSound killSound = Modules.get().get(KillSound.class);
            if (killSound.isActive()) {
                // Play the selected sound when the player kills an entity
                killSound.onPlayerKill();
            }
        }
    }
}
