package dev.hikari.kutils.client.commands;

import com.mojang.brigadier.CommandDispatcher
import dev.hikari.kutils.client.KutilsClient
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
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
                        //KutilsClient.logger.info("Test command executed")
                        context ->
                        shareSpotify()
                        0
                    }
            )
        })
    }
    fun shareSpotify() {
        KutilsClient.logger.info("Share Spotify command executed")
        runBlocking {
            var currentlyPlaying = KutilsClient.Spotify.spotifyApi?.player?.getCurrentlyPlaying()?.item?.asTrack?.href.toString().replace("https://api.spotify.com/v1/tracks/", "https://open.spotify.com/track/")
            println(currentlyPlaying)
            val clickableText = Text.literal("Click me to link Spotify!")
                .setStyle(
                    Style.EMPTY
                        .withColor(Formatting.AQUA)
                        .withItalic(true)
                        .withUnderline(true)
                        .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, currentlyPlaying))
                )
            MinecraftClient.getInstance().getNetworkHandler()?.sendPacket(ChatMessageC2SPacket("/say Hello from MyFabricMod!"))
        }
    }
}
