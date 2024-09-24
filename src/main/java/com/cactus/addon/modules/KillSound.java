package com.cactus.addon.modules;

import com.cactus.addon.AddonCactus;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class KillSound extends Module {

    // Minecraft client instance
    private final MinecraftClient mc = MinecraftClient.getInstance();

    // Setting group
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Enum setting for choosing sound
    private final Setting<KillSoundOption> killSound = sgGeneral.add(new EnumSetting.Builder<KillSoundOption>()
        .name("kill-sound")
        .description("The sound to play when you kill an entity.")
        .defaultValue(KillSoundOption.AMETHYST)
        .build());

    // Integer settings for volume and pitch
    private final Setting<Integer> volume = sgGeneral.add(new IntSetting.Builder()
        .name("volume")
        .description("The volume of the kill sound.")
        .defaultValue(100) // Volume percentage
        .min(0)
        .max(300) // Scale it to a percentage
        .build());

    private final Setting<Integer> pitch = sgGeneral.add(new IntSetting.Builder()
        .name("pitch")
        .description("The pitch of the kill sound.")
        .defaultValue(100) // Pitch percentage
        .min(0)
        .max(200) // Scale it to a percentage
        .build());

    // Enum for sound options
    public enum KillSoundOption {
        AMETHYST(SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK),
        WITHER_DEATH(SoundEvents.ENTITY_WITHER_DEATH),
        LEVEL_UP(SoundEvents.ENTITY_PLAYER_LEVELUP),
        ANVIL_LAND(SoundEvents.BLOCK_ANVIL_LAND);

        public final SoundEvent soundEvent;

        KillSoundOption(SoundEvent soundEvent) {
            this.soundEvent = soundEvent;
        }

        public SoundEvent getSoundEvent() {
            return soundEvent;
        }
    }

    // Constructor
    public KillSound() {
        super(AddonCactus.CATEGORY, "Kill Sound", "Plays a sound when you kill any entity.");
    }

    // Method to handle playing the selected sound when a kill happens
    public void onPlayerKill() {
        // Play the sound for any entity kill if the module is active
        if (this.isActive()) {
            assert mc.player != null;
            // Use selected sound from enum
            SoundEvent selectedSound = killSound.get().getSoundEvent();
            mc.player.playSound(selectedSound, volume.get() / 100.0F, pitch.get() / 100.0F); // Scale to 0-3 and 0-2
        }
    }
}
