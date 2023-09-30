package com.jellied.gamerules.client.mixins;

import com.fox2code.foxloader.registry.CommandCompat;
import com.jellied.gamerules.GamerulesClient;
import net.minecraft.src.client.player.EntityPlayerSP;
import net.minecraft.src.client.player.MovementInput;
import net.minecraft.src.game.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Method;

@Mixin(EntityPlayerSP.class)
public class EntityPlayerSPMixin {
    @Shadow private int sprintToggleTimer;
    @Shadow public MovementInput movementInput;

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/client/player/EntityPlayerSP;setSprinting(Z)V"))
    public void onSprintStateSet(EntityPlayerSP plr, boolean isSprinting) {
        if (plr.capabilities.allowFlying || GamerulesClient.getGamerule("allowSurvivalSprinting") != 1) {
            plr.setSprinting(isSprinting);
        }
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/game/entity/player/EntityPlayer;onLivingUpdate()V"))
    public void onUpdate(CallbackInfo ci) {
        EntityPlayer plr = (EntityPlayer) (Object) this; // evil floating point bit hack
        // i either cast 'this' to plr or i make this mixin extend the EntityPlayer class
        // and be forced to implement a shit ton of methods i'm not even gonna fuckin use

        if (plr.capabilities.allowFlying || GamerulesClient.getGamerule("allowSurvivalSprinting") != 1) {
            return;
        }

        if (this.sprintToggleTimer > 0) {
            this.sprintToggleTimer -= 1;
        }

        plr.isRiding();

        if (!plr.isSprinting() && Keyboard.isKeyDown(29)) {
            plr.setSprinting(true);
        }

        float forward = movementInput.moveForward;
        boolean movementFlag = forward >= 0.8F;

        if (plr.onGround && !plr.isSneaking() && !movementFlag && !plr.isSprinting()) {
            if (sprintToggleTimer <= 0 && !Keyboard.isKeyDown(29)) {
                sprintToggleTimer = 7;
            }
            else {
                plr.setSprinting(true);
            }
        }

        if (plr.isSprinting() && (forward < 0.8F || plr.isCollidedHorizontally)) {
            plr.setSprinting(false);
        }
    }
}
