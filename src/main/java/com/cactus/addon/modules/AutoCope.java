package com.cactus.addon.modules;

import com.cactus.addon.AddonCactus;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class AutoCope extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    // Settings
    private final Setting<List<String>> messagesSetting = sgGeneral.add(new StringListSetting.Builder()
        .name("messages")
        .description("Messages to send when you die.")
        .defaultValue(
            List.of(
                "Cactus Client",
                "gg!",
                "You suck!"
            )
        )
        .build()
    );

    private final Setting<Boolean> ignoreSelfSetting = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-self")
        .description("Ignores your own death messages.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> randomSetting = sgGeneral.add(new BoolSetting.Builder()
        .name("random")
        .description("Sends a random message from the list when you die.")
        .defaultValue(false)
        .build()
    );

    private boolean sent = false;
    private int messageIndex = 0;
    private final Random random = new Random();

    public AutoCope() {
        super(AddonCactus.CATEGORY, "Auto Cope", "Automatically sends a message when you die. (Envy Client Skid)");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (messagesSetting.get().isEmpty()) return;

        // Ensure it doesn't send multiple times for a single death
        if (!sent && Objects.requireNonNull(mc.player).isDead()) {
            String message;

            if (randomSetting.get()) {
                int randomIndex = random.nextInt(messagesSetting.get().size());
                message = messagesSetting.get().get(randomIndex);
            } else {
                if (messageIndex >= messagesSetting.get().size()) {
                    messageIndex = 0;
                }
                message = messagesSetting.get().get(messageIndex++);
            }

            // Send chat message
            ChatUtils.sendPlayerMsg(message);
            sent = true;
        } else {
            assert mc.player != null;
            if (!mc.player.isDead()) {
                sent = false;
            }
        }
    }
}
