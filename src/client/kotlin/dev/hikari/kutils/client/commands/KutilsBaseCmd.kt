package dev.hikari.kutils.client.commands;

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.hikari.kutils.client.KutilsClient
import dev.hikari.kutils.client.modules.Spotify
import dev.hikari.kutils.client.utils.ColorHelper
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback

import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text


class KutilsBaseCmd {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("kutils")
                    .executes {
                        context ->
                        {
                            displayInfo(context)
                            KutilsClient.logger.info("Kutils command executed")
                        }

                        1
                    }.then(
                        CommandManager.argument("Debug Argument", StringArgumentType.string())
                            .executes {
                                context ->
                                KutilsClient.logger.info("Test command executed")
                                if (StringArgumentType.getString(context, "Debug Argument") == "reloadSpotify") {
                                    KutilsClient.Spotify = Spotify()
                                }
                                1
                            }
                    )
            )

        })

    }

    fun displayInfo(context: CommandContext<ServerCommandSource>) {
        context.source.sendFeedback(
            {Text.literal("" +
                    ColorHelper.AQUA + "Kutils " + ColorHelper.WHITE + "${"${ColorHelper.GREEN}${KutilsClient.version}${ColorHelper.WHITE}"}" +
                    "\nA small utilty mod that adds a ton of utility such as spotify integration!")}, false)
    }
}
