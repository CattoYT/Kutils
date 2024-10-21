package dev.hikari.kutils.client

import dev.hikari.kutils.client.commands.Commands
import dev.hikari.kutils.client.commands.LinkSpotify
import dev.hikari.kutils.client.commands.Pause
import dev.hikari.kutils.client.commands.Test
import dev.hikari.kutils.client.modules.Spotify
import dev.hikari.kutils.client.utils.FileManager
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import java.nio.file.Path
import java.nio.file.Paths

class KutilsClient : ClientModInitializer {


    override fun onInitializeClient() {
        logger.info("Hello Fabric world!")


        Commands().register()
        LinkSpotify().register()
        Test().register()
        Pause().register()
    }

    companion object {
        val logger = org.slf4j.LoggerFactory.getLogger("Kutils")
        val ConfigManager = FileManager()
        val Spotify = Spotify()
    }
}
