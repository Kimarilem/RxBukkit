package com.kimarilem.bukkit.rxbukkit.internal.bukkit

import org.bukkit.command.CommandExecutor
import org.bukkit.command.PluginCommand

internal interface CommandService {

    fun registerCommand(command: PluginCommand, commandExecutor: CommandExecutor)
    fun deregisterCommand(command: PluginCommand)
}

internal class BukkitCommandService : CommandService {

    override fun registerCommand(command: PluginCommand, commandExecutor: CommandExecutor) {
        command.setExecutor(commandExecutor)
    }

    override fun deregisterCommand(command: PluginCommand) {
        command.setExecutor(null)
    }
}
