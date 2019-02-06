package com.kimarilem.bukkit.rxbukkit.internal.observable

import io.reactivex.Emitter
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.Plugin

internal class BukkitPluginDisableEventExecutor(
    private val plugin: Plugin,
    private val emitter: Emitter<*>
) : EventExecutor {

    override fun execute(listener: Listener, event: Event) {
        if ((event as PluginDisableEvent).plugin == plugin) {
            emitter.onComplete()
        }
    }
}
