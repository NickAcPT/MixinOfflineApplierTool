package io.github.nickacpt.mixinofflineappliertool.mixins

import io.github.nickacpt.mixinofflineappliertool.MixinOfflineApplierTool
import io.github.nickacpt.mixinofflineappliertool.MixinSide
import org.spongepowered.asm.launch.platform.IMixinPlatformAgent
import org.spongepowered.asm.launch.platform.IMixinPlatformServiceAgent
import org.spongepowered.asm.launch.platform.MixinPlatformAgentAbstract
import org.spongepowered.asm.launch.platform.MixinPlatformManager
import org.spongepowered.asm.launch.platform.container.IContainerHandle
import org.spongepowered.asm.util.Constants

class MixinPlatformServiceAgentImpl : MixinPlatformAgentAbstract(), IMixinPlatformServiceAgent {
    override fun init() {
    }

    override fun getSideName(): String {
        return when (MixinOfflineApplierTool.side) {
            MixinSide.UNKNOWN -> Constants.SIDE_UNKNOWN
            MixinSide.CLIENT -> Constants.SIDE_CLIENT
            MixinSide.SERVER -> Constants.SIDE_SERVER
            MixinSide.DEDICATEDSERVER -> Constants.SIDE_DEDICATEDSERVER
        }
    }

    override fun accept(manager: MixinPlatformManager?, handle: IContainerHandle?): IMixinPlatformAgent.AcceptResult {
        return IMixinPlatformAgent.AcceptResult.ACCEPTED
    }

    override fun getMixinContainers(): MutableCollection<IContainerHandle>? {
        return null
    }
}