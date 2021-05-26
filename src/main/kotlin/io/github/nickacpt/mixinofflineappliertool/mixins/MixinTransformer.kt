package io.github.nickacpt.mixinofflineappliertool.mixins

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.transformers.TreeTransformer
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


object MixinTransformer {

    /**
     * Call MixinTransformer's transformClass
     */
    private var transformClassMethod: Method? = null
    private var transformer: TreeTransformer? = null

    init {
        try {
            // MixinTransformer is package-protected, so we have to force to gain access
            val mixinTransformerClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer")
            val ctor = mixinTransformerClass.getDeclaredConstructor()
            ctor.isAccessible = true
            transformer = ctor.newInstance() as TreeTransformer

            // we can't access the MixinTransformer type here, so we use reflection to access the method
            transformClassMethod = mixinTransformerClass.getDeclaredMethod(
                "transformClass",
                MixinEnvironment::class.java,
                String::class.java,
                ClassNode::class.java
            )

            transformClassMethod!!.isAccessible = true
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("Failed to initialize MixinCodeModifier", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Failed to initialize MixinCodeModifier", e)
        } catch (e: InstantiationException) {
            throw RuntimeException("Failed to initialize MixinCodeModifier", e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException("Failed to initialize MixinCodeModifier", e)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException("Failed to initialize MixinCodeModifier", e)
        }
    }

    fun transform(source: ClassNode): Boolean {
        try {
            return transformClassMethod!!.invoke(
                transformer,
                MixinEnvironment.getEnvironment(MixinEnvironment.Phase.DEFAULT),
                source.name.replace("/", "."),
                source
            ) as Boolean
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return false
    }

}