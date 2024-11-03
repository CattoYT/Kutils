package dev.hikari.kutils.client.commands

import com.adamratzman.spotify.models.ContextUri
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
                            }.then(
                                CommandManager.argument("Artist", StringArgumentType.string())
                                    .executes {
                                            context ->
                                        playSpotify(context, StringArgumentType.getString(context, "Song Name"), StringArgumentType.getString(context, "Artist"))
                                        1
                                    }
            )))

        })

    }
    fun playSpotify(context : CommandContext<ServerCommandSource>, songName : String? = null, artist : String? = null) : Int {

        runBlocking {
            try {
                if (songName != null) {
                    var query = songName
                    KutilsClient.Log("Searching for $query")
                    var RequestedSong = KutilsClient.Spotify.spotifyApi?.search?.searchTrack(query.replace(" ", "&20"))?.items?.getOrNull(0)

                    if (RequestedSong != null){
                        KutilsClient.Log("Playing ${RequestedSong.name} by ${RequestedSong.artists[0].name}")
                        KutilsClient.Spotify.spotifyApi?.player?.startPlayback(
                            playableUrisToPlay = listOf(RequestedSong.uri),
                        )
                    }
                    else {
                        KutilsClient.Log("Song not found")
                    }

                    }
                else {
                    KutilsClient.Spotify.spotifyApi?.player?.resume()
                    KutilsClient.Log("Resumed Spotify")
                }


            } catch (e: Exception) {
                KutilsClient.Log("Error pausing Spotify " + e)
                context.source.sendFeedback({ net.minecraft.text.Text.literal("Failed to pause Spotify!") }, false)
            }

        }
        return 0 // this can fail
    }

}