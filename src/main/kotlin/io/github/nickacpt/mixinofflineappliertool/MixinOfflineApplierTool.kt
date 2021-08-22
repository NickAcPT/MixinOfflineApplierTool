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
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

object MixinOfflineApplierTool {

    var side: MixinSide = MixinSide.UNKNOWN

    val classPath = mutableListOf<String>()

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
        return FileSystems.newFileSystem(input.toPath()).use { inputFs ->
            this.classPath.add(mixinInput.path)
            this.classPath.addAll(classPath.map { it.path })
            MixinBootstrap.init()
            MixinBootstrap.getPlatform().inject()

            configurations.forEach {
                Mixins.addConfiguration(it)
            }

            MixinServiceImpl.gotoPhase(MixinEnvironment.Phase.DEFAULT)

            Files.walkFileTree(inputFs.getPath("/"), object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
                    val name = file.toString()
                    if (name.endsWith(".class")) {
                        val reader = ClassReader(Files.readAllBytes(file))
                        val node = ClassNode()
                        reader.accept(node, 0)

                        if (input == mixinInput && node.invisibleAnnotations?.any { it.desc == mixinAnnotationDescriptor } == true) {
                            println("Skipping ${node.name} - Mixin class")
                            return FileVisitResult.CONTINUE
                        }
                        val modified = MixinTransformer.transform(node)
                        if (modified)
                            println("Finished processing ${node.name}")

                        if (modified) {
                            val writer = MixinClassWriter(ClassWriter.COMPUTE_MAXS.or(ClassWriter.COMPUTE_FRAMES))
                            node.accept(writer)
                            val outputBytes = writer.toByteArray()
                            File(output, node.name.replace('/', File.separatorChar) + ".class").also { it.parentFile.mkdirs() }
                                .writeBytes(outputBytes)

                            modifiedEntries.add(name)
                        }

                    }
                    return FileVisitResult.CONTINUE
                }
            })
            this.classPath.clear()

            return@use modifiedEntries
        }
    }
}