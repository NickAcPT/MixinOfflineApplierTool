package io.github.nickacpt.mixinofflineappliertool.mixins

import io.github.nickacpt.mixinofflineappliertool.MixinOfflineApplierTool
import io.github.nickacpt.mixinofflineappliertool.tool
import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual
import org.spongepowered.asm.launch.platform.container.IContainerHandle
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.service.*
import org.spongepowered.asm.util.IConsumer
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.jar.JarFile

class MixinServiceImpl : MixinServiceAbstract() {
    companion object {
        lateinit var instance: MixinServiceImpl

        fun gotoPhase(phase: MixinEnvironment.Phase) {
            instance.phaseConsumer?.accept(phase)
        }
    }

    init {
        instance = this
    }

    private val trail = MixinAuditTrailImpl()
    val classLoader = OfflineClassLoader()
    val classProvider = ClassProviderImpl(classLoader)

    val bytecodeProvider = ClassBytecodeProviderImpl()

    override fun getName(): String {
        return tool
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun getClassProvider(): IClassProvider {
        return classProvider
    }

    override fun getBytecodeProvider(): IClassBytecodeProvider {
        return bytecodeProvider
    }

    override fun getTransformerProvider(): ITransformerProvider? {
        return null
    }

    override fun getClassTracker(): IClassTracker? {
        return null
    }

    override fun getAuditTrail(): IMixinAuditTrail = trail

    override fun getPlatformAgents(): MutableCollection<String> =
        mutableListOf("io.github.nickacpt.mixinofflineappliertool.mixins.MixinPlatformServiceAgentImpl")

    override fun getPrimaryContainer(): IContainerHandle {
        return ContainerHandleVirtual(tool)
    }

    override fun getResourceAsStream(name: String): InputStream? {
        MixinOfflineApplierTool.classPath.forEach { jarPath ->
            JarFile(jarPath).use { jar ->
                jar.getJarEntry(name)?.let { jar.getInputStream(it) }?.let { return it.use { ByteArrayInputStream(it.readAllBytes()) } }
            }
        }
        return null
    }

    private var phaseConsumer: IConsumer<MixinEnvironment.Phase>? = null
    override fun wire(phase: MixinEnvironment.Phase?, phaseConsumer: IConsumer<MixinEnvironment.Phase>) {
        super.wire(phase, phaseConsumer)
        this.phaseConsumer = phaseConsumer
    }

    private fun gotoPhase(phase: MixinEnvironment.Phase) {
        phaseConsumer?.accept(phase)
    }
}