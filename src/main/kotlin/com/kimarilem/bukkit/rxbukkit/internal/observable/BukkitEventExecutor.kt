package com.kimarilem.bukkit.rxbukkit.internal.observable

import io.reactivex.Emitter
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor

@Suppress("UNCHECKED_CAST")
internal class BukkitEventExecutor<T : Event>(
	private val emitter: Emitter<T>,
	private val eventClass: Class<T>
) : EventExecutor {

	override fun execute(listener: Listener, event: Event) {
		if (event.javaClass.isAssignableFrom(eventClass)) {
			emitter.onNext(event as T)
		}
	}
}