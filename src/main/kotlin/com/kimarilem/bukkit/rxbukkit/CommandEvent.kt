package com.kimarilem.bukkit.rxbukkit

import org.bukkit.command.Command
import org.bukkit.command.CommandSender

/**
 * This class wraps the parameters of [org.bukkit.command.CommandExecutor.onCommand].
 *
 * @property cmd command which was executed
 * @property sender source of the command
 * @property label alias of the command which was used
 * @property args passed command arguments
 * @constructor
 * @see org.bukkit.command.CommandExecutor.onCommand
 */
@Suppress("MemberVisibilityCanBePrivate")
data class CommandEvent(
    val cmd: Command,
    val sender: CommandSender,
    val label: String,
    val args: List<String>
)
