package io.github.nickacpt.mixinofflineappliertool.mixins

import org.spongepowered.asm.service.IClassProvider
import java.net.URL

class ClassProviderImpl(val classLoader: OfflineClassLoader) : IClassProvider {
    override fun getClassPath(): Array<URL> {
        return classLoader.urLs
    }

    override fun findClass(name: String): Class<*> {
        return classLoader.findClass(name)
    }

    override fun findClass(name: String?, initialize: Boolean): Class<*> {
        return Class.forName(name, initialize, classLoader);
    }

    override fun findAgentClass(name: String?, initialize: Boolean): Class<*> {
        return Class.forName(name, initialize, classLoader);
    }
}