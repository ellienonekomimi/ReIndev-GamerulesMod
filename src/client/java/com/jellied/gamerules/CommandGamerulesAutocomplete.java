package com.jellied.gamerules;

import com.fox2code.foxloader.loader.ModLoader;
import net.minecraft.src.client.gui.GuiChat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CommandGamerulesAutocomplete {
    List<String> gamerules = new ArrayList<>();
    List<String> blankList = new ArrayList<>();

    public CommandGamerulesAutocomplete() {
        for (String gameruleName : GamerulesClient.GAMERULE_IDS) {
            if (gameruleName != null) {
                gamerules.add(gameruleName);
            }
        }
    }

    // lol
    public int getCursorArgIndex(String command, int cursorPos) {
        int argIndex = 0;

        for (int i = 0; i <= command.length() - 1; i++) {
            if (i == cursorPos) {
                break;
            }

            if (command.charAt(i) == ' ') {
                argIndex++;
            }
        }

        return argIndex;
    }

    public List<String> getCommandSuggestions(GuiChat gui) {
        int commandArgIndex = getCursorArgIndex(gui.chat.text, gui.chat.cursorPosition);
        if (commandArgIndex != 1) {
            return blankList;
        }

       return gamerules;
    }
}
