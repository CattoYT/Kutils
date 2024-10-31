package dev.hikari.kutils.client.commands

import com.adamratzman.spotify.*
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import dev.hikari.kutils.client.KutilsClient
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.ClickEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

class Test {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("testspotify")
                    .then(
                        CommandManager.argument("ClientID", StringArgumentType.string())
                            .then(
                                CommandManager.argument("SecretKey", StringArgumentType.string())
                                    .executes {
                                        //KutilsClient.logger.info("Test command executed")
                                        context -> testSpotify()
                                        1
                                    }
                            )
                    )
            )


        })

    }

    fun testSpotify() {

        KutilsClient.logger.info("Test Spotify command executed")


        val codeVerifier = "thisisaveryrandomalphanumericcodeverifierandisgreaterthan43characters"
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