package dev.hikari.commandspotify.client.commands

import com.mojang.brigadier.CommandDispatcher
import dev.hikari.commandspotify.client.CommandSpotifyClient
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
        CommandSpotifyClient.logger.info("Pause Spotify command executed")
        runBlocking {
            try {
                CommandSpotifyClient.Spotify.spotifyApi?.player?.pause()
                CommandSpotifyClient.Log("Paused Spotify")
            } catch (e: Exception) {
                CommandSpotifyClient.logger.error("Error pausing Spotify " + e)
                CommandSpotifyClient.Log("Failed to pause Spotify!")
                }
        }


    }
}