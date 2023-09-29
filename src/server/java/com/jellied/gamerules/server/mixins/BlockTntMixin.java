package com.jellied.gamerules.server.mixins;

import com.jellied.gamerules.GamerulesServer;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.block.BlockTNT;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.other.EntityItem;
import net.minecraft.src.game.item.ItemStack;
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
        boolean returnVal = world.entityJoinedWorld(entity);

        if (GamerulesServer.getGamerule("tntExplodes") == 1) {
            EntityItem itemDropped = new EntityItem(world, entity.posX, entity.posY, entity.posZ, new ItemStack(Block.tnt));
            world.entityJoinedWorld(itemDropped);

            entity.setEntityDead();
        }

        return returnVal;
    }
}
