package com.jellied.gamerules.client.mixins;

import com.jellied.gamerules.FastChat;
import net.minecraft.mitask.utils.CommandHelperGUI;
import net.minecraft.src.client.gui.GuiChat;
import net.minecraft.src.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandHelperGUI.class)
public class CommandHelperGuiMixin {
    @Shadow private GuiScreen gui;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void onRender(String message, boolean multiplayer, CallbackInfo ci) {
        GuiChat chatGui = (GuiChat) this.gui;
        if (!chatGui.chat.text.startsWith("/gamerule ")) {
            return;
        }

        FastChat.drawAutocompleteSuggestions();
        ci.cancel();
    }
}
