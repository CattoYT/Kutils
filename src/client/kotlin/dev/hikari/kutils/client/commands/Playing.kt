package dev.hikari.kutils.client.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.hikari.kutils.client.KutilsClient
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class Playing {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("pause")
                    .executes {
                        //KutilsClient.logger.info("Test command executed")
                            context ->
                                WhatIsPlaying()

                        0
                    }
            )

        })

    }

    fun WhatIsPlaying() {
        KutilsClient.logger.info("Pause Spotify command executed")
        runBlocking {

                var currentlyPlaying = KutilsClient.Spotify.spotifyApi?.player?.getCurrentlyPlaying()?.item?.asTrack?.name
                if (currentlyPlaying == null) {
                    KutilsClient.Log("No song is currently playing")
                } else {
                    KutilsClient.Log("Currently Playing: $currentlyPlaying")
                }

        }

    }
}