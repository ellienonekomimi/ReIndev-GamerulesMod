package com.jellied.gamerules.server.mixins;


import com.fox2code.foxloader.loader.ServerMod;
import com.jellied.gamerules.GamerulesServer;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.player.EntityPlayerMP;
import net.minecraft.src.game.entity.player.InventoryPlayer;
import net.minecraft.src.game.level.WorldServer;
import net.minecraft.src.game.level.chunk.ChunkCoordinates;
import net.minecraft.src.server.ServerConfigurationManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerConfigurationManager.class)
public class ServerConfigManagerMixin {
    InventoryPlayer inventory;

    @Inject(method = "recreatePlayerEntity", at = @At("HEAD"))
    public void beforePlayerRecreated(EntityPlayerMP currentPlr, int arg2, CallbackInfoReturnable<EntityPlayerMP> cir) {
        inventory = currentPlr.inventory;
    }

    @Inject(method = "recreatePlayerEntity", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void afterPlayerRecreated(EntityPlayerMP entityPlayerMP, int arg2, CallbackInfoReturnable<EntityPlayerMP> cir, ChunkCoordinates var3, EntityPlayerMP var4, WorldServer worldServer){
        if (GamerulesServer.getGamerule("keepScore") != 0) {
            var4.score = entityPlayerMP.score;
        }
    }

    @Redirect(method = "recreatePlayerEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/WorldServer;entityJoinedWorld(Lnet/minecraft/src/game/entity/Entity;)Z"))
    public boolean onAddingToWorld(WorldServer world, Entity entity) {
        EntityPlayerMP plr = (EntityPlayerMP) entity;
        GamerulesServer.sendGamerulesPacket(ServerMod.toNetworkPlayer(plr));

        if (GamerulesServer.getGamerule("keepInventory") != 1) {
            return world.entityJoinedWorld(entity);
        }

        for (int slot = 0; slot < inventory.mainInventory.length; slot++) {
            if (inventory.mainInventory[slot] == null) {
                continue;
            }

            plr.inventory.mainInventory[slot] = inventory.mainInventory[slot];
            inventory.mainInventory[slot] = null;
        }

        for (int slot = 0; slot < inventory.armorInventory.length; slot++) {
            if (inventory.armorInventory[slot] == null) {
                continue;
            }

            plr.inventory.armorInventory[slot] = inventory.armorInventory[slot];
            inventory.armorInventory[slot] = null;
        }

        inventory = null;

        return world.entityJoinedWorld(entity);
    }
}
