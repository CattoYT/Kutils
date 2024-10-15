package dev.hikari.kutils.client

import dev.hikari.kutils.client.commands.Commands
import net.fabricmc.api.ClientModInitializer

class KutilsClient : ClientModInitializer {


    override fun onInitializeClient() {
        logger.info("Hello Fabric world!")
        Commands().register()
    }

    companion object {
        val logger = org.slf4j.LoggerFactory.getLogger("Kutils")
    }
}
