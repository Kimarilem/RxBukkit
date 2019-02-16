package com.kimarilem.bukkit.rxbukkit.internal.bukkit

import org.bukkit.command.CommandExecutor
import org.bukkit.command.PluginCommand
import org.bukkit.plugin.Plugin

internal interface CommandService {

    fun registerCommand(command: PluginCommand, commandExecutor: CommandExecutor)
    fun getCommand(plugin: Plugin, command: String): PluginCommand?
}

internal class BukkitCommandService : CommandService {

    override fun registerCommand(command: PluginCommand, commandExecutor: CommandExecutor) {
        command.executor = commandExecutor
    }

    override fun getCommand(plugin: Plugin, command: String) =
        plugin.server.getPluginCommand(
            "${plugin.description.name.toLowerCase()}:${command.toLowerCase()}"
        ).let {
            when (it?.plugin) {
                plugin -> it
                else -> null
            }
        }
}
