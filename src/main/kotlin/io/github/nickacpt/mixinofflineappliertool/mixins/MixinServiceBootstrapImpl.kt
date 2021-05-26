package io.github.nickacpt.mixinofflineappliertool.mixins

import io.github.nickacpt.mixinofflineappliertool.tool
import org.spongepowered.asm.service.IMixinServiceBootstrap

class MixinServiceBootstrapImpl : IMixinServiceBootstrap {
    override fun getName(): String {
        return tool
    }

    override fun getServiceClassName(): String {
        return "io.github.nickacpt.mixinofflineappliertool.mixins.MixinServiceImpl"
    }

    override fun bootstrap() {
    }

}