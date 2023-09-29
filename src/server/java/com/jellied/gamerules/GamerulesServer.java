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

    public final static Map<String, String> GAMERULE_DESCRIPTIONS = new HashMap<>();
    public final static Map<String, Integer> GAMERULE_DEFAULTS = new HashMap<>();
    public final static Map<String, String> GAMERULE_SYNTAX = new HashMap<>();
    public static String[] GAMERULE_IDS = new String[32];

    // Initialization
    public void onInit() {
        initializeGamerules();
        gamerulesModContainer = this.getModContainer();

        // Chat commands
        CommandCompat.registerCommand(new GameruleChatCommand());
    }

    public void initializeGamerules() {
        int i = 0;
        for(EnumGameruleData gamerule : EnumGameruleData.values()) {
            GAMERULE_DEFAULTS.put(gamerule.getId(), gamerule.getDefaultValue());
            GAMERULE_DESCRIPTIONS.put(gamerule.getId(), gamerule.getDescription());
            GAMERULE_SYNTAX.put(gamerule.getId(), gamerule.getDescription());
            GAMERULE_IDS[i] = gamerule.getId();

            i++;
        }
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


    // Server networking
    public void onNetworkPlayerJoined(NetworkPlayer netPlr) {
        EntityPlayerMP plr = ServerMod.toEntityPlayerMP(netPlr);
        plr.sendNetworkData(gamerulesModContainer, gamerulesPacket);
    }

    public static void buildGamerulesPacket() {
        gamerulesPacket = new byte[GAMERULE_IDS.length * 2 + 1];
        gamerulesPacket[0] = 0; // Packet id

        int gamerulesPacketIndex = 1;
        for (int i = 0; i < GAMERULE_IDS.length - 1; i++) {
            gamerulesPacket[gamerulesPacketIndex] = (byte) i; // Gamerule id
            gamerulesPacket[gamerulesPacketIndex + 1] = (byte) worldGamerules.getInteger(GAMERULE_IDS[i]); // Gamerule value

            gamerulesPacketIndex += 2;
        }
    }

    public static Integer getGamerule(String gameruleName) {
        if (!worldGamerules.hasKey(gameruleName)) {
            return null;
        }

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
