package com.cactus.addon.modules;

import com.cactus.addon.AddonCactus;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.starscript.Script;
import meteordevelopment.starscript.compiler.Compiler;
import meteordevelopment.starscript.compiler.Parser;
import meteordevelopment.starscript.utils.StarscriptError;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class ChatBot extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<String> prefix = sgGeneral.add(new StringSetting.Builder()
        .name("prefix")
        .description("Command prefix for the bot.")
        .defaultValue("!")
        .build()
    );

    private final Setting<Boolean> help = sgGeneral.add(new BoolSetting.Builder()
        .name("help")
        .description("Add help command.")
        .defaultValue(true)
        .build()
    );

    private final Map<String, String> commandMap = new LinkedHashMap<>();

    public ChatBot() {
        super(AddonCactus.CATEGORY, "Chat Bot", "Bot which automatically responds to chat messages. (Meteor Rejects Fix)");

        commandMap.put("ping", "Pong!");
        commandMap.put("tps", "Current TPS: {server.tps}");
        commandMap.put("time", "It's currently {server.time}");
        commandMap.put("pos", "I am @ {player.pos}");
    }

    @Override
    public WVerticalList getWidget(GuiTheme theme) {
        WVerticalList list = theme.verticalList();

        WButton addCommandButton = list.add(theme.button("Add Command")).expandX().widget();
        addCommandButton.action = () -> {
            commandMap.put("newCommand", "Enter response");
            updateCommandList(theme, list);
        };

        updateCommandList(theme, list);

        return list;
    }

    private void updateCommandList(GuiTheme theme, WVerticalList list) {
        list.clear();

        for (Map.Entry<String, String> entry : commandMap.entrySet()) {
            AtomicReference<String> command = new AtomicReference<>(entry.getKey());
            AtomicReference<String> response = new AtomicReference<>(entry.getValue());

            WTextBox commandBox = list.add(theme.textBox(command.get())).minWidth(150).expandX().widget();
            commandBox.actionOnUnfocused = () -> {
                String oldCommand = command.get();
                command.set(commandBox.get());
                if (!oldCommand.equals(command.get())) {
                    String oldResponse = commandMap.remove(oldCommand);
                    commandMap.put(command.get(), oldResponse);
                }
            };

            WTextBox responseBox = list.add(theme.textBox(response.get())).minWidth(150).expandX().widget();
            responseBox.actionOnUnfocused = () -> {
                response.set(responseBox.get());
                commandMap.put(command.get(), response.get());
            };

            WButton removeButton = list.add(theme.button("Remove")).widget();
            removeButton.action = () -> {
                commandMap.remove(command.get());
                updateCommandList(theme, list);
            };
        }

        WButton addCommandButton = list.add(theme.button("Add Command")).expandX().widget();
        addCommandButton.action = () -> {
            commandMap.put("newCommand", "Enter response");
            updateCommandList(theme, list);
        };
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) {
        String msg = event.getMessage().getString();
        String cmdPrefix = prefix.get();

        if (help.get() && msg.endsWith(cmdPrefix + "help")) {
            ChatUtils.sendPlayerMsg("Available commands: " + String.join(", ", commandMap.keySet()));
            return;
        }

        for (String cmd : commandMap.keySet()) {
            if (msg.endsWith(cmdPrefix + cmd)) {
                Script script = compile(commandMap.get(cmd));
                if (script == null) {
                    ChatUtils.sendPlayerMsg("An error occurred");
                    return;
                }

                try {
                    var section = MeteorStarscript.ss.run(script);
                    ChatUtils.sendPlayerMsg(section.text);
                } catch (StarscriptError e) {
                    MeteorStarscript.printChatError(e);
                    ChatUtils.sendPlayerMsg("An error occurred");
                }
                return;
            }
        }
    }

    private Script compile(String scriptText) {
        if (scriptText == null) return null;

        Parser.Result result = Parser.parse(scriptText);
        if (result.hasErrors()) {
            MeteorStarscript.printChatError(result.errors.get(0));
            return null;
        }

        return Compiler.compile(result);
    }
}
