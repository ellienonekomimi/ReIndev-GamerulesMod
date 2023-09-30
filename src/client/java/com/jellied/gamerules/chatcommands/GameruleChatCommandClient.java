package com.jellied.gamerules.chatcommands;

import com.fox2code.foxloader.network.ChatColors;
import com.fox2code.foxloader.network.NetworkPlayer;
import com.fox2code.foxloader.registry.CommandCompat;
import com.jellied.gamerules.GamerulesClient;
import net.minecraft.client.Minecraft;

import java.util.Map;

public class GameruleChatCommandClient extends CommandCompat {
    public GameruleChatCommandClient() {
        super("gamerule", true);
    }

    public String commandSyntax() {
        // Can't paste that weird ass symbol in intellij for some reason
        return ChatColors.YELLOW + "/gamerule <gamerule name> <gamerule value>";
    }

    @Override
    public void onExecute(String[] args, NetworkPlayer user) {
        if (args.length == 1) {
            user.displayChatMessage(commandSyntax());
            return;
        }
        else if (args.length == 2) {
            if (GamerulesClient.getGamerule(args[1]) == null) {
                // iT'S obNOxiOuS
                // user.displayChatMessage(ChatColors.RED + "<COMMAND FEEDBACK> Gamerule '" + args[1] + "' does not exist!");
                user.displayChatMessage(ChatColors.RED + "Gamerule '" + args[1] + "' does not exist!");
                return;
            }

            String gameruleName = GamerulesClient.GAMERULE_CASE_INSENSITIVE_MAP.get((args[1].toLowerCase()));

            String desc = GamerulesClient.GAMERULE_DESCRIPTIONS.get(gameruleName);
            String syntax = GamerulesClient.GAMERULE_SYNTAX.get(gameruleName);
            user.displayChatMessage(ChatColors.GOLD + gameruleName + ": " + ChatColors.GRAY + desc);
            user.displayChatMessage(ChatColors.GREEN + "Syntax: " + ChatColors.YELLOW + syntax);
            user.displayChatMessage("Current value: " + ChatColors.AQUA + GamerulesClient.getGamerule(gameruleName));

            return;
        }

        // args[0] is "/gamerule"
        String typedGameruleName = args[1];
        String gameruleName = GamerulesClient.GAMERULE_CASE_INSENSITIVE_MAP.get((typedGameruleName.toLowerCase()));
        Integer gameruleValue;

        try {
            gameruleValue = Integer.valueOf(args[2]);
        }
        catch(Exception e) {
            // fuck you kiva
            // user.displayChatMessage(ChatColors.RED + "<COMMAND FEEDBACK> '" + args[2] + "' is not an integer!");
            user.displayChatMessage(ChatColors.RED + args[2] + "' is not an integer!");
            return;
        }

        if (GamerulesClient.getGamerule(gameruleName) == null) {
            // fuck you kiva x2
            // user.displayChatMessage(ChatColors.RED + "<COMMAND FEEDBACK> '" + gameruleName + "' is not a valid gamerule!");
            user.displayChatMessage(ChatColors.RED + gameruleName + "' is not a valid gamerule!");
            return;
        }

        GamerulesClient.setGamerule(gameruleName, gameruleValue);

        // fuck you kiva x3
        //user.displayChatMessage(ChatColors.GREEN + "<COMMAND FEEDBACK> Gamerule '" + gameruleName + "' set to " + ChatColors.AQUA + gameruleValue);
        user.displayChatMessage(ChatColors.GREEN + "Gamerule '" + typedGameruleName + "' set to " + ChatColors.AQUA + gameruleValue);
    }
}
