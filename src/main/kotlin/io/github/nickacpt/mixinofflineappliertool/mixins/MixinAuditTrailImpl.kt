package io.github.nickacpt.mixinofflineappliertool.mixins

import org.spongepowered.asm.service.IMixinAuditTrail

class MixinAuditTrailImpl : IMixinAuditTrail {
    override fun onApply(className: String, mixinName: String) {
        println("Applied mixin $mixinName to class $className")
    }

    override fun onPostProcess(className: String?) {
        println("Post processing $className")
    }

    override fun onGenerate(className: String?, generatorName: String?) {
        println("Generating class $className via generator $generatorName")
    }
}