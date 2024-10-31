package dev.hikari.kutils.client.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.hikari.kutils.client.KutilsClient
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class Play {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("play")

                    .executes {
                        //KutilsClient.logger.info("Test command executed")
                            context ->
                        playSpotify(context)

                    }
                    .then(
                        CommandManager.argument("Song Name", StringArgumentType.string())
                            .executes {
                                context ->
                                playSpotify(context, StringArgumentType.getString(context, "Song Name"))
                                1
                            }
            ))

        })

    }
    fun playSpotify(context : CommandContext<ServerCommandSource>, songName : String? = null) : Int {
        KutilsClient.Log("Pause Spotify command executed")
        runBlocking {
            try {
                if (songName != null) {
                    KutilsClient.Spotify.spotifyApi?.player?.startPlayback() // TODO: IMPLEMENT CUSTOM SONG INPUTS
                }
                else {
                    KutilsClient.Spotify.spotifyApi?.player?.resume()
                }
                KutilsClient.Log("Resumed Spotify")

            } catch (e: Exception) {
                KutilsClient.Log("Error pausing Spotify " + e)
                context.source.sendFeedback({ net.minecraft.text.Text.literal("Failed to pause Spotify!") }, false)
            }

        }
        return 0 // this can fail
    }

}