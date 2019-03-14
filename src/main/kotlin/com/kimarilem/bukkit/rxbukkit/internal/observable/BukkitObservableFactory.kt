package com.kimarilem.bukkit.rxbukkit.internal.observable

import com.kimarilem.bukkit.rxbukkit.CommandEvent
import com.kimarilem.bukkit.rxbukkit.EventListenerData
import com.kimarilem.bukkit.rxbukkit.internal.bukkit.CommandService
import com.kimarilem.bukkit.rxbukkit.internal.bukkit.EventService
import io.reactivex.Emitter
import io.reactivex.Observable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin

internal class BukkitObservableFactory(
    private val eventService: EventService,
    private val commandService: CommandService
) {

    fun <T : Event> createEventObservable(
        plugin: Plugin,
        eventClass: Class<T>,
        eventListenerData: EventListenerData
    ) = Observable.create<T> { emitter ->
        val eventExecutor = BukkitEventExecutor<T>(emitter)
        val listener = object : Listener {}

        eventService.registerEvent(plugin, eventClass, eventListenerData, eventExecutor, listener)
        registerDisableEvent(plugin, emitter, listener)

        emitter.setCancellable {
            eventService.deregisterEvents(listener)
        }
    }

    fun createCommandObservable(plugin: Plugin, command: String) = Observable.create<CommandEvent> { emitter ->
        val commandExecutor = BukkitCommandExecutor(emitter)
        val pluginCommand = commandService.getCommand(plugin, command)
            ?: throw NullPointerException("Command '$command' not registered")
        val listener = object : Listener {}

        commandService.registerCommand(pluginCommand, commandExecutor)
        registerDisableEvent(plugin, emitter, listener)

        emitter.setCancellable {
            commandService.deregisterCommand(pluginCommand)
            eventService.deregisterEvents(listener)
        }
    }

    private fun <T> registerDisableEvent(plugin: Plugin, emitter: Emitter<T>, listener: Listener) {
        val disableEventExecutor = BukkitPluginDisableEventExecutor(plugin, emitter)

        eventService.registerEvent(
            plugin,
            PluginDisableEvent::class.java,
            EventListenerData(EventPriority.MONITOR, false),
            disableEventExecutor,
            listener
        )
    }
}
