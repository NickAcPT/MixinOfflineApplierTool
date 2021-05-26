package io.github.nickacpt.mixinofflineappliertool

import io.github.nickacpt.mixinofflineappliertool.mixins.MixinServiceImpl
import io.github.nickacpt.mixinofflineappliertool.mixins.MixinTransformer
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.launch.MixinBootstrap
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.Mixins
import java.io.File
import java.util.jar.JarFile

object MixinOfflineApplierTool {

    var side: MixinSide = MixinSide.UNKNOWN

    val classPath = mutableListOf<String>()

    fun getJarClasses(file: File): List<ClassNode> {
        val resultNodes = mutableListOf<ClassNode>()
        val jar = JarFile(file)
        jar.entries().asIterator().forEachRemaining {
            if (it.realName.endsWith(".class")) {
                val reader = jar.getInputStream(it).use { ClassReader(it) }
                val node = ClassNode()
                reader.accept(node, 0)

                resultNodes.add(node)
            }
        }

        return resultNodes
    }

    fun apply(
        input: File,
        mixinInput: File,
        configurations: List<String>,
        side: MixinSide,
        output: File,
        classPath: List<File>
    ) {
        this.side = side
        output.mkdirs()

        val inputClasses = getJarClasses(input)
        this.classPath.addAll(classPath.map { it.path })
        MixinBootstrap.init()
        MixinBootstrap.getPlatform().inject()

        configurations.forEach {
            Mixins.addConfiguration(it)
        }

        MixinServiceImpl.gotoPhase(MixinEnvironment.Phase.DEFAULT)

        inputClasses.forEach {
            if (input == mixinInput && it.invisibleAnnotations?.any { it.desc == mixinAnnotationDescriptor } == true) {
                println("Skipping ${it.name} - Mixin class")
                return@forEach
            }
            println("Processing ${it.name}")
            val modified = MixinTransformer.transform(it)
            println("Finished processing ${it.name} - was modified: $modified")

            if (modified) {
                val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
                it.accept(writer)
                val outputBytes = writer.toByteArray()
                File(output, it.name.replace('/', File.separatorChar) + ".class").also { it.parentFile.mkdirs() }
                    .writeBytes(outputBytes)
                println("Saved modified ${it.name}")
            }
        }
    }
}