package io.github.nickacpt.mixinofflineappliertool.mixins

import org.spongepowered.asm.service.IGlobalPropertyService
import org.spongepowered.asm.service.IPropertyKey
import java.util.*

/**
 * Global properties service for Mixin
 */
class GlobalPropertyServiceImpl : IGlobalPropertyService {
    private class BasicProperty(private val name: String) : IPropertyKey {
        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false
            val that = o as BasicProperty
            return name == that.name
        }

        override fun hashCode(): Int {
            return Objects.hash(name)
        }

        override fun toString(): String {
            return "BasicProperty{" +
                    "name='" + name + '\'' +
                    '}'
        }
    }

    private val keys: MutableMap<String, IPropertyKey> = HashMap()
    private val values: MutableMap<IPropertyKey, Any> = HashMap()
    override fun resolveKey(name: String): IPropertyKey {
        return keys.computeIfAbsent(name) { name: String -> BasicProperty(name) }
    }

    override fun <T> getProperty(key: IPropertyKey): T {
        return values[key] as T
    }

    override fun setProperty(key: IPropertyKey, value: Any) {
        values[key] = value
    }

    override fun <T> getProperty(key: IPropertyKey, defaultValue: T): T {
        return values.getOrDefault(key, defaultValue!!) as T
    }

    override fun getPropertyString(key: IPropertyKey, defaultValue: String): String {
        return values.getOrDefault(key, defaultValue) as String
    }
}