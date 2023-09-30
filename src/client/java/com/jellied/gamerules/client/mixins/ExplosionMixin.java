package com.jellied.gamerules.client.mixins;

import com.jellied.gamerules.GamerulesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.entity.EntityMob;
import net.minecraft.src.game.level.Explosion;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.level.chunk.ChunkPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(Explosion.class)
public class ExplosionMixin {
    Minecraft minecraft = Minecraft.getInstance();

    @Shadow public Set<ChunkPosition> destroyedBlockPositions;

    @Shadow public Entity exploder;

    @Shadow private World worldObj;

    @Redirect(method = "doExplosionA", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/block/Block;getExplosionResistance(Lnet/minecraft/src/game/entity/Entity;)F"))
    public float onExplosionA(Block block, Entity entity) {
        if (this.exploder == null || !(this.exploder instanceof EntityMob) || GamerulesClient.getGamerule("mobGriefing") == 1) {
            return block.getExplosionResistance(this.exploder);
        }

        //If you're dealing with a super creeper with a larger blast strength than this, you're probably fucked anyway.
        return 3600000;
    }
}
