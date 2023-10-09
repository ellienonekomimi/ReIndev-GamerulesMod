package com.jellied.gamerules.chatcommands;

import com.fox2code.foxloader.network.ChatColors;
import com.fox2code.foxloader.network.NetworkPlayer;
import com.fox2code.foxloader.registry.CommandCompat;
import com.jellied.gamerules.GamerulesClient;

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

        if (GamerulesClient.getGamerule(args[1]) == null) {
            user.displayChatMessage(ChatColors.RED + "Gamerule '" + args[1] + "' does not exist!");
            return;
        }

        if (args.length == 2) {
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
            user.displayChatMessage(ChatColors.RED + "'" + args[2] + "' is not an integer!");
            return;
        }

        if (GamerulesClient.getGamerule(gameruleName) == null) {
            user.displayChatMessage(ChatColors.RED + "'" + gameruleName + "' is not a valid gamerule!");
            return;
        }

        GamerulesClient.setGamerule(gameruleName, gameruleValue);

        user.displayChatMessage(ChatColors.GREEN + "Gamerule '" + typedGameruleName + "' set to " + ChatColors.AQUA + gameruleValue);
    }
}
