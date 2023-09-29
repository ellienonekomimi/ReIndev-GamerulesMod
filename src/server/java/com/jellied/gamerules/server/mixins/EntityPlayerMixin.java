package com.jellied.gamerules.server.mixins;

import com.jellied.gamerules.GamerulesServer;
import net.minecraft.src.game.entity.player.EntityPlayerMP;
import net.minecraft.src.game.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayerMP.class)
public class EntityPlayerMixin {
    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/entity/player/InventoryPlayer;dropAllItems()V"))
    public void onDeath(InventoryPlayer instance) {
        if (GamerulesServer.getGamerule("keepInventory") != 1) {
            instance.dropAllItems();
        }
    }
}
