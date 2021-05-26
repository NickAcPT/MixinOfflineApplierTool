package io.github.nickacpt.mixinofflineappliertool.mixins

import io.github.nickacpt.mixinofflineappliertool.MixinOfflineApplierTool
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.service.IClassBytecodeProvider
import java.io.IOException


class ClassBytecodeProviderImpl : IClassBytecodeProvider {
    override fun getClassNode(name: String): ClassNode {
        return MixinOfflineApplierTool.loadedClasses[name.removeSuffix(".class").replace(".", "/")] ?: kotlin.run {
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

    override fun getClassNode(name: String, runTransformers: Boolean): ClassNode {
        return MixinOfflineApplierTool.loadedClasses[name.removeSuffix(".class").replace(".", "/")]!!
    }
}