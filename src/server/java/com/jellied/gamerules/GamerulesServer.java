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
    // Field constants
    public final static Map<String, String> GAMERULE_DESCRIPTIONS = new HashMap<>();
    public final static Map<String, String> GAMERULE_CASE_INSENSITIVE_MAP = new HashMap<>();
    public final static Map<String, Integer> GAMERULE_DEFAULTS = new HashMap<>();
    public final static Map<String, String> GAMERULE_SYNTAX = new HashMap<>();
    public static String[] GAMERULE_IDS = new String[32];

    // Field variables
    public static ModContainer gamerulesModContainer;
    public static NBTTagCompound worldGamerules;
    public static byte[] gamerulesPacket;

    // Initialization
    public void onInit() {
        initializeGamerules();
        gamerulesModContainer = this.getModContainer();

        // Chat commands
        CommandCompat.registerCommand(new GameruleChatCommand());
    }

    public void initializeGamerules() {
        for(EnumGameruleData gamerule : EnumGameruleData.values()) {
            String gameruleName = gamerule.getName();

            GAMERULE_DEFAULTS.put(gameruleName, gamerule.getDefaultValue());
            GAMERULE_CASE_INSENSITIVE_MAP.put(gameruleName.toLowerCase(), gameruleName);
            GAMERULE_DESCRIPTIONS.put(gameruleName, gamerule.getDescription());
            GAMERULE_SYNTAX.put(gameruleName, gamerule.getSyntaxHelp());
            GAMERULE_IDS[gamerule.getId()] = gameruleName;
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

    // get & set
    public static Integer getGamerule(String gameruleName) {
        gameruleName = GAMERULE_CASE_INSENSITIVE_MAP.get((gameruleName.toLowerCase()));

        if (worldGamerules == null | gameruleName == null | !worldGamerules.hasKey(gameruleName)) {
            return GAMERULE_DEFAULTS.get(gameruleName);
        }

        return worldGamerules.getInteger(gameruleName);
    }

    public static void setGamerule(String gameruleName, Integer gameruleValue) {
        worldGamerules.setInteger(gameruleName, gameruleValue);
        buildGamerulesPacket();

        for (NetworkPlayer plr : ServerMod.getOnlineNetworkPlayers()) {
            // replication
            plr.sendNetworkData(gamerulesModContainer, gamerulesPacket);
        }
    }



    // networking 🤮🤮
    public void onNetworkPlayerJoined(NetworkPlayer netPlr) {
        EntityPlayerMP plr = ServerMod.toEntityPlayerMP(netPlr);
        plr.sendNetworkData(gamerulesModContainer, gamerulesPacket);
    }

    public static void buildGamerulesPacket() {
        gamerulesPacket = new byte[GAMERULE_IDS.length * 2 + 1];
        gamerulesPacket[0] = 0; // Packet id

        int gamerulesPacketIndex = 1;
        for (int i = 0; i < GAMERULE_IDS.length - 1; i++) {
            // maybe one of these days i write a serializer for this kind of thing
            // then i charge money for it
            // a monthly subscription service
            // you pay either $12 a month OR $100 annually!
            // or maybe like
            // $500 once and u never have to pay for it again
            // being a software engineer is so lucrative
            // anyways

            gamerulesPacket[gamerulesPacketIndex] = (byte) i; // Gamerule id
            gamerulesPacket[gamerulesPacketIndex + 1] = (byte) worldGamerules.getInteger(GAMERULE_IDS[i]); // Gamerule value

            gamerulesPacketIndex += 2;
        }
    }
}
