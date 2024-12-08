package dev.hikari.kutils.client.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import dev.hikari.kutils.client.KutilsClient
import kotlinx.coroutines.runBlocking
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

class Playing {
    fun register() {
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("playing")
                    .executes {
                        //KutilsClient.logger.info("Test command executed")
                            context ->
                                KutilsClient.Spotify.WhatIsPlaying()

                        0
                    }
            )

        })

    }


}