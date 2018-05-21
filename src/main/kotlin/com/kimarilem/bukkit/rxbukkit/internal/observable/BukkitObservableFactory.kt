package com.kimarilem.bukkit.rxbukkit.internal.observable

import com.kimarilem.bukkit.rxbukkit.CommandEvent
import com.kimarilem.bukkit.rxbukkit.EventListenerData
import com.kimarilem.bukkit.rxbukkit.internal.BukkitAdapter
import io.reactivex.Emitter
import io.reactivex.Observable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin

internal class BukkitObservableFactory(private val bukkitAdapter: BukkitAdapter) {

	fun <T : Event> createEventObservable(
		plugin: Plugin,
		eventClass: Class<T>,
		eventListenerData: EventListenerData
	): Observable<T> = Observable.create<T> { emitter ->
		val listener = object : Listener {}
		val eventExecutor = BukkitEventExecutor<T>(emitter, eventClass)

		bukkitAdapter.registerEvent(plugin, eventClass, eventListenerData, listener, eventExecutor)
		registerDisableEvent(plugin, emitter, listener)
	}


	fun createCommandObservable(plugin: Plugin, command: String): Observable<CommandEvent> =
		Observable.create<CommandEvent> { emitter ->
			val commandExecutor = BukkitCommandExecutor(emitter)
			val pluginCommand = bukkitAdapter.getCommand(plugin, command)
				?: throw NullPointerException("Command '$command' not registered")

			bukkitAdapter.registerCommand(pluginCommand, commandExecutor)
		}

	private fun <T : Event> registerDisableEvent(plugin: Plugin, emitter: Emitter<T>, listener: Listener) {
		val disableEventExecutor = BukkitPluginDisableEventExecutor(plugin, emitter)

		bukkitAdapter.registerEvent(
			plugin,
			PluginDisableEvent::class.java,
			EventListenerData(EventPriority.MONITOR, false),
			listener,
			disableEventExecutor
		)
	}
}
