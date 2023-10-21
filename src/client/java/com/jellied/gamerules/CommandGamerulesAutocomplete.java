package com.jellied.gamerules;

import net.minecraft.src.client.gui.GuiChat;

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

    public List<String> getGamerulesThatBeginWith(String with) {
        List<String> gamerulesThatBegin = new ArrayList<>();

        for (String entry : gamerules) {
            if (entry != null && entry.startsWith(with)) {
                gamerulesThatBegin.add(entry);
            }
        }

        return gamerulesThatBegin;
    }

    public List<String> getCommandSuggestions(GuiChat gui, int commandArgIndex) {
        if (commandArgIndex != 1) {
            return blankList;
        }

        String typedGamerule = gui.chat.text.replaceFirst("/gamerule ", "");
       return getGamerulesThatBeginWith(typedGamerule);
    }
}
