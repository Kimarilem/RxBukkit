package com.kimarilem.bukkit.rxbukkit

import com.kimarilem.bukkit.rxbukkit.internal.observable.BukkitObservableFactory
import com.kimarilem.bukkit.rxbukkit.internal.DefaultBukkitAdapter
import io.reactivex.Observable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.plugin.Plugin

/**
 * Object containing methods to create various observables.
 */
object RxBukkit {

	private val bukkitObservableFactory: BukkitObservableFactory = BukkitObservableFactory(DefaultBukkitAdapter())

	/**
	 * Create an observable for an event.
	 *
	 * @param T the type of the event
	 * @param plugin the plugin that needs to listen for the event
	 * @param eventListenerData the extra data for the event listener
	 */
	inline fun <reified T : Event> observeEvent(
		plugin: Plugin,
		eventListenerData: EventListenerData = EventListenerData(EventPriority.NORMAL, false)
	): Observable<T> {
		return observeEvent(plugin, T::class.java, eventListenerData)
	}

	/**
	 * Create an observable for an event.
	 *
	 * @param T the type of the event
	 * @param plugin the plugin that needs to listen for the event
	 * @param eventClass the class of the event
	 * @param eventListenerData the extra data for the event listener
	 */
	@JvmStatic
	fun <T : Event> observeEvent(
		plugin: Plugin,
		eventClass: Class<T>,
		eventListenerData: EventListenerData = EventListenerData(EventPriority.NORMAL, false)
	): Observable<T> {
		return bukkitObservableFactory.createEventObservable(plugin, eventClass, eventListenerData)
	}

	/**
	 * Create an observable for a command.
	 * The command needs to be specified in the plugin.yml.
	 *
	 * @param plugin the plugin to register the command for
	 * @param command the command to create the observable for
	 */
	@JvmStatic
	fun observeCommand(plugin: Plugin, command: String): Observable<CommandEvent> {
		return bukkitObservableFactory.createCommandObservable(plugin, command)
	}
}