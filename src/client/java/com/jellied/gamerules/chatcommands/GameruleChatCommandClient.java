package com.jellied.gamerules.chatcommands;

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
        return "gamerule <gamerule> <value>";
    }

    @Override
    public void onExecute(String[] args, NetworkPlayer user) {
        if (args.length == 1) {
            user.displayChatMessage("<COMMAND FEEDBACK> Type /gamerule help for a list of gamerules.");
            return;
        }
        else if (args.length == 2) {
            if (args[1].equals("help")) {
                listGamerules(user);
            }
            else if (GamerulesClient.getGamerule(args[1]) != null) {
                String desc = GamerulesClient.GAMERULE_DESCRIPTIONS.get(args[1]);
                user.displayChatMessage("<COMMAND FEEDBACK> " + args[1] + ": " + desc);
                user.displayChatMessage("<COMMAND FEEDBACK> Current value: " + GamerulesClient.getGamerule(args[1]));
            }
            else {
                user.displayChatMessage("<COMMAND FEEDBACK> Gamerule '" + args[1] + "' does not exist!");
            }

            return;
        }

        // args[0] is "/gamerule"
        String gameruleName = args[1];
        Integer gameruleValue;

        try {
            gameruleValue = Integer.valueOf(args[2]);
        }
        catch(Exception e) {
            user.displayChatMessage("<COMMAND FEEDBACK> '" + args[2] + "' is not an integer!");
            return;
        }

        if (GamerulesClient.getGamerule(gameruleName) == null) {
            user.displayChatMessage("<COMMAND FEEDBACK> '" + gameruleName + "' is not a valid gamerule!");
            return;
        }

        GamerulesClient.setGamerule(gameruleName, gameruleValue);
        user.displayChatMessage("<COMMAND FEEDBACK> Gamerule '" + gameruleName + "' set to " + gameruleValue);
    }

    public void listGamerules(NetworkPlayer plr) {
        plr.displayChatMessage("<COMMAND FEEDBACK> Gamerules:");

        for (Map.Entry<String, String> set : GamerulesClient.GAMERULE_DESCRIPTIONS.entrySet()) {
            String name = set.getKey();
            String desc = set.getValue();

            plr.displayChatMessage(name + ": " + desc);
        }
    }
}
