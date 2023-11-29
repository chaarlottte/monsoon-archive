package me.bush.eventbuskotlin

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

open class TestEventBus(private val config: Config = Config()) {
    private val listeners = ConcurrentHashMap<KClass<*>, ListenerGroup>()
    private val subscribers = ConcurrentHashMap.newKeySet<Any>()

    fun subscribe(subscriber: Any): Boolean = subscribers.add(subscriber).also {
        if (it) getListeners(subscriber, config)?.forEach(::register) ?: return false
    }

    fun unsubscribe(subscriber: Any): Boolean = subscribers.remove(subscriber).also {
        if (it) getListeners(subscriber, config)?.forEach(::unregister)
    }

    fun register(listener: Listener): Boolean = listeners.computeIfAbsent(listener.type) {
        ListenerGroup(it, config)
    }.register(listener)

    fun unregister(listener: Listener): Boolean = listeners[listener.type]?.let {
        val contained = it.unregister(listener)
        if (it.parallel.isEmpty() && it.sequential.isEmpty()) {
            listeners.remove(listener.type)
        }
        contained
    } ?: false

    fun post(event: Any): Boolean = listeners[event::class]?.post(event) ?: false

    fun debug() {
        config.logger.info("Subscribers: ${subscribers.size}")
        val sequential = listeners.values.sumOf { it.sequential.size }
        val parallel = listeners.values.sumOf { it.parallel.size }
        config.logger.info("Listeners: $sequential sequential, $parallel parallel")
        listeners.values.sortedByDescending { it.sequential.size + it.parallel.size }.forEach {
            config.logger.info(it.toString())
        }
    }

    fun reload() {

    }
}
