package com.kimarilem.bukkit.rxbukkit.internal

import com.kimarilem.bukkit.rxbukkit.EventListenerData
import io.kotlintest.TestCaseContext
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.mock.mock
import io.kotlintest.specs.WordSpec
import io.reactivex.Observable
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerEggThrowEvent
import org.bukkit.plugin.Plugin
import org.mockito.Mockito.reset

internal class BukkitObservableFactoryTest : WordSpec() {

	val pluginMock = mock<Plugin>()
	val bukkitAdapterMock = mock<BukkitAdapter>()
	val bukkitObservableFactory = BukkitObservableFactory(bukkitAdapterMock)

	init {
		"createEventObservable" should {
			"return an Observable" {
				(bukkitObservableFactory.createEventObservable(
					pluginMock,
					PlayerEggThrowEvent::class.java,
					EventListenerData(EventPriority.NORMAL, false)
				)) shouldNotBe null
			}
		}
	}

	override fun interceptTestCase(context: TestCaseContext, test: () -> Unit) {
		test()
		reset(pluginMock, bukkitAdapterMock)
	}
}