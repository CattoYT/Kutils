package dev.hikari.kutils.client.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.hikari.kutils.client.KutilsClient
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text

class LinkSpotify {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
        dispatcher.register(
            CommandManager.literal("linkspotify")
                .then(CommandManager.argument("ClientID", StringArgumentType.string())
                .then(CommandManager.argument("SecretKey", StringArgumentType.string())
                .executes { context ->
                    execute(
                        context,
                        StringArgumentType.getString(context, "ClientID"),
                        StringArgumentType.getString(context, "SecretKey"))
                }
                )))
        })
    }

    private fun execute(context: CommandContext<ServerCommandSource>, clientID: String, clientSecret: String) = runBlocking {
        KutilsClient.logger.info("Link Spotify command executed")

        context.source.sendFeedback({ Text.literal("Use /test!") }, false)
        //if (KutilsClient.Spotify.createAppAPI(clientID, clientSecret) == 1) {
        //    context.source.sendFeedback({ Text.literal("Failed to link spotify!") }, false)
            1
        //} else {
        //    context.source.sendFeedback({ Text.literal("Successfully linked Spotify") } , false)
        //    0
        //}

    }
}