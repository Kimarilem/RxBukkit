package com.kimarilem.bukkit.rxbukkit.internal.bukkit

import com.kimarilem.bukkit.rxbukkit.EventListenerData
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin

internal interface EventService {

    fun <T : Event> registerEvent(
        plugin: Plugin,
        eventClass: Class<T>,
        eventListenerData: EventListenerData,
        eventExecutor: EventExecutor
    )
}

internal class BukkitEventService : EventService {

    override fun <T : Event> registerEvent(
        plugin: Plugin,
        eventClass: Class<T>,
        eventListenerData: EventListenerData,
        eventExecutor: EventExecutor
    ) {
        plugin.server.pluginManager.registerEvent(
            eventClass,
            object : Listener {},
            eventListenerData.priority,
            eventExecutor,
            plugin,
            eventListenerData.shouldIgnoreCancelled
        )
    }
}
