package dev.hikari.kutils.client.commands

import com.adamratzman.spotify.*
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.hikari.kutils.client.KutilsClient
import dev.hikari.kutils.client.modules.Spotify
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

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
                                        context -> testSpotify(context)
                                        1
                                    }
                            )
                    )
            )


        })

    }

    fun testSpotify(context: CommandContext<ServerCommandSource>) {

        KutilsClient.logger.info("Test Spotify command executed")


        val codeVerifier = "thisisaveryrandomalphanumericcodeverifierandisgreaterthan43characters"
        val codeChallenge = getSpotifyPkceCodeChallenge(codeVerifier) // helper method
        val url: String = getSpotifyPkceAuthorizationUrl(
            SpotifyScope.AppRemoteControl,
            SpotifyScope.UserModifyPlaybackState,
            clientId = "a9e2b8d829d648f7ac6fac3dce2567cd",
            redirectUri = "http://localhost:8080",
            codeChallenge = codeChallenge
        )

        KutilsClient.Spotify.initialize(
            "a9e2b8d829d648f7ac6fac3dce2567cd",
            "b30875191a2b4d4bb8613a802b21516d",
            codeVerifier
        )

        context.source.sendFeedback({ Text.literal(url) }, false)

    }
}