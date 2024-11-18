package dev.hikari.kutils.client.utils

import dev.hikari.kutils.client.KutilsClient
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


    fun writeConfig(key: String) {
        writeConfigFile(key)
    }

    fun writeToken(token : String) {
        val configFile = kutilsDir.resolve("token")
        if (!Files.exists(configFile)) {
            Files.createDirectories(kutilsDir)
            Files.createFile(configFile)
        }

        var (eToken , iv) = Encryption().encrypt(token)
        Files.writeString(configFile, eToken + "|" + iv)
        println("Written token to file")
    }


    fun readEncryptedToken(): String? {
        val configFile = kutilsDir.resolve("token")

        try {
            val (eToken, iv) = Files.readString(configFile).split("|")
            if (eToken != null || iv != null) {
                return Encryption().decrypt(eToken, iv)
            }
            throw Exception()
        } catch (e: Exception) {
            KutilsClient.logger.info("Token file not found or is empty! Skipping Spotify restore...")
            println(e)
            return null
        }

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