package dev.hikari.SimpleSpotifyController.client.commands

import com.adamratzman.spotify.models.Track
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.hikari.SimpleSpotifyController.client.SimpleSpotifyControllerClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

class Play {
    fun register() {

        /*Play*/ CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("play")

                    .executes {
                        //CommandSpotifyClient.logger.info("Test command executed")
                            context ->
                        playSpotify(context)

                    }
                    .then(
                        CommandManager.argument("Song Name", StringArgumentType.string())
                            .executes { context ->
                                playSpotify(context, StringArgumentType.getString(context, "Song Name"))
                                1
                            }.then(
                                CommandManager.argument("Artist", StringArgumentType.string())
                                    .executes { context ->
                                        playSpotify(
                                            context,
                                            StringArgumentType.getString(context, "Song Name"),
                                            StringArgumentType.getString(context, "Artist")
                                        )
                                        1
                                    }
                            )))

        })
        /*Queue*/ CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("queue")
                    .then(
                        CommandManager.argument("Request", StringArgumentType.string())
                            .executes { context ->
                                queueManager(context)
                                1
                            }.then(
                                CommandManager.argument("Song", StringArgumentType.string())
                                    .executes { context ->
                                        queueManager(context)
                                        1
                                    }
                            )))
        })
        /*Skip*/ CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("skip")

                    .executes {
                        //CommandSpotifyClient.logger.info("Test command executed")
                            context ->
                        runBlocking {
                            SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.player?.skipForward()
                            delay(700L)
                            SimpleSpotifyControllerClient.Companion.Spotify.WhatIsPlaying()

                        }
                        0
                    }

            )
            dispatcher.register(
                CommandManager.literal("back")

                    .executes {
                        //CommandSpotifyClient.logger.info("Test command executed")
                            context ->
                        runBlocking {
                            SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.player?.skipBehind()
                            SimpleSpotifyControllerClient.Companion.Spotify.WhatIsPlaying()

                        }
                        0
                    }

            )

        }
        )


    }

    fun queueManager(context: CommandContext<ServerCommandSource>): Int {
        println(StringArgumentType.getString(context, "Request"))
        if (StringArgumentType.getString(context, "Request") == "add") {
            SimpleSpotifyControllerClient.Companion.Log("Adding song to queue")

            var track = querySong(StringArgumentType.getString(context, "Song"))
            println(track)
            if (track != null) {
                try {
                    runBlocking {
                        SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.player?.addItemToEndOfQueue(track.uri)
                    }
                    SimpleSpotifyControllerClient.Companion.Log("Added ${track.name} by ${track.artists[0].name} to the queue.")
                } catch (e: Exception) {
                    println(e)
                    SimpleSpotifyControllerClient.Companion.Log("Oh Nyo, are you sure you started Spotify playback? Just play any song from the client!")
                }
            } else {
                SimpleSpotifyControllerClient.Companion.Log("Song not found")
            }


        } else if (StringArgumentType.getString(context, "Request") == "skip") {
            runBlocking {
                SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.player?.skipForward()
                delay(700L)
                SimpleSpotifyControllerClient.Companion.Spotify.WhatIsPlaying()
            }
        } else {
            SimpleSpotifyControllerClient.Companion.Log("Invalid request")
        }
        return 0
    }

    fun playSpotify(
        context: CommandContext<ServerCommandSource>,
        songName: String? = null,
        artist: String? = null
    ): Int {

        runBlocking {
            try {
                if (songName != null) {
                    var query = "track:$songName "
                    SimpleSpotifyControllerClient.Companion.Log("Searching for $query")
                    if (artist != null) {
                        //query += "%20artist:$artist"
                        query += " artist:$artist"
                    }
                    var songResults = SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.search?.searchTrack(query)?.items
                    var RequestedSong = songResults?.getOrNull(0)
                    for (i in 0..9) {
                        SimpleSpotifyControllerClient.Companion.Log("Found ${songResults?.getOrNull(i)?.name} by ${songResults?.getOrNull(i)?.artists[0]?.name}")
                    }
                    if (RequestedSong != null) {
                        SimpleSpotifyControllerClient.Companion.Log("Playing ${RequestedSong.name} by ${RequestedSong.artists[0].name}")
                        try {
                            SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.player?.startPlayback(
                                playableUrisToPlay = listOf(RequestedSong.uri),
                            )
                            println(SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.token?.refreshToken)
                        } catch (e: Exception) {
                            SimpleSpotifyControllerClient.Companion.Log("Oh Nyo, are you sure you started Spotify playback? Just play any song from the client!")

                        }
                    } else {
                        SimpleSpotifyControllerClient.Companion.Log("Song not found")
                    }

                } else {
                    SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.player?.resume()
                    SimpleSpotifyControllerClient.Companion.Log("Resumed Spotify")
                }


            } catch (e: Exception) {
                SimpleSpotifyControllerClient.Companion.Log("Error pausing Spotify " + e)
                context.source.sendFeedback({ Text.literal("Failed to pause Spotify!") }, false)
            }

        }
        return 0 // this can fail
    }

    fun querySong(songName: String, artist: String? = null): Track? {


        try {
            var query = "track:$songName "
            SimpleSpotifyControllerClient.Companion.Log("Searching for $query")
            if (artist != null) {
                //query += "%20artist:$artist"
                query += " artist:$artist"
            }
            var RequestedSong: Track? = null
            runBlocking {

                RequestedSong =
                    SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.search?.searchTrack(query)?.items?.getOrNull(0)?.asTrack
            }
            return RequestedSong

        } catch (e: Exception) {
            SimpleSpotifyControllerClient.Companion.Log("Error pausing Spotify " + e)

        }

        return null
    }

}