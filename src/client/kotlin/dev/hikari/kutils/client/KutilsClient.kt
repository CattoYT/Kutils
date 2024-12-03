package dev.hikari.kutils.client

import dev.hikari.kutils.client.commands.*
import dev.hikari.kutils.client.modules.Spotify
import dev.hikari.kutils.client.utils.FileManager
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text


class KutilsClient : ClientModInitializer {


    override fun onInitializeClient() {
        logger.info("Hello Fabric world!")


        KutilsBaseCmd().register()
        LinkSpotify().register()
        Test().register()
        Pause().register()
        Play().register()
        Playing().register()
        Debug().register()
        Share().register()
    }

    companion object {

        val version =
            FabricLoader.getInstance().getModContainer("kutils").map { it.metadata.version.friendlyString }.orElse(null)

        val logger = org.slf4j.LoggerFactory.getLogger("Kutils")
        val ConfigManager = FileManager()
        var Spotify = Spotify()

        fun Log(message: String) {
            MinecraftClient.getInstance().player?.sendMessage(createReturnMessage(message), false)
        }

        fun Log(message: Text) {
            MinecraftClient.getInstance().player?.sendMessage(createReturnMessage(message), false)
        }

        fun createReturnMessage(vararg messages: Any): Text {
            val combinedText = Text.literal("(§3Kutils§f) ")
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
