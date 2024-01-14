package com.jellied.gamerules.chatcommands;

import com.fox2code.foxloader.network.ChatColors;
import com.fox2code.foxloader.network.NetworkPlayer;
import com.fox2code.foxloader.registry.CommandCompat;
import com.jellied.gamerules.GamerulesClient;
import net.minecraft.client.Minecraft;

import java.util.Map;

public class Gamerules extends CommandCompat {

    public Gamerules() {
        super("gamerules", false);
    }

    public void listGamerules(NetworkPlayer plr) {
        if (Minecraft.getInstance().thePlayer.worldObj.multiplayerWorld && GamerulesClient.worldGamerules == null){
            plr.displayChatMessage(ChatColors.RED + "This server does not have GamerulesMod");
            return;
        }

        plr.displayChatMessage(ChatColors.GRAY + "Gamerules:");

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
