package com.jellied.gamerules.client.mixins;

import com.jellied.gamerules.WorldInfoAccessorClient;
import net.minecraft.src.game.level.WorldInfo;
import net.minecraft.src.game.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldInfo.class)
public abstract class WorldInfoMixin implements WorldInfoAccessorClient {
    private NBTTagCompound gamerulesTag;

    @Inject(method = "<init>*", at = @At("TAIL"))
    public void onWorldInfoConstructedWithTag(NBTTagCompound tag, CallbackInfo ci) {
        // System.out.println("New world info!");

        if (tag.hasKey("jelliedgamerules")) {
            gamerulesTag = tag.getCompoundTag("jelliedgamerules");
        }
        else {
            gamerulesTag = new NBTTagCompound();
            tag.setCompoundTag("jelliedgamerules", gamerulesTag);
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/src/game/level/WorldInfo;)V", at = @At("TAIL"))
    public void onWorldInfoConstructedWithWorldInfo(WorldInfo worldInfo, CallbackInfo ci) {
        gamerulesTag = ((WorldInfoAccessorClient) worldInfo).getGamerules();
        if (gamerulesTag == null) {
            gamerulesTag = new NBTTagCompound();
        }

        this.setGamerules(gamerulesTag);
    }

    @Inject(method = "updateTagCompound", at = @At("TAIL"))
    public void onWorldInfoUpdated(NBTTagCompound newTag, NBTTagCompound plrTag, CallbackInfo ci) {
        if (gamerulesTag != null) {
            newTag.setCompoundTag("jelliedgamerules", gamerulesTag);
        }
    }

    @Override
    public NBTTagCompound getGamerules() {
        return gamerulesTag;
    }

    @Override
    public void setGamerules(NBTTagCompound newGamerules) {
        gamerulesTag = newGamerules;
    }
}
