package dev.hikari.kutils.client.commands;

import com.mojang.brigadier.CommandDispatcher
import dev.hikari.kutils.client.KutilsClient
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

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
            var currentlyPlaying = KutilsClient.Spotify.spotifyApi?.player?.getCurrentlyPlaying()?.item?.href

            MinecraftClient.getInstance().player?.networkHandler?.sendChatCommand(currentlyPlaying); //replace with chat message
        }
    }
}
