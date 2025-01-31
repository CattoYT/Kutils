package dev.hikari.SimpleSpotifyController.client.commands

import com.mojang.brigadier.CommandDispatcher
import dev.hikari.SimpleSpotifyController.client.SimpleSpotifyControllerClient
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class Share {

    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("share")
                    .executes {
                        //CommandSpotifyClient.logger.info("Test command executed")
                        context ->
                        shareSpotify()
                        0
                    }
            )
        })
    }
    fun shareSpotify() {
        SimpleSpotifyControllerClient.Companion.logger.info("Share Spotify command executed")
        runBlocking {
            var currentlyPlaying = SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.player?.getCurrentlyPlaying()?.item?.asTrack?.href.toString().replace("https://api.spotify.com/v1/tracks/", "https://open.spotify.com/track/")
            println(currentlyPlaying)
            val clickableText = Text.literal("Currently playing " + SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.player?.getCurrentlyPlaying()?.item?.asTrack?.name)
                .setStyle(
                    Style.EMPTY
                        .withColor(Formatting.AQUA)
                        .withItalic(true)
                        .withUnderline(true)
                        .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, currentlyPlaying))
                )
            MinecraftClient.getInstance().networkHandler?.sendChatMessage(
                "I'm currently listening to " + SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.player?.getCurrentlyPlaying()?.item?.asTrack?.name +
                        " by " + SimpleSpotifyControllerClient.Companion.Spotify.spotifyApi?.player?.getCurrentlyPlaying()?.item?.asTrack?.artists?.get(0)?.name)
            MinecraftClient.getInstance().networkHandler?.sendChatMessage(
                "Here's the web link! " + currentlyPlaying)
        }
    }
}
