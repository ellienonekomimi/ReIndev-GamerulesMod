package com.jellied.gamerules.server.mixins;

import com.jellied.gamerules.GamerulesServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.game.level.ISaveFormat;
import net.minecraft.src.game.level.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow public abstract WorldServer getWorldManager(int arg1);

    @Inject(method = "initWorld", at = @At("TAIL"))
    public void onWorldInit(ISaveFormat saveFormat, String arg2, long arg3, CallbackInfo ci) {
        GamerulesServer.onWorldInit(this.getWorldManager(0));
    }
}
