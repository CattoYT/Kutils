package dev.hikari.commandspotify.client.commands

import com.mojang.brigadier.CommandDispatcher
import dev.hikari.commandspotify.client.CommandSpotifyClient
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class Playing {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("playing")
                    .executes {
                        //CommandSpotifyClient.logger.info("Test command executed")
                            context ->
                                CommandSpotifyClient.Spotify.WhatIsPlaying()

                        0
                    }
            )

        })

    }


}