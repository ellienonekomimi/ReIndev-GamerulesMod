package com.jellied.gamerules;

import com.fox2code.foxloader.client.KeyBindingAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.src.client.KeyBinding;
import net.minecraft.src.client.gui.FontRenderer;
import net.minecraft.src.client.gui.Gui;
import net.minecraft.src.client.gui.GuiChat;
import net.minecraft.src.client.gui.ScaledResolution;
import org.graalvm.compiler.core.phases.EconomyMidTier;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class FastChat {
    private static final Map<Integer, Boolean> keyStates = new HashMap<>();
    private static final Minecraft minecraft = Minecraft.getInstance();
    private static final ScaledResolution res = ScaledResolution.instance;
    private static int currentTabCycle = 0;
    private static String currentTabStringListeningTo = null;

    static final int BLACK = new Color(0, 0, 0, (int) (255 * 0.75)).getRGB();

    public static void init() {
        keyStates.put(Keyboard.KEY_TAB, true);
        keyStates.put(Keyboard.KEY_UP, true);
        keyStates.put(Keyboard.KEY_DOWN, true);
    }

    public static void drawAutocompleteSuggestions() {
        if (!(minecraft.currentScreen instanceof GuiChat)) {
            currentTabCycle = 0;
            currentTabStringListeningTo = null;

            return;
        }

        GuiChat textGui = (GuiChat) minecraft.currentScreen;
        if (!textGui.chat.text.startsWith("/gamerule ")) {
            currentTabCycle = 0;
            currentTabStringListeningTo = null;

            return;
        }

        String typedCommand = textGui.chat.text.replaceAll("/gamerule ", "");
        if (currentTabStringListeningTo != null && !currentTabStringListeningTo.equals(typedCommand)) {
            currentTabCycle = 0;
        }

        String[] suggestions = GamerulesClient.getGamerulesThatBeginWith(typedCommand);

        int drawX = minecraft.fontRenderer.getStringWidth("> /gamerule ");
        int drawY = res.getScaledHeight() - 22 - 4 - 1; // One pixel above the chat bar
        int largestCommandWidth = 0;
        int totalCommands = 0;

        for (int i = suggestions.length - 1; i >= 0; i--) {
            String suggestion = suggestions[i];
            if (suggestion == null) {
                continue;
            }

            totalCommands++;
            largestCommandWidth = Math.max(largestCommandWidth, minecraft.fontRenderer.getStringWidth(suggestion));
        }

        Gui.drawRect(drawX, drawY + 12, drawX + largestCommandWidth + 6, drawY - (12 * (totalCommands - 1)) - 4, BLACK);

        for (int i = suggestions.length - 1; i >= 0; i--) {
            String suggestion = suggestions[i];
            if (suggestion == null) {
                continue;
            }

            boolean isCurrentCycle = i == currentTabCycle;
            int textColor = isCurrentCycle ? Color.YELLOW.getRGB() : Color.WHITE.getRGB();
            minecraft.fontRenderer.drawStringWithShadow(suggestion, drawX, drawY, textColor);

            drawY -= 12;
        }

        currentTabStringListeningTo = typedCommand;
    }

    public static void handleKeybinds() {
        // hell

        if (Keyboard.isKeyDown(Keyboard.KEY_TAB) && keyStates.get(Keyboard.KEY_TAB)) {
            keyStates.put(Keyboard.KEY_TAB, false);
            handleTabKeybind();
        }
        else if (!Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
            keyStates.put(Keyboard.KEY_TAB, true);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_UP) && keyStates.get(Keyboard.KEY_UP)) {
            keyStates.put(Keyboard.KEY_UP, false);
            handleArrowKeybind(-1);
        }
        else if (!Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            keyStates.put(Keyboard.KEY_UP, true);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN) && keyStates.get(Keyboard.KEY_DOWN)) {
            keyStates.put(Keyboard.KEY_DOWN, false);
            handleArrowKeybind(1);
        }
        else if (!Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            keyStates.put(Keyboard.KEY_DOWN, true);
        }
    }

    public static void handleTabKeybind() {
        if (!(minecraft.currentScreen instanceof GuiChat)) {
            return;
        }

        GuiChat textGui = (GuiChat) minecraft.currentScreen;
        if (!textGui.chat.text.startsWith("/gamerule ")) {
            return;
        }

        String typedCommand = textGui.chat.text.replaceAll("/gamerule ", "");
        String[] suggestions = GamerulesClient.getGamerulesThatBeginWith(typedCommand);

        textGui.chat.text = "/gamerule " + suggestions[currentTabCycle] + " ";
        textGui.chat.setCursorPosition(textGui.chat.text.length());
    }

    public static void handleArrowKeybind(int direction) {
        if (!(minecraft.currentScreen instanceof GuiChat)) {
            return;
        }

        GuiChat textGui = (GuiChat) minecraft.currentScreen;
        if (!textGui.chat.text.startsWith("/gamerule ")) {
            return;
        }

        String typedCommand = textGui.chat.text.replaceAll("/gamerule ", "");
        String[] suggestions = GamerulesClient.getGamerulesThatBeginWith(typedCommand);

        int nextCycle = currentTabCycle + direction;
        if (nextCycle > suggestions.length - 1 | nextCycle >= 0 && suggestions[nextCycle] == null) {
            currentTabCycle = 0;
        }
        else if (nextCycle <= -1) {
            // when i first wrote this i accidentally wrote i++
            for (int i = suggestions.length - 1; i >= 0; i--) {
                if (suggestions[i] != null) {
                    currentTabCycle = i;
                    break;
                }
            }
        }
        else {
            currentTabCycle = nextCycle;
        }
    }
}
