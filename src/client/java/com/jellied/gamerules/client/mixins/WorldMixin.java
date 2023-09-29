package com.jellied.gamerules.client.mixins;

import com.jellied.gamerules.GamerulesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.block.BlockFire;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.level.SpawnerAnimals;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.level.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Random;

@Mixin(World.class)
public class WorldMixin {
    @Shadow public boolean multiplayerWorld;

    @Shadow public List<EntityPlayer> playerEntities;

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/WorldInfo;setWorldTime(J)V"))
    public void tick(WorldInfo worldInfo, long newWorldTime) {
        if (GamerulesClient.getGamerule("doDaylightCycle") == 1) {
            worldInfo.setWorldTime(newWorldTime);
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/SpawnerAnimals;performNightmares(Lnet/minecraft/src/game/level/World;Ljava/util/List;)Z"))
    public boolean onNightmare(World world, List<?> playerlist) {
        if (GamerulesClient.getGamerule("doNightmares") == 1) {
            return SpawnerAnimals.performNightmares(world, playerlist);
        }

        return false;
    }

    @Redirect(method = "scheduleBlockUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/block/Block;updateTick(Lnet/minecraft/src/game/level/World;IIILjava/util/Random;)V"))
    public void onScheduledBlockUpdate(Block block, World world, int x, int y, int z, Random random) {
        if (block.getBlockID() == Block.fire.blockID && GamerulesClient.getGamerule("doFireTick") == 0) {
            return;
        }

        block.updateTick(world, x, y, z, random);
    }

    @Redirect(method = "doRandomUpdateTicks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/block/Block;updateTick(Lnet/minecraft/src/game/level/World;IIILjava/util/Random;)V"))
    public void onRandomBlockUpdate(Block block, World world, int x, int y, int z, Random random) {
        if (block.getBlockID() == Block.fire.blockID && GamerulesClient.getGamerule("doFireTick") == 0) {
            return;
        }

        block.updateTick(world, x, y, z, random);
    }

    @Redirect(method = "TickUpdates",  at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/block/Block;updateTick(Lnet/minecraft/src/game/level/World;IIILjava/util/Random;)V"))
    public void onTickUpdate(Block block, World world, int x, int y, int z, Random random) {
        if (block.getBlockID() == Block.fire.blockID && GamerulesClient.getGamerule("doFireTick") == 0) {
            return;
        }

        block.updateTick(world, x, y, z, random);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/World;isAllPlayersFullyAsleep()Z"))
    public boolean checkPlayersAsleep(World world) {
        if (world.multiplayerWorld) {
            return false;
        }

        float totalPlayers = playerEntities.size();
        float playersAsleep = 0;
        for(int i = 0; i < playerEntities.size() - 1; i++) {
            if (playerEntities.get(i).isPlayerFullyAsleep()) {
                playersAsleep++;
            }
        }

        return ((playersAsleep / totalPlayers) * 100) >= GamerulesClient.getGamerule("playersSleepingPercentage");
    }
}
