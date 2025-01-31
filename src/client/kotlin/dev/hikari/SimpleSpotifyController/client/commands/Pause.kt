package dev.hikari.SimpleSpotifyController.client.commands

import com.mojang.brigadier.CommandDispatcher
import dev.hikari.SimpleSpotifyController.client.SimpleSpotifyControllerClient
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
                        //CommandSpotifyClient.logger.info("Test command executed")
                        context ->
                        pauseSpotify()

                    0
                }
            )

        })

    }

    fun pauseSpotify() = runBlocking {
        SimpleSpotifyControllerClient.Companion.logger.info("Pause Spotify command executed")
        runBlocking {
            try {
                SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.player?.pause()
                SimpleSpotifyControllerClient.Companion.Log("Paused Spotify")
            } catch (e: Exception) {
                SimpleSpotifyControllerClient.Companion.logger.error("Error pausing Spotify " + e)
                SimpleSpotifyControllerClient.Companion.Log("Failed to pause Spotify!")
                }
        }


    }
}