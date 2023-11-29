package wtf.monsoon.backend.setting

import spritz.api.annotations.Excluded
import spritz.api.annotations.Identifier
import wtf.monsoon.backend.Feature
import wtf.monsoon.backend.module.mode.ModuleMode

/**
 * @author surge
 * @since 09/02/2023
 */
class Setting<T>(name: String, description: String, private var value: T) : Feature(name, description) {

    @Excluded val default = value

    @Excluded var minimum: T? = null
    @Excluded var maximum: T? = null
    @Excluded var incrementation: T? = null

    private var index: Int = 0

    @Excluded var parent: Setting<*>? = null
    @Excluded val children = mutableListOf<Setting<*>>()

    @Excluded var modes: List<ModuleMode<*>>? = null

    @Excluded var visibility: () -> Boolean = { true }

    constructor(name: String, description: String, value: T, minimum: T, maximum: T, incrementation: T) : this(name, description, value) {
        this.minimum = minimum
        this.maximum = maximum
        this.incrementation = incrementation
    }

    constructor(name: String, description: String, modes: List<ModuleMode<*>>) : this(name, description, modes[0] as T) {
        this.modes = modes;
    }

    constructor(name: String, value: T) : this(name, "No description set", value)

    @Identifier("get_value")
    fun getValue(): T {
        return value
    }

    @Identifier("set_value")
    fun setValue(value: T) {
        this.value = value
    }

    @Excluded
    infix fun childOf(parent: Setting<*>): Setting<T> {
        this.parent = parent
        this.parent!!.children.add(this)

        return this
    }

    @Excluded
    infix fun visibleWhen(condition: () -> Boolean): Setting<T> {
        this.visibility = condition
        return this
    }

    @Excluded
    infix fun minimum(minimum: T): Setting<T> {
        this.minimum = minimum
        return this
    }

    @Excluded
    infix fun maximum(maximum: T): Setting<T> {
        this.maximum = maximum
        return this
    }

    @Excluded
    infix fun incrementation(incrementation: T): Setting<T> {
        this.incrementation = incrementation
        return this
    }

    @Excluded
    val nextMode: T
        get() {
            return if(value is Enum<*>) {
                val enum = value as Enum<*>

                java.lang.Enum.valueOf(
                    enum::class.java,
                    enum.javaClass.enumConstants.map { it.name }[nextIndex]
                ) as T
            } else {
                this.modes?.get(nextIndex) as T
            }
        }

    @Excluded
    private val nextIndex: Int
        get() {
            if(value is Enum<*>) {
                val enum = value as Enum<*>

                if (index + 1 > enum.javaClass.enumConstants.size - 1) {
                    index = 0
                } else {
                    index += 1
                }

                return index
            } else {
                if (index + 1 > modes!!.size - 1) {
                    index = 0
                } else {
                    index += 1
                }

                return index
            }
        }

}