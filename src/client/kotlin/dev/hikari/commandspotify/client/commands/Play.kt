package dev.hikari.commandspotify.client.commands

import com.adamratzman.spotify.models.Track
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.hikari.commandspotify.client.CommandSpotifyClient
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
                            CommandSpotifyClient.Spotify.spotifyApi?.player?.skipForward()
                            delay(700L)
                            CommandSpotifyClient.Spotify.WhatIsPlaying()

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
                            CommandSpotifyClient.Spotify.spotifyApi?.player?.skipBehind()
                            CommandSpotifyClient.Spotify.WhatIsPlaying()

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
            CommandSpotifyClient.Log("Adding song to queue")

            var track = querySong(StringArgumentType.getString(context, "Song"))
            println(track)
            if (track != null) {
                try {
                    runBlocking {
                        CommandSpotifyClient.Spotify.spotifyApi?.player?.addItemToEndOfQueue(track.uri)
                    }
                    CommandSpotifyClient.Log("Added ${track.name} by ${track.artists[0].name} to the queue.")
                } catch (e: Exception) {
                    println(e)
                    CommandSpotifyClient.Log("Oh Nyo, are you sure you started Spotify playback? Just play any song from the client!")
                }
            } else {
                CommandSpotifyClient.Log("Song not found")
            }


        } else if (StringArgumentType.getString(context, "Request") == "skip") {
            runBlocking {
                CommandSpotifyClient.Spotify.spotifyApi?.player?.skipForward()
                delay(700L)
                CommandSpotifyClient.Spotify.WhatIsPlaying()
            }
        } else {
            CommandSpotifyClient.Log("Invalid request")
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
                    CommandSpotifyClient.Log("Searching for $query")
                    if (artist != null) {
                        //query += "%20artist:$artist"
                        query += " artist:$artist"
                    }
                    var songResults = CommandSpotifyClient.Spotify.spotifyApi?.search?.searchTrack(query)?.items
                    var RequestedSong = songResults?.getOrNull(0)
                    for (i in 0..9) {
                        CommandSpotifyClient.Log("Found ${songResults?.getOrNull(i)?.name} by ${songResults?.getOrNull(i)?.artists[0]?.name}")
                    }
                    if (RequestedSong != null) {
                        CommandSpotifyClient.Log("Playing ${RequestedSong.name} by ${RequestedSong.artists[0].name}")
                        try {
                            CommandSpotifyClient.Spotify.spotifyApi?.player?.startPlayback(
                                playableUrisToPlay = listOf(RequestedSong.uri),
                            )
                            println(CommandSpotifyClient.Spotify.spotifyApi?.token?.refreshToken)
                        } catch (e: Exception) {
                            CommandSpotifyClient.Log("Oh Nyo, are you sure you started Spotify playback? Just play any song from the client!")

                        }
                    } else {
                        CommandSpotifyClient.Log("Song not found")
                    }

                } else {
                    CommandSpotifyClient.Spotify.spotifyApi?.player?.resume()
                    CommandSpotifyClient.Log("Resumed Spotify")
                }


            } catch (e: Exception) {
                CommandSpotifyClient.Log("Error pausing Spotify " + e)
                context.source.sendFeedback({ Text.literal("Failed to pause Spotify!") }, false)
            }

        }
        return 0 // this can fail
    }

    fun querySong(songName: String, artist: String? = null): Track? {


        try {
            var query = "track:$songName "
            CommandSpotifyClient.Log("Searching for $query")
            if (artist != null) {
                //query += "%20artist:$artist"
                query += " artist:$artist"
            }
            var RequestedSong: Track? = null
            runBlocking {

                RequestedSong =
                    CommandSpotifyClient.Spotify.spotifyApi?.search?.searchTrack(query)?.items?.getOrNull(0)?.asTrack
            }
            return RequestedSong

        } catch (e: Exception) {
            CommandSpotifyClient.Log("Error pausing Spotify " + e)

        }

        return null
    }

}