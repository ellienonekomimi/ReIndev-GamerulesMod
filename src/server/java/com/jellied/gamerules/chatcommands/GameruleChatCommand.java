package com.jellied.gamerules.chatcommands;

import com.fox2code.foxloader.network.ChatColors;
import com.fox2code.foxloader.network.NetworkPlayer;
import com.fox2code.foxloader.registry.CommandCompat;
import com.jellied.gamerules.GamerulesServer;

public class GameruleChatCommand extends CommandCompat {
    public GameruleChatCommand() {
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

        if (GamerulesServer.getGamerule(args[1]) == null) {
            // iT'S obNOxiOuS
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

        // args[0] is "/gamerule"
        String typedGameruleName = args[1];
        String gameruleName = GamerulesServer.GAMERULE_CASE_INSENSITIVE_MAP.get((typedGameruleName.toLowerCase()));
        Integer gameruleValue;

        try {
            gameruleValue = Integer.valueOf(args[2]);
        }
        catch(Exception e) {
            // fuck you kiva
            user.displayChatMessage(ChatColors.RED + "'" + args[2] + "' is not an integer!");
            return;
        }

        if (GamerulesServer.getGamerule(gameruleName) == null) {
            // fuck you kiva x2
            user.displayChatMessage(ChatColors.RED + "'" + gameruleName + "' is not a valid gamerule!");
            return;
        }

        GamerulesServer.setGamerule(gameruleName, gameruleValue);

        // fuck you kiva x3
        user.displayChatMessage(ChatColors.GREEN + "Gamerule '" + typedGameruleName + "' set to " + ChatColors.AQUA + gameruleValue);
    }
}
