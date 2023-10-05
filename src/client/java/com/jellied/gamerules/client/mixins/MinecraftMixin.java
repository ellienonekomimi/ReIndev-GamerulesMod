package com.jellied.gamerules.client.mixins;

import com.jellied.gamerules.FastChat;
import com.jellied.gamerules.GamerulesClient;
import com.jellied.gamerules.WorldInfoAccessorClient;
import net.minecraft.client.Minecraft;
import net.minecraft.src.client.player.EntityPlayerSP;
import net.minecraft.src.client.player.PlayerController;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.entity.player.InventoryPlayer;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.level.WorldProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow public EntityPlayerSP thePlayer;

    @Shadow public World theWorld;


    @Redirect(method = "respawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/client/player/PlayerController;createPlayer(Lnet/minecraft/src/game/level/World;)Lnet/minecraft/src/game/entity/player/EntityPlayer;"))
    public EntityPlayer createPlayer(PlayerController instance, World world) {
        EntityPlayer newPlr = instance.createPlayer(world);

        if (GamerulesClient.getGamerule("keepInventory") != 1) {
            return newPlr;
        }

        InventoryPlayer currentInventory = thePlayer.inventory;
        for (int slot = 0; slot < currentInventory.mainInventory.length; slot++) {
            if (currentInventory.mainInventory[slot] == null) {
                continue;
            }

            newPlr.inventory.mainInventory[slot] = currentInventory.mainInventory[slot];
            currentInventory.mainInventory[slot] = null;
        }

        for (int slot = 0; slot < currentInventory.armorInventory.length; slot++) {
            if (currentInventory.armorInventory[slot] == null) {
                continue;
            }

            newPlr.inventory.armorInventory[slot] = currentInventory.armorInventory[slot];
            currentInventory.armorInventory[slot] = null;
        }

        return newPlr;
    }



    @Inject(method = "changeWorld", at = @At("TAIL"))
    public void onWorldChanged(CallbackInfo ci) {
        World world = this.theWorld;

        // System.out.println("World changed to: " + world);

        if (world == null) {
            return;
        }

        GamerulesClient.onWorldChanged(world);
    }
}
