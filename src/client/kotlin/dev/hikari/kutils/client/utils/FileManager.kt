package dev.hikari.kutils.client.utils

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.MinecraftClient
import org.apache.logging.log4j.core.appender.FileManager
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths



class FileManager {
    companion object {
        val kutilsDir: Path = Paths.get(MinecraftClient.getInstance().runDirectory.toString(), "kutils")
    }

    fun readConfig(): String? {
        val configFile = kutilsDir.resolve("config.json")
        return if (Files.exists(configFile)) {
            Files.readString(configFile)
        } else {
            null
        }
    }

    fun writeConfig(key: String) {

        writeConfigFile(key)
    }

    fun writeConfigFile() {
        val configFile = kutilsDir.resolve("config.json")
        if (!Files.exists(kutilsDir)) {
            Files.createDirectories(kutilsDir)
            Files.createFile(configFile)
        }

    }
    fun writeConfigFile(config : String) {
        val configFile = kutilsDir.resolve("config.json")
//        val jsonString = Json.encodeToString(config)
//        if (!Files.exists(kutilsDir)) {
//            Files.createDirectories(kutilsDir)
//            Files.createFile(configFile)
//        }
        Files.writeString(configFile, config)
    }
}