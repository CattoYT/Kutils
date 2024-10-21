package dev.hikari.kutils.client.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.hikari.kutils.client.KutilsClient
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import kotlinx.coroutines.launch
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
                        pauseSpotify(context)

                    1
                }
            )

        })

    }

    fun pauseSpotify(context : CommandContext<ServerCommandSource>) = runBlocking {
        KutilsClient.logger.info("Pause Spotify command executed")
        launch {
            for (device in KutilsClient.Spotify.spotifyApi?.player?.getDevices()!!) {
                KutilsClient.logger.info(device.name)
                if (device.isActive) {
                    KutilsClient.Spotify.spotifyApi?.player?.pause()
                    context.source.sendFeedback({ net.minecraft.text.Text.literal("Paused Spotify") }, false)
                    break
                }
            }
        }


    }
}