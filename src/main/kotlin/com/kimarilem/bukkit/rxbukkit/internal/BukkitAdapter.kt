package com.kimarilem.bukkit.rxbukkit.internal

import com.kimarilem.bukkit.rxbukkit.EventListenerData
import org.bukkit.command.CommandExecutor
import org.bukkit.command.PluginCommand
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin

internal interface BukkitAdapter {

	fun <T : Event> registerEvent(
		plugin: Plugin,
		eventClass: Class<T>,
		eventListenerData: EventListenerData,
		listener: Listener,
		eventExecutor: EventExecutor
	)

	fun registerCommand(command: PluginCommand, commandExecutor: CommandExecutor)

	fun getCommand(plugin: Plugin, command: String): PluginCommand?
}

internal class DefaultBukkitAdapter : BukkitAdapter {

	override fun <T : Event> registerEvent(
		plugin: Plugin,
		eventClass: Class<T>,
		eventListenerData: EventListenerData,
		listener: Listener,
		eventExecutor: EventExecutor
	) {
		plugin.server.pluginManager.registerEvent(
			eventClass,
			listener,
			eventListenerData.priority,
			eventExecutor,
			plugin,
			eventListenerData.shouldIgnoreCancelled
		)
	}

	override fun registerCommand(command: PluginCommand, commandExecutor: CommandExecutor) {
		command.executor = commandExecutor
	}

	override fun getCommand(plugin: Plugin, command: String): PluginCommand? =
		plugin.server.getPluginCommand(
			"${plugin.description.name.toLowerCase()}:${command.toLowerCase()}"
		).let {
			when (it?.plugin) {
				plugin -> it
				else -> null
			}
		}
}