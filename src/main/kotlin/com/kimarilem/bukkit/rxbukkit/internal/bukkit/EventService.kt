package com.kimarilem.bukkit.rxbukkit.internal.bukkit

import com.kimarilem.bukkit.rxbukkit.EventListenerData
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin

internal interface EventService {

    fun <T : Event> registerEvent(
        plugin: Plugin,
        eventClass: Class<T>,
        eventListenerData: EventListenerData,
        eventExecutor: EventExecutor,
        listener: Listener
    )

    fun deregisterEvents(listener: Listener)
}

internal class BukkitEventService : EventService {

    override fun <T : Event> registerEvent(
        plugin: Plugin,
        eventClass: Class<T>,
        eventListenerData: EventListenerData,
        eventExecutor: EventExecutor,
        listener: Listener

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

    override fun deregisterEvents(listener: Listener) {
        HandlerList.unregisterAll(listener)
    }
}
