package dev.hikari.commandspotify.client.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import dev.hikari.commandspotify.client.CommandSpotifyClient
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

class Playlist {
    fun register() {
         CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
             dispatcher.register(
                 CommandManager.literal("playlist")
                     .executes {
                         //CommandSpotifyClient.logger.info("Test command executed")
                             context ->
                         context.source.sendFeedback(
                             {Text.literal("Usage: /playlist {PlaylistName}")}, false)


                         0
                     }
                     .then(
                         CommandManager.argument("arg", StringArgumentType.string())
                             .executes {
                                    context ->
                                        playPlaylist(StringArgumentType.getString(context, "arg"))
                             }

             ))
         })
    }

    fun playPlaylist(name: String) : Int  {
        println(name)
        return runBlocking {
            for (playlist in CommandSpotifyClient.Spotify.spotifyApi?.playlists?.getClientPlaylists()!!) {
                println(playlist.name.lowercase())
                if (playlist.name.lowercase() == name.lowercase()) {
                    try{
                        CommandSpotifyClient.Spotify.spotifyApi!!.player.startPlayback(playlistId = playlist.id)
                        return@runBlocking 0
                    } catch (e: Exception) {
                        CommandSpotifyClient.logger.error("Error playing playlist: ${e.message}")
                        return@runBlocking 1
                    }

                }
            }
        } as Int
    }



}