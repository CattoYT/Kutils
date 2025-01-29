package dev.hikari.commandspotify.client

import dev.hikari.commandspotify.client.commands.*
import dev.hikari.commandspotify.client.modules.Spotify
import dev.hikari.commandspotify.client.utils.FileManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import org.slf4j.LoggerFactory


class CommandSpotifyClient : ClientModInitializer {


    override fun onInitializeClient() {
        logger.info("Hello Fabric world!")


        CommandSpotifyBaseCmd().register()
        LinkSpotify().register()
        Pause().register()
        Play().register()
        Playing().register()
        Share().register()
        Playlist().register()
    }

    companion object {

        val version =
            FabricLoader.getInstance().getModContainer("commandspotify").map { it.metadata.version.friendlyString }.orElse(null)

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
            val combinedText = Text.literal("(§3CommandSpotify§f) ")
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
