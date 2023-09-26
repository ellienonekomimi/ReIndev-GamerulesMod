package com.jellied.gamerules.client.mixins;

import com.jellied.gamerules.GamerulesClient;
import net.minecraft.src.game.block.BlockTNT;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.level.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockTNT.class)
public class BlockTntMixin {
    @Redirect(method = "onBlockDestroyedByPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/World;entityJoinedWorld(Lnet/minecraft/src/game/entity/Entity;)Z"))
    public boolean onDestroyedByPlayer(World world, Entity entity) {
        return onTntPrimed(world, entity);
    }

    @Redirect(method = "onBlockDestroyedByExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/level/World;entityJoinedWorld(Lnet/minecraft/src/game/entity/Entity;)Z"))
    public boolean OnDestroyedByExplosion(World world, Entity entity) {
        return onTntPrimed(world, entity);
    }

    public boolean onTntPrimed(World world, Entity entity) {
        if (GamerulesClient.getGamerule("tntExplodes") == 1) {
            return world.entityJoinedWorld(entity);
        }

        entity.setEntityDead();

        return false;
    }
}
