package com.kimarilem.bukkit.rxbukkit

import org.bukkit.event.EventPriority

/**
 * This class contains the arguments required for registering an event listener in Bukkit.
 *
 * @property priority the priority to register the event listener at
 * @property shouldIgnoreCancelled whether the event listener should ignore a cancelled event
 * @constructor
 */
data class EventListenerData(val priority: EventPriority, val shouldIgnoreCancelled: Boolean)
