package com.jellied.gamerules.server.mixins;

import com.jellied.gamerules.GamerulesServer;
import net.minecraft.src.game.block.Block;
import net.minecraft.src.game.entity.Entity;
import net.minecraft.src.game.level.Explosion;
import net.minecraft.src.game.level.chunk.ChunkPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Set;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Shadow public Set<ChunkPosition> destroyedBlockPositions;

    @Shadow public Entity exploder;

    @Redirect(method = "doExplosion", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/block/Block;getExplosionResistance(Lnet/minecraft/src/game/entity/Entity;)F"))
    public float onExplosionA(Block block, Entity entity) {
        if (this.exploder == null || GamerulesServer.getGamerule("mobGriefing") == 1) {
            return block.getExplosionResistance(this.exploder);
        }

        //If you're dealing with a super creeper with a larger blast strength than this, you're probably fucked anyway.
        return 999999;
    }
}
