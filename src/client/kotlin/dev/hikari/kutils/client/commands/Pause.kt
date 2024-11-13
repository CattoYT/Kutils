package dev.hikari.kutils.client.commands

import com.mojang.brigadier.CommandDispatcher
import dev.hikari.kutils.client.KutilsClient
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import kotlinx.coroutines.runBlocking
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class Pause {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("pause")
                    .executes {
                        //KutilsClient.logger.info("Test command executed")
                        context ->
                        pauseSpotify()

                    0
                }
            )

        })

    }

    fun pauseSpotify() = runBlocking {
        KutilsClient.logger.info("Pause Spotify command executed")
        runBlocking {
            try {
                KutilsClient.Spotify.spotifyApi?.player?.pause()
                KutilsClient.Log("Paused Spotify")
            } catch (e: Exception) {
                KutilsClient.logger.error("Error pausing Spotify " + e)
                KutilsClient.Log("Failed to pause Spotify!")
                }
        }


    }
}