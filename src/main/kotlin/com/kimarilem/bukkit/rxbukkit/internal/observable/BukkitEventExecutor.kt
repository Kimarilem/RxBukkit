package com.kimarilem.bukkit.rxbukkit.internal.observable

import io.reactivex.Emitter
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor

internal class BukkitEventExecutor<T : Event>(private val emitter: Emitter<T>) : EventExecutor {

    /*
     * This cast is safe as long as Bukkit never calls this executor with another type than it was registered with
     * If this happens, it should be considered a bug in Bukkit
     */
    @Suppress("UNCHECKED_CAST")
    override fun execute(listener: Listener, event: Event) {
        emitter.onNext(event as T)
    }
}
