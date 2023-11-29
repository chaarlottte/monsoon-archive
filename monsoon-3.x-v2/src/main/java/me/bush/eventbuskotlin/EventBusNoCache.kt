package me.bush.eventbuskotlin

import net.jodah.typetools.TypeResolver
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap

class EventBusNoCache : EventBus() {
    /* A map of instances to a list of handlers. */
    val registry = ConcurrentHashMap<Any, List<OneArgHandler>>()

    /**
     * Publish an event to all listeners that are listening for it
     *
     * @param event Any - The event that is being published.
     */
    override fun post(event: Any): Boolean {
        registry.values.asSequence().flatten().filter { it.isListeningFor(event) }.forEach { it.pub(event) }
        // println("posted ${event.javaClass.name} to ${registry.size} listeners hiiiiii!!!")
        return true
    }

    /**
     * If the instance has any listeners, add them to the registry
     *
     * @param subscriber Any — The instance of the class that you want to subscribe to.
     */
    override fun subscribe(subscriber: Any): Boolean {
        // println("requested subscribe for " + subscriber.javaClass.name + "!!")
        val listeners = collectListeners(subscriber)
        if (listeners.isNotEmpty()) {
            registry[subscriber] = listeners
            // println("wowza, added ${listeners.size} new listeners. truly amazing what technology is like today")
        } else {
            // println("wow! no listeners :(")
        }
        return registry.contains(listeners)
    }

    /**
     * Remove the given instance from the registry
     *
     * @param subscriber Any - The instance that you want to unsubscribe from the event.
     */
    override fun unsubscribe(subscriber: Any): Boolean  {
        registry.remove(subscriber)
        return !registry.contains(subscriber)
    }

    /**
     * Remove all the subscribers from the registry
     */
    fun unsubscribeAll() {
        registry.clear()
    }

    /**
     * If the type of the event handler is not of the type specified, throw an error
     *
     * @param typeStr The type of the event handler.
     */
    @Suppress("NOTHING_TO_INLINE")
    private inline fun errorNotOfType(typeStr: String): Nothing = error("Event handler not of type $typeStr")

    /**
     * Used by [EventBusNoCache.subscribe] to collect all the [OneArgHandler]s for a given [instance].
     *
     * traverses the given [instance], looking for fields annotated with [listener]
     * and returning a list of handlers
     *
     * @param instance The instance to collect the [OneArgHandler]s for.
     * @return A list of [OneArgHandler]'s.
     */
    private fun collectListeners(instance: Any, clazz: Class<*> = instance.javaClass, builder: MutableList<OneArgHandler> = mutableListOf()): List<OneArgHandler> {
        for (field in clazz.declaredFields) {
            if (field.isAnnotationPresent(EventListener::class.java)) {
                // println("the field " + field.name + " has @EventListener omg!!!! im so nervous im gonna be so happy omg")
                val fieldValue = getValueFromField(instance, field)
                builder.add(
                    handler(fieldValue)
                )
            }
        }
        // If the class has a superclass, we need to check it too
        clazz.superclass?.let { collectListeners(instance, it, builder) }
        return builder
    }

    // Helper functions for handlers
    private fun handler(value: Any) = tryCast<(Any) -> Any>(value)?.let { OneArgHandler(it, TypeResolver.resolveRawArguments(
        Function1::class.java, it.javaClass)[0]) } ?: errorNotOfType("(Any) -> Any")
    /**
     * Get the value of a field from an instance of a class
     *
     * @param instance The object that contains the field.
     * @param field The field to get the value from.
     */
    private fun getValueFromField(instance: Any, field: Field) = field.also { it.isAccessible = true }.get(instance)
    private inline fun <reified T> tryCast(instance: Any?): T? = if (instance is T) instance else null
}

/*
 * A handler that takes a single arguments.
 * ex:
 *
 * @Handler
 * val handler = { event: EventType -> ... }
 */
open class OneArgHandler(private val handler: (Any) -> Any, private val listeningFor: Class<*>) {
    open fun pub(obj: Any) {
        handler(obj)
    }

    fun isListeningFor(obj: Any) = this.listeningFor.isInstance(obj)
}
