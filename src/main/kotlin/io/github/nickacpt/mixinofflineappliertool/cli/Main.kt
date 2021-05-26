package io.github.nickacpt.mixinofflineappliertool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.file

class MixinOfflineApplierCommand : CliktCommand() {
    val input by argument(help = "The input jar that will get its classes modified").file(
        mustExist = true,
        canBeFile = true,
        canBeDir = false,
        mustBeWritable = false,
        mustBeReadable = true
    )

    val mixinInput by argument(help = "The mixin jar").file(
        mustExist = true,
        canBeFile = true,
        canBeDir = false,
        mustBeWritable = false,
        mustBeReadable = true
    )

    val configurations by argument(help = "The Mixin configurations to apply").multiple()

    val side by argument(help= "The side to apply mixins to").enum<MixinSide>().default(MixinSide.CLIENT)

    val output by argument(help = "The output directory where modified classes will be saved").file(
        canBeFile = false,
        canBeDir = true
    )

    val classPath by option(help = "The classpath of the input").file(
        mustExist = true,
        canBeFile = true,
        canBeDir = false,
        mustBeWritable = false,
        mustBeReadable = true
    ).multiple()

    override fun run() {
        MixinOfflineApplierTool.apply(input, mixinInput, configurations, side, output, classPath)
    }
}

fun main(args: Array<String>) {
    MixinOfflineApplierCommand().main(args)
}