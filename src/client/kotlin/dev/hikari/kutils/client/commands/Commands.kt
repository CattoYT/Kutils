package dev.hikari.kutils.client.commands


import com.mojang.brigadier.CommandDispatcher
import dev.hikari.kutils.client.KutilsClient
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource


class Commands {
    fun register() {
        KutilsClient.logger.info("Registering cocmmands")

        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, _, _ ->
            dispatcher.register(
                CommandManager.literal("test_command")
                    .executes { context ->
                        KutilsClient.logger.info("Test command executed")
                        1
                    }
            )
        })

    }


}