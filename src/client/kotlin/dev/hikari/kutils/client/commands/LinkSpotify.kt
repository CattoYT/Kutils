package dev.hikari.kutils.client.commands

import com.adamratzman.spotify.*
import com.mojang.brigadier.CommandDispatcher
import dev.hikari.kutils.client.KutilsClient
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import org.kotlincrypto.SecureRandom
import java.util.Base64

class LinkSpotify {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("linkspotify")

                                    .executes {
                                        //KutilsClient.logger.info("Test command executed")
                                        context -> testSpotify()
                                        1
                                    }

            )


        })

    }

    fun testSpotify() {

        val secureRandom = SecureRandom()
        val randomBytes = ByteArray(32) // ~43 characters when Base64 URL-encoded
        secureRandom.nextBytes(randomBytes)

        // Encode to Base64 URL-safe string
        val codeVerifier = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes).take(69)
        println(codeVerifier)
        val codeChallenge = getSpotifyPkceCodeChallenge(codeVerifier) // helper method
        val url: String = getSpotifyPkceAuthorizationUrl(
            SpotifyScope.AppRemoteControl,
            SpotifyScope.UserModifyPlaybackState,
            SpotifyScope.UserReadPlaybackState,
            clientId = "a9e2b8d829d648f7ac6fac3dce2567cd",
            redirectUri = "http://localhost:8080",
            codeChallenge = codeChallenge
        )

        KutilsClient.Spotify.initialize(
            "a9e2b8d829d648f7ac6fac3dce2567cd",
            codeVerifier
        )
        val clickableText = Text.literal("Click me to link Spotify!")
            .setStyle(
                Style.EMPTY
                    .withColor(Formatting.AQUA)
                    .withUnderline(true)
                    .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, url))
            )
        KutilsClient.Log(clickableText)

    }
}