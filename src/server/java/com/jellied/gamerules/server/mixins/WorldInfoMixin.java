package com.jellied.gamerules.server.mixins;

import com.jellied.gamerules.WorldInfoAccessor;
import net.minecraft.src.game.level.WorldInfo;
import net.minecraft.src.game.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldInfo.class)
public abstract class WorldInfoMixin implements WorldInfoAccessor {
    private NBTTagCompound gamerulesTag;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onWorldInfoConstructed(NBTTagCompound tag, CallbackInfo ci) {
        if (tag.hasKey("jelliedgamerules")) {
            gamerulesTag = tag.getCompoundTag("jelliedgamerules");
        }
        else {
            gamerulesTag = new NBTTagCompound();
            tag.setCompoundTag("jelliedgamerules", gamerulesTag);
        }
    }

    @Inject(method = "saveNBTTag", at = @At("TAIL"))
    public void onNBTSave(NBTTagCompound newTag, NBTTagCompound plrTag, CallbackInfo ci) {
        newTag.setCompoundTag("jelliedgamerules", gamerulesTag);
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
