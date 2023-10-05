package com.jellied.gamerules.client.mixins;

import com.jellied.gamerules.GuiTextFieldAccessorClient;
import net.minecraft.src.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiTextField.class)
public abstract class GuiTextFieldMixin implements GuiTextFieldAccessorClient {
    @Shadow private int tHeight;

    @Shadow @Final private int yPos;

    @Override
    public int getTHeight() {
        return tHeight;
    }

    @Override
    public int getPosY() {
        return yPos;
    }
}
