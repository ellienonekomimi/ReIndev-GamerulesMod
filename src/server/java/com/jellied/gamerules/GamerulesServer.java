package com.jellied.gamerules;

import com.fox2code.foxloader.loader.ModContainer;
import com.fox2code.foxloader.loader.ServerMod;
import com.fox2code.foxloader.network.NetworkPlayer;
import com.fox2code.foxloader.registry.CommandCompat;

import com.jellied.gamerules.chatcommands.GameruleChatCommand;
import net.minecraft.src.game.entity.player.EntityPlayerMP;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

public class GamerulesServer extends GamerulesMod implements ServerMod {
    public static ModContainer gamerulesModContainer;

    public static NBTTagCompound worldGamerules;
    public static byte[] gamerulesPacket;

    public static String[] gameruleIds;

    public final static Map<String, String> GAMERULE_DESCRIPTIONS = new HashMap<>();
    public final static Map<String, Integer> GAMERULE_DEFAULTS = new HashMap<>();

    public void onInit() {
        initializeGamerules();
        gamerulesModContainer = this.getModContainer();

        // Chat commands
        CommandCompat.registerCommand(new GameruleChatCommand());
    }

    public void onNetworkPlayerJoined(NetworkPlayer netPlr) {
        EntityPlayerMP plr = ServerMod.toEntityPlayerMP(netPlr);
        plr.sendNetworkData(gamerulesModContainer, gamerulesPacket);
    }

    public static void onWorldInit(World world) {
        worldGamerules = ((WorldInfoAccessor) world.getWorldInfo()).getGamerules();

        // Set defaults
        for (Map.Entry<String, Integer> set : GAMERULE_DEFAULTS.entrySet()) {
            String name = set.getKey();
            Integer defaultValue = set.getValue();

            if (!worldGamerules.hasKey(name)) {
                worldGamerules.setInteger(name, defaultValue);
            }
        }

        buildGamerulesPacket();
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

    public static void buildGamerulesPacket() {
        gamerulesPacket = new byte[gameruleIds.length * 2 + 1];
        gamerulesPacket[0] = 0;

        int gamerulesPacketIndex = 1;
        for (int i = 0; i < gameruleIds.length - 1; i++) {
            gamerulesPacket[gamerulesPacketIndex] = (byte) i; // Gamerule id
            gamerulesPacket[gamerulesPacketIndex + 1] = (byte) worldGamerules.getInteger(gameruleIds[i]); // Gamerule value

            gamerulesPacketIndex += 2;
        }
    }

    public static Integer getGamerule(String gameruleName) {
        return worldGamerules.getInteger(gameruleName);
    }

    public static void setGamerule(String gameruleName, Integer gameruleValue) {
        worldGamerules.setInteger(gameruleName, gameruleValue);
        buildGamerulesPacket();

        for (NetworkPlayer plr : ServerMod.getOnlineNetworkPlayers()) {
            plr.sendNetworkData(gamerulesModContainer, gamerulesPacket);
        }
    }
}
