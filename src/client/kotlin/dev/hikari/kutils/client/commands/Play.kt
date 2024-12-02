package dev.hikari.kutils.client.commands

import com.adamratzman.spotify.models.ContextUri
import com.adamratzman.spotify.models.SearchFilter
import com.adamratzman.spotify.models.SearchFilterType
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.hikari.kutils.client.KutilsClient
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

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
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("queue")

                    .executes {
                        //KutilsClient.logger.info("Test command executed")
                            context ->
                        playSpotify(context)

                    }
                    .then(
                        CommandManager.argument("Request", StringArgumentType.string())
                            .executes {
                                    context ->
                                queueManager(context)
                                1
                            }.then(
                                CommandManager.argument("Song", StringArgumentType.string())
                                    .executes {
                                            context ->
                                        queueManager(context)
                                        1
                                    }
                            )))

        })

    }

    fun queueManager(context : CommandContext<ServerCommandSource>) : Int {
        if (StringArgumentType.getString(context, "Request") == "add") {
            KutilsClient.Log("Adding song to queue")
        }
        else if (StringArgumentType.getString(context, "Request") == "skip") {
            KutilsClient.Log("Removing song from queue")
        }
        else {
            KutilsClient.Log("Invalid request")
        }
        return 0
    }

    fun playSpotify(context : CommandContext<ServerCommandSource>, songName : String? = null, artist : String? = null) : Int {

        runBlocking {
            try {
                if (songName != null) {
                    var query = "track:$songName "
                    KutilsClient.Log("Searching for $query")
                    if (artist != null) {
                        //query += "%20artist:$artist"
                        query += " artist:$artist"
                    }
                    var songResults = KutilsClient.Spotify.spotifyApi?.search?.searchTrack(query)?.items;
                    var RequestedSong = songResults?.getOrNull(0)
                    for (i in 0..9) {
                        KutilsClient.Log("Found ${songResults?.getOrNull(i)?.name} by ${songResults?.getOrNull(i)?.artists[0]?.name}")
                    }
                    if (RequestedSong != null){
                        KutilsClient.Log("Playing ${RequestedSong.name} by ${RequestedSong.artists[0].name}")
                        try{
                            KutilsClient.Spotify.spotifyApi?.player?.startPlayback(
                            playableUrisToPlay = listOf(RequestedSong.uri),
                        )
                            println(KutilsClient.Spotify.spotifyApi?.token?.refreshToken)
                        } catch (e: Exception) {
                            KutilsClient.Log("Oh Nyo, are you sure you started Spotify playback? Just play any song from the client!")

                        }
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
                context.source.sendFeedback({ Text.literal("Failed to pause Spotify!") }, false)
            }

        }
        return 0 // this can fail
    }

}