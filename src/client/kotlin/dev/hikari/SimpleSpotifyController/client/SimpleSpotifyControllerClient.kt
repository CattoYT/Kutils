package dev.hikari.SimpleSpotifyController.client

import dev.hikari.SimpleSpotifyController.client.commands.LinkSpotify
import dev.hikari.SimpleSpotifyController.client.commands.Pause
import dev.hikari.SimpleSpotifyController.client.commands.Play
import dev.hikari.SimpleSpotifyController.client.commands.Playing
import dev.hikari.SimpleSpotifyController.client.commands.Playlist
import dev.hikari.SimpleSpotifyController.client.commands.Share
import dev.hikari.SimpleSpotifyController.client.commands.SimpleSpotifyControllerBaseCmd
import dev.hikari.SimpleSpotifyController.client.modules.Spotify
import dev.hikari.SimpleSpotifyController.client.utils.FileManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import org.slf4j.LoggerFactory


class SimpleSpotifyControllerClient : ClientModInitializer {


    override fun onInitializeClient() {
        logger.info("Hello Fabric world!")


        SimpleSpotifyControllerBaseCmd().register()
        LinkSpotify().register()
        Pause().register()
        Play().register()
        Playing().register()
        Share().register()
        Playlist().register()
    }

    companion object {

        val version = FabricLoader.getInstance().getModContainer("simplespotifycontroller")?.get()?.metadata?.version?.toString()

        val logger = LoggerFactory.getLogger("")
        val ConfigManager = FileManager()
        var Spotify = Spotify()

        fun Log(message: String) {
            MinecraftClient.getInstance().player?.sendMessage(createReturnMessage(message), false)
        }

        fun Log(message: Text) {
            MinecraftClient.getInstance().player?.sendMessage(createReturnMessage(message), false)
        }

        fun createReturnMessage(vararg messages: Any): Text {
            val combinedText = Text.literal("(§3SSC§f) ")
            messages.forEach { text ->
                when (text) {
                    is String -> combinedText.append(Text.literal(text))
                    is Text -> combinedText.append(text)
                    else -> throw IllegalArgumentException("Only Text and String types are supported")

                }

            }
            return combinedText


        }
    }
}
