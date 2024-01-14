package com.jellied.gamerules.chatcommands;

import com.fox2code.foxloader.network.ChatColors;
import com.fox2code.foxloader.network.NetworkPlayer;
import com.fox2code.foxloader.registry.CommandCompat;
import com.jellied.gamerules.GamerulesServer;

public class GameruleServer extends CommandCompat {
    public GameruleServer() {
        super("gamerule", true);
    }

    public String commandSyntax() {
        return ChatColors.YELLOW + "/gamerule <gamerule name> <gamerule value>";
    }

    @Override
    public void onExecute(String[] args, NetworkPlayer user) {
        if (args.length == 1) {
            user.displayChatMessage(commandSyntax());
            return;
        }

        if (GamerulesServer.getGamerule(args[1]) == null) {
            user.displayChatMessage(ChatColors.RED + "Gamerule '" + args[1] + "' does not exist!");
            return;
        }

        if (args.length == 2) {
            String gameruleName = GamerulesServer.GAMERULE_CASE_INSENSITIVE_MAP.get((args[1].toLowerCase()));

            String desc = GamerulesServer.GAMERULE_DESCRIPTIONS.get(gameruleName);
            String syntax = GamerulesServer.GAMERULE_SYNTAX.get(gameruleName);
            user.displayChatMessage(ChatColors.GOLD + gameruleName + ": " + ChatColors.GRAY + desc);
            user.displayChatMessage(ChatColors.GREEN + "Syntax: " + ChatColors.YELLOW + syntax);
            user.displayChatMessage("Current value: " + ChatColors.AQUA + GamerulesServer.getGamerule(gameruleName));

            return;
        }

        String typedGameruleName = args[1];
        String gameruleName = GamerulesServer.GAMERULE_CASE_INSENSITIVE_MAP.get((typedGameruleName.toLowerCase()));
        int gameruleValue;

        try {
            gameruleValue = Integer.parseInt(args[2]);
        }
        catch(Exception e) {
            user.displayChatMessage(ChatColors.RED + "'" + args[2] + "' is not an integer!");
            return;
        }

        if (GamerulesServer.getGamerule(gameruleName) == null) {
            user.displayChatMessage(ChatColors.RED + "'" + gameruleName + "' is not a valid gamerule!");
            return;
        }

        GamerulesServer.setGamerule(gameruleName, gameruleValue);

        user.displayChatMessage(ChatColors.GREEN + "Gamerule '" + typedGameruleName + "' set to " + ChatColors.AQUA + gameruleValue);
    }
}
