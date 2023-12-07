package com.jellied.gamerules.server.mixins;

import com.jellied.gamerules.GamerulesServer;
import com.jellied.gamerules.WorldInfoAccessor;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.player.EntityPlayer;
import net.minecraft.src.game.level.SpawnerAnimals;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.level.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Random;

@Mixin(World.class)
public abstract class WorldMixin {
    long frozenWorldTime;

    @Shadow protected WorldInfo worldInfo;

    @Shadow public abstract boolean addLightningBolt(Entity entity);

    @Inject(method = "saveLevel", at = @At("HEAD"))
    public void beforeSave(CallbackInfo ci) {
        ((WorldInfoAccessor) this.worldInfo).setGamerules(GamerulesServer.worldGamerules);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/WorldInfo;setWorldTime(J)V"))
    public void onTick(WorldInfo worldInfo, long newWorldTime) {
        if (GamerulesServer.getGamerule("doDaylightCycle") == 1) {
            worldInfo.setWorldTime(newWorldTime);
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/SpawnerAnimals;performNightmares(Lnet/minecraft/src/game/level/World;Ljava/util/List;)Z"))
    public boolean onNightmare(World world, List<?> playerlist) {
        if (GamerulesServer.getGamerule("doNightmares") == 1) {
            return SpawnerAnimals.performNightmares(world, playerlist);
        }

        return false;
    }

    @Redirect(method = "scheduleBlockUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/block/Block;updateTick(Lnet/minecraft/src/game/level/World;IIILjava/util/Random;)V"))
    public void onScheduledBlockUpdate(Block block, World world, int x, int y, int z, Random random) {
        if (block.getBlockID() == Block.fire.blockID && GamerulesServer.getGamerule("doFireTick") == 0) {
            return;
        }

        block.updateTick(world, x, y, z, random);
    }

    @Redirect(method = "doRandomUpdateTicks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/block/Block;updateTick(Lnet/minecraft/src/game/level/World;IIILjava/util/Random;)V"))
    public void onRandomBlockUpdate(Block block, World world, int x, int y, int z, Random random) {
        if (block.getBlockID() == Block.fire.blockID && GamerulesServer.getGamerule("doFireTick") == 0) {
            return;
        }

        block.updateTick(world, x, y, z, random);
    }

    @Redirect(method = "TickUpdates",  at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/block/Block;updateTick(Lnet/minecraft/src/game/level/World;IIILjava/util/Random;)V"))
    public void onTickUpdate(Block block, World world, int x, int y, int z, Random random) {
        if (block.getBlockID() == Block.fire.blockID && GamerulesServer.getGamerule("doFireTick") == 0) {
            return;
        }

        block.updateTick(world, x, y, z, random);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/World;isAllPlayersFullyAsleep()Z"))
    public boolean checkPlayersAsleep(World world) {
        float playersAsleep = 0;
        for (EntityPlayer p : world.playerEntities)
            playersAsleep += p.isPlayerFullyAsleep() ? 1 : 0;

        float totalPlayers = world.playerEntities.size();

        return ((playersAsleep / totalPlayers) * 100) >= GamerulesServer.getGamerule("playersSleepingPercentage");
    }

    @Redirect(method = "doRandomUpdateTicks", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/World;addLightningBolt(Lnet/minecraft/src/game/entity/Entity;)Z"))
    public boolean onLightningAdd(World instance, Entity entity) {
        System.out.println("Striking lightning!");

        if (GamerulesServer.getGamerule("doLightning") != 1) {
            entity.setEntityDead();

            return false;
        }

        return addLightningBolt(entity);
    }
}
