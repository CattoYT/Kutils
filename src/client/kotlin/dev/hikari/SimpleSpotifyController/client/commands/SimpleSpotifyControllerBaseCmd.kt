package dev.hikari.SimpleSpotifyController.client.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.hikari.SimpleSpotifyController.client.SimpleSpotifyControllerClient
import dev.hikari.SimpleSpotifyController.client.utils.ColorHelper
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback

import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text


class SimpleSpotifyControllerBaseCmd {
    fun register() {
        println("BRO")

        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("simplespotifycontroller")
                    .executes {
                        context -> displayInfo(context)


                        0
                    }
//                    .then(
//                        CommandManager.argument("Debug Argument", StringArgumentType.string())
//                            .executes {
//                                context ->
//                                CommandSpotifyClient.logger.info("Test command executed")
//                                if (StringArgumentType.getString(context, "Debug Argument") == "reloadSpotify") {
//                                    CommandSpotifyClient.Spotify = Spotify()
//                                } else if (StringArgumentType.getString(context, "Debug Argument") == "writeToken") {
//                                    CommandSpotifyClient.ConfigManager.writeToken(CommandSpotifyClient.Spotify.spotifyApi?.token?.refreshToken.toString())
//
//                                } else if (StringArgumentType.getString(context, "Debug Argument") == "initmodule") {
//                                    Playlist().register()
//                                }
//
//
//                                1
//                            }
//                    )
            )

        })
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("ssc") //idk how to alias cmds enjoy the copy paste lol
                    .executes {
                            context -> displayInfo(context)


                        0
                    }
            )

        })

    }

    fun displayInfo(context: CommandContext<ServerCommandSource>) {
        println("YO?")
        context.source.sendFeedback(
            {Text.literal("           " +
                    ColorHelper.AQUA + "Simple Spotify Controller" +
                    " " + ColorHelper.WHITE + "${"${ColorHelper.GREEN}${SimpleSpotifyControllerClient.Companion.version}${ColorHelper.WHITE}"}" +
                    "\nA small utilty mod that adds Spotify integration to Minecraft!")}, false)
    }
}
