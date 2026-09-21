package com.multicmd;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class MultiCmdMod implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                CommandManager.literal("runbatch")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.argument("commands", StringArgumentType.greedyString())
                        .executes(context -> {
                            String input = StringArgumentType.getString(context, "commands");
                            ServerCommandSource source = context.getSource();
                            
                            // Splits on semicolons while trimming whitespace
                            String[] commands = input.split(";");
                            int executedCount = 0;
                            
                            for (String rawCmd : commands) {
                                String cmd = rawCmd.trim();
                                if (!cmd.isEmpty()) {
                                    if (cmd.startsWith("/")) {
                                        cmd = cmd.substring(1);
                                    }
                                    source.getServer().getCommandManager().executeWithPrefix(source, cmd);
                                    executedCount++;
                                }
                            }
                            return executedCount;
                        })
                    )
            );
        });
    }
}
