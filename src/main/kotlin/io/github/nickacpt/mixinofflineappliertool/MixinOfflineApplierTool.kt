package io.github.nickacpt.mixinofflineappliertool

import io.github.nickacpt.mixinofflineappliertool.mixins.MixinServiceImpl
import io.github.nickacpt.mixinofflineappliertool.mixins.MixinTransformer
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.launch.MixinBootstrap
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.Mixins
import org.spongepowered.asm.transformers.MixinClassWriter
import java.io.File
import java.util.jar.JarFile

object MixinOfflineApplierTool {

    var side: MixinSide = MixinSide.UNKNOWN

    val classPath = mutableListOf<String>()

    fun getJarClasses(file: File): MutableMap<String, ClassNode> {
        val resultNodes = mutableMapOf<String, ClassNode>()
        val jar = JarFile(file)
        jar.entries().asIterator().forEachRemaining {
            if (it.realName.endsWith(".class")) {
                val reader = jar.getInputStream(it).use { ClassReader(it) }
                val node = ClassNode()
                reader.accept(node, 0)

                resultNodes[it.realName] = node
            }
        }

        jar.close()
        return resultNodes
    }

    fun apply(
        input: File,
        mixinInput: File,
        configurations: List<String>,
        side: MixinSide,
        output: File,
        classPath: List<File>
    ): List<String> {
        val modifiedEntries = mutableListOf<String>()
        this.side = side
        output.mkdirs()

        val inputClasses = getJarClasses(input)
        this.classPath.add(mixinInput.path)
        this.classPath.addAll(classPath.map { it.path })
        MixinBootstrap.init()
        MixinBootstrap.getPlatform().inject()

        configurations.forEach {
            Mixins.addConfiguration(it)
        }

        MixinServiceImpl.gotoPhase(MixinEnvironment.Phase.DEFAULT)

        inputClasses.forEach { (entryName, it) ->
            if (input == mixinInput && it.invisibleAnnotations?.any { it.desc == mixinAnnotationDescriptor } == true) {
                println("Skipping ${it.name} - Mixin class")
                return@forEach
            }
            val modified = MixinTransformer.transform(it)
            if (modified)
                println("Finished processing ${it.name}")

            if (modified) {
                val writer = MixinClassWriter(ClassWriter.COMPUTE_MAXS.or(ClassWriter.COMPUTE_FRAMES))
                it.accept(writer)
                val outputBytes = writer.toByteArray()
                File(output, it.name.replace('/', File.separatorChar) + ".class").also { it.parentFile.mkdirs() }
                    .writeBytes(outputBytes)

                modifiedEntries.add(entryName)
            }
        }
        this.classPath.clear()
        inputClasses.clear()

        return modifiedEntries
    }
}