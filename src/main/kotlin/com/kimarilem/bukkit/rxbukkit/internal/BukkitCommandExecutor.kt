package com.kimarilem.bukkit.rxbukkit.internal

import com.kimarilem.bukkit.rxbukkit.CommandEvent
import io.reactivex.Emitter
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

internal class BukkitCommandExecutor(private val emitter: Emitter<CommandEvent>) : CommandExecutor {

	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
		try {
			emitter.onNext(CommandEvent(command, sender, label, args.asList()))
		} catch (t: Throwable) {
			return false
		}
		return true
	}
}