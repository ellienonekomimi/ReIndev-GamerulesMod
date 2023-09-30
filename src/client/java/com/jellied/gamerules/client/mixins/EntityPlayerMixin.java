package com.jellied.gamerules.client.mixins;

import com.jellied.gamerules.GamerulesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayer.class)
public class EntityPlayerMixin {
    Minecraft minecraft = Minecraft.getInstance();

    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/entity/player/InventoryPlayer;dropAllItems()V"))
    public void onDeath(InventoryPlayer instance) {
        if (GamerulesClient.getGamerule("keepInventory") != 1) {
            instance.dropAllItems();
        }
    }
}
