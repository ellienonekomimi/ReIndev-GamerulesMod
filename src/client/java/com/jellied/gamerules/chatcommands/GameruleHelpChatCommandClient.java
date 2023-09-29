package com.jellied.gamerules.chatcommands;

import com.fox2code.foxloader.network.ChatColors;
import com.fox2code.foxloader.network.NetworkPlayer;
import com.fox2code.foxloader.registry.CommandCompat;
import com.jellied.gamerules.GamerulesClient;

import java.util.Map;

public class GameruleHelpChatCommandClient extends CommandCompat {

    public GameruleHelpChatCommandClient() {
        super("gamerules", false);
    }

    public void listGamerules(NetworkPlayer plr) {
        plr.displayChatMessage(ChatColors.GRAY + "<COMMAND FEEDBACK> Gamerules:");

        for (Map.Entry<String, String> set : GamerulesClient.GAMERULE_DESCRIPTIONS.entrySet()) {
            String name = set.getKey();

            plr.displayChatMessage(ChatColors.GOLD + name + ": " + ChatColors.AQUA + GamerulesClient.getGamerule(name));
        }
    }

    public void onExecute(String[] args, NetworkPlayer user) {
        listGamerules(user);
    }

    public String commandSyntax() {
        return ChatColors.YELLOW + "/gamerules";
    }
}
