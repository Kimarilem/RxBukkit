package com.kimarilem.bukkit.rxbukkit.internal.observable

import com.kimarilem.bukkit.rxbukkit.CommandEvent
import com.kimarilem.bukkit.rxbukkit.EventListenerData
import com.kimarilem.bukkit.rxbukkit.internal.bukkit.CommandService
import com.kimarilem.bukkit.rxbukkit.internal.bukkit.EventService
import io.reactivex.Emitter
import io.reactivex.Observable
import org.bukkit.command.PluginCommand
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

    fun createCommandObservable(plugin: Plugin, command: PluginCommand) = Observable.create<CommandEvent> { emitter ->
        val commandExecutor = BukkitCommandExecutor(emitter)
        val listener = object : Listener {}

        commandService.registerCommand(command, commandExecutor)
        registerDisableEvent(plugin, emitter, listener)

        emitter.setCancellable {
            commandService.deregisterCommand(command)
            eventService.deregisterEvents(listener)
        }
    }

    private fun <T> registerDisableEvent(plugin: Plugin, emitter: Emitter<T>, listener: Listener) {
        val disableEventExecutor = BukkitPluginDisableEventExecutor(plugin, emitter)

        /*
         * The disable event is registered with MONITOR priority, which means the executor 'sees' the event as one of
         * the last executors. Since the PluginDisableEvent is not cancellable, the shouldIgnoreCancelled boolean isn't
         * relevant.
         *
         * Caveat: if you create an observable for a PluginDisableEvent with priority MONITOR, you may or may not see
         * an onNext for your own plugin depending on in which order Bukkit executes executors with the same priority.
         */
        eventService.registerEvent(
            plugin,
            PluginDisableEvent::class.java,
            EventListenerData(EventPriority.MONITOR, false),
            disableEventExecutor,
            listener
        )
    }
}
