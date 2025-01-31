package dev.hikari.SimpleSpotifyController.client.utils

import dev.hikari.SimpleSpotifyController.client.SimpleSpotifyControllerClient
import net.minecraft.client.MinecraftClient
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths



class FileManager {
    companion object {
        val commandspotifyDir: Path = Paths.get(MinecraftClient.getInstance().runDirectory.toString(), "commandspotify")
    }


    fun writeConfig(key: String) {
        writeConfigFile(key)
    }

    fun writeToken(token : String) {
        val configFile = commandspotifyDir.resolve("token")
        if (!Files.exists(configFile)) {
            Files.createDirectories(commandspotifyDir)
            Files.createFile(configFile)
        }

        var (eToken , iv) = Encryption().encrypt(token)
        Files.writeString(configFile, eToken + "|" + iv)
        println("Written token to file")
    }


    fun readEncryptedToken(): String? {
        val configFile = commandspotifyDir.resolve("token")

        try {
            val (eToken, iv) = Files.readString(configFile).split("|")
            if (eToken != null || iv != null) {
                return Encryption().decrypt(eToken, iv)
            }
            throw Exception()
        } catch (e: Exception) {
            SimpleSpotifyControllerClient.Companion.logger.info("Token file not found or is empty! Skipping Spotify restore...")
            println(e)
            return null
        }

    }

    fun writeConfigFile() {
        val configFile = commandspotifyDir.resolve("config.json")
        if (!Files.exists(commandspotifyDir)) {
            Files.createDirectories(commandspotifyDir)
            Files.createFile(configFile)
        }

    }
    fun writeConfigFile(config : String) {
        val configFile = commandspotifyDir.resolve("config.json")
//        val jsonString = Json.encodeToString(config)
//        if (!Files.exists(commandspotifyDir)) {
//            Files.createDirectories(commandspotifyDir)
//            Files.createFile(configFile)
//        }
        Files.writeString(configFile, config)
    }
}