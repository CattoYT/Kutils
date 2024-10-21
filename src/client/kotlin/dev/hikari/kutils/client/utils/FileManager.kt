package dev.hikari.kutils.client.utils

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.MinecraftClient
import org.apache.logging.log4j.core.appender.FileManager
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Serializable
data class Config(
    val clientID: String,
    val clientSecret: String
)

class FileManager {
    companion object {
        val kutilsDir: Path = Paths.get(MinecraftClient.getInstance().runDirectory.toString(), "kutils")
        var config: Config? = FileManager().readConfig()
    }

    fun readConfig(): Config? {
        val configFile = kutilsDir.resolve("config.json")
        return if (Files.exists(configFile)) {
            val jsonString = Files.readString(configFile)
            Json.decodeFromString<Config>(jsonString)
        } else {
            null
        }
    }

    fun writeConfig(key: String, value: String) {
        config = Config(key, value)
        writeConfigFile(config!!)
    }

    fun writeConfigFile(config: Config) {
        val configFile = kutilsDir.resolve("config.json")
        val jsonString = Json.encodeToString(config)
        Files.writeString(configFile, jsonString)
    }
}