package com.jellied.gamerules;

import com.fox2code.foxloader.loader.ClientMod;
import com.fox2code.foxloader.network.NetworkPlayer;
import com.fox2code.foxloader.registry.CommandCompat;

import com.jellied.gamerules.chatcommands.GameruleChatCommandClient;

import net.minecraft.src.client.packets.NetworkManager;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GamerulesClient extends GamerulesMod implements ClientMod {
    public static NBTTagCompound worldGamerules;

    public static String[] gameruleIds;

    public final static Map<String, String> GAMERULE_DESCRIPTIONS = new HashMap<>();
    public final static Map<String, Integer> GAMERULE_DEFAULTS = new HashMap<>();

    public void onInit() {
        initializeGamerules();

        // Chat commands
        CommandCompat.registerCommand(new GameruleChatCommandClient());
    }

    public static void onWorldChanged(World world) {
        worldGamerules = ((WorldInfoAccessorClient) world.worldInfo).getGamerules();

        System.out.print("Gamerules: ");
        System.out.println(worldGamerules);

        if (world.multiplayerWorld) {
            return;
        }

        // Set defaults
        for (Map.Entry<String, Integer> set : GAMERULE_DEFAULTS.entrySet()) {
            String name = set.getKey();
            Integer defaultValue = set.getValue();

            if (!worldGamerules.hasKey(name)) {
                // System.out.println("Gamerule " + name + " not found in gamerules, assigning default value.");
                worldGamerules.setInteger(name, defaultValue);
            }
        }
    }

    public static void onGamerulesPacketRecieved(byte[] packet) {
        NBTTagCompound newTag = new NBTTagCompound();
        System.out.println(Arrays.toString(packet));

        // Parse packet
        for (int i = 1; i < packet.length - 1; i += 2) {
            String gameruleName = gameruleIds[packet[i]];
            Integer gameruleValue = (int) packet[i + 1];

            System.out.println(gameruleName + ": " + gameruleValue);

            newTag.setInteger(gameruleName, gameruleValue);
        }

        worldGamerules = newTag;
        System.out.println("Gamerules packet received!");
    }

    public void onReceiveServerPacket(NetworkPlayer plr, byte[] data) {
        if (data[0] == 0) {
            onGamerulesPacketRecieved(data);
        }
    }



    public void initializeGamerules() {
        GAMERULE_DEFAULTS.put("keepInventory", 0);
        GAMERULE_DESCRIPTIONS.put("keepInventory", "If true, players will not drop their inventory on death.");

        GAMERULE_DEFAULTS.put("doDaylightCycle", 1);
        GAMERULE_DESCRIPTIONS.put("doDaylightCycle", "If false, the current time will be frozen until set to true.");

        GAMERULE_DEFAULTS.put("mobGriefing", 1);
        GAMERULE_DESCRIPTIONS.put("mobGriefing", "If true, mobs (i.e. creepers) can grief the world.");

        GAMERULE_DEFAULTS.put("tntExplodes", 1);
        GAMERULE_DESCRIPTIONS.put("tntExplodes", "If true, tnt will prime when broken/activated.");

        GAMERULE_DEFAULTS.put("doNightmares", 1);
        GAMERULE_DESCRIPTIONS.put("doNightmares", "If true, mobs can interrupt sleeping.");

        GAMERULE_DEFAULTS.put("allowSurvivalSprinting", 0);
        GAMERULE_DESCRIPTIONS.put("allowSurvivalSprinting", "If true, players can sprint in survival.");

        GAMERULE_DEFAULTS.put("doFireTick", 1);
        GAMERULE_DESCRIPTIONS.put("doFireTick", "If true, fire will be able to spread to nearby flammable blocks.");

        gameruleIds = new String[] {
                "keepInventory",
                "doDaylightCycle",
                "mobGriefing",
                "tntExplodes",
                "doNightmares",
                "allowSurvivalSprinting",
                "doFireTick"
        };
    }

    public static Integer getGamerule(String gameruleName) {
        if (worldGamerules == null) {
            return -1;
        }

        return worldGamerules.getInteger(gameruleName);
    }

    public static void setGamerule(String gameruleName, Integer gameruleValue) {
        if (worldGamerules == null) {
            return;
        }

        worldGamerules.setInteger(gameruleName, gameruleValue);
    }
}
