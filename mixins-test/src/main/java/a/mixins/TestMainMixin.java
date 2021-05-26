package a.mixins;

import a.TestMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TestMain.class)
public class TestMainMixin {

    @Inject(method = "main", remap = false, at= @At("RETURN"))
    private static void onAfterMain(String[] args, CallbackInfo ci) {
        System.out.println("Hello from mixins!");
    }

}
