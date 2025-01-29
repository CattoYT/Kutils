package dev.hikari.commandspotify.client.commands;

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.hikari.commandspotify.client.CommandSpotifyClient
import dev.hikari.commandspotify.client.modules.Spotify
import dev.hikari.commandspotify.client.utils.ColorHelper
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback

import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text


class CommandSpotifyBaseCmd {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("commandspotify")
                    .executes {
                        context ->
                        {
                            displayInfo(context)
                            CommandSpotifyClient.logger.info("CommandSpotify command executed")
                        }

                        1
                    }.then(
                        CommandManager.argument("Debug Argument", StringArgumentType.string())
                            .executes {
                                context ->
                                CommandSpotifyClient.logger.info("Test command executed")
                                if (StringArgumentType.getString(context, "Debug Argument") == "reloadSpotify") {
                                    CommandSpotifyClient.Spotify = Spotify()
                                } else if (StringArgumentType.getString(context, "Debug Argument") == "writeToken") {
                                    CommandSpotifyClient.ConfigManager.writeToken(CommandSpotifyClient.Spotify.spotifyApi?.token?.refreshToken.toString())

                                } else if (StringArgumentType.getString(context, "Debug Argument") == "initmodule") {
                                    Playlist().register()
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
                    ColorHelper.AQUA + "Command Spotify " + ColorHelper.WHITE + "${"${ColorHelper.GREEN}${CommandSpotifyClient.version}${ColorHelper.WHITE}"}" +
                    "\nA small utilty mod that adds Spotify integration to Minecraft!")}, false)
    }
}
