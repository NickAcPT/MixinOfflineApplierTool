package io.github.nickacpt.mixinofflineappliertool.mixins

import io.github.nickacpt.mixinofflineappliertool.MixinOfflineApplierTool
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.service.IClassBytecodeProvider
import java.io.IOException
import java.util.jar.JarFile


class ClassBytecodeProviderImpl : IClassBytecodeProvider {
    override fun getClassNode(name: String): ClassNode {
        return getClass(name) ?: kotlin.run {
            val node = ClassNode()
            val reader: ClassReader
            try {
                reader = ClassReader(javaClass.classLoader.getResourceAsStream(name.replace(".", "/") + ".class"))
            } catch (e: IOException) {
                throw ClassNotFoundException("Could not load ClassNode with name $name", e)
            }
            reader.accept(node, 0)
            return node

        }
    }

    private fun getClass(name: String): ClassNode? {
        MixinOfflineApplierTool.classPath.forEach { classPathFile ->
            JarFile(classPathFile).use {
                val entry = it.getJarEntry(name.removeSuffix(".class").replace(".", "/") + ".class")
                if (entry != null) {
                    val node = ClassNode()
                    val reader: ClassReader

                    try {
                        reader = ClassReader(it.getInputStream(entry))
                    } catch (e: IOException) {
                        throw ClassNotFoundException("Could not load ClassNode with name $name", e)
                    }
                    reader.accept(node, 0)
                    return node
                }
            }
        }
        return null
    }

    override fun getClassNode(name: String, runTransformers: Boolean): ClassNode {
        return getClass(name) ?: throw ClassNotFoundException("Could not load ClassNode with name $name")
    }
}