package com.jellied.gamerules;

import com.fox2code.foxloader.loader.ClientMod;
import com.fox2code.foxloader.loader.ModContainer;
import com.fox2code.foxloader.loader.ModLoader;
import com.fox2code.foxloader.network.NetworkPlayer;
import com.fox2code.foxloader.registry.CommandCompat;
import com.jellied.gamerules.chatcommands.Gamerule;
import com.jellied.gamerules.chatcommands.Gamerules;
import net.minecraft.src.game.level.World;
import net.minecraft.src.game.nbt.NBTTagCompound;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class GamerulesClient extends GamerulesMod implements ClientMod {
    // Field
    public final static Map<String, String> GAMERULE_DESCRIPTIONS = new HashMap<>();
    public final static Map<String, String> GAMERULE_CASE_INSENSITIVE_MAP = new HashMap<>();
    public final static Map<String, Integer> GAMERULE_DEFAULTS = new HashMap<>();
    public final static Map<String, String> GAMERULE_SYNTAX = new HashMap<>();
    public static String[] GAMERULE_IDS = new String[32];

    public static NBTTagCompound worldGamerules;



    // Initialization
    public void onInit() {
        initializeGamerules();
        initAutocomplete();

        // Chat commands
        CommandCompat.registerCommand(new Gamerule());
        CommandCompat.registerClientCommand(new Gamerules());
    }

    public void initAutocomplete() {
        ModContainer autocompleteModContainer = ModLoader.getModContainer("jelliedautocomplete");

        if (autocompleteModContainer == null) {
            return;
        }

        Object clientMod = autocompleteModContainer.getClientMod();
        System.out.println("Detected autocomplete mod, initializing... ");

        try {
            Method addAutocompleteMethod =  clientMod.getClass().getMethod("addAutocomplete", String.class, Object.class);
            addAutocompleteMethod.invoke(clientMod, "/gamerule", new CommandGamerulesAutocomplete());

            System.out.println("Successfully initialized gamerule autocompletion.");
        }
        catch (Exception e) {
            System.out.println("Could not initialize gamerule autocompletion:");
            e.printStackTrace();
        }
    }

    public void initializeGamerules() {
        for(EnumGameruleDataClient gamerule : EnumGameruleDataClient.values()) {
            String gameruleName = gamerule.getName();

            GAMERULE_DEFAULTS.put(gameruleName, gamerule.getDefaultValue());
            GAMERULE_CASE_INSENSITIVE_MAP.put(gameruleName.toLowerCase(), gameruleName);
            GAMERULE_DESCRIPTIONS.put(gameruleName, gamerule.getDescription());
            GAMERULE_SYNTAX.put(gameruleName, gamerule.getSyntaxHelp());
            GAMERULE_IDS[gamerule.getId()] = gameruleName;
        }
    }



    // get & set
    public static Integer getGamerule(String gameruleName) {
        gameruleName = GAMERULE_CASE_INSENSITIVE_MAP.get((gameruleName.toLowerCase()));

        if (worldGamerules == null | gameruleName == null) {
            return GAMERULE_DEFAULTS.get(gameruleName);
        }

        if (!worldGamerules.hasKey(gameruleName)) {
            return GAMERULE_DEFAULTS.get(gameruleName);
        }

        return worldGamerules.getInteger(gameruleName);
    }

    public static void setGamerule(String gameruleName, Integer gameruleValue) {
        if (worldGamerules == null) {
            return;
        }

        worldGamerules.setInteger(gameruleName, gameruleValue);
    }


    // For singleplayer
    public static void onWorldChanged(World world) {
        if (world.multiplayerWorld) {
            return;
        }

        worldGamerules = ((WorldInfoAccessorClient) world.worldInfo).getGamerules();
        if (worldGamerules == null) {
            ((WorldInfoAccessorClient) world.worldInfo).setGamerules(worldGamerules = new NBTTagCompound());
        }

        // Set defaults
        for (Map.Entry<String, Integer> set : GAMERULE_DEFAULTS.entrySet()) {
            String name = set.getKey();
            Integer defaultValue = set.getValue();

            if (!worldGamerules.hasKey(name)) {
                worldGamerules.setInteger(name, defaultValue);
            }
        }
    }



    // For multiplayer
    public static void onGamerulesPacketRecieved(byte[] packet) {
        NBTTagCompound newTag = new NBTTagCompound();

        // Parse packet
        // Example packet:
        // 0 0 1 1 0 2 3
        // We ignore the first byte as it's just the packet identifier
        // Everything else can be grouped into pairs
        // 0 1   1 0   2 3
        // First integer in each pair is the gamerule's numerical id
        // Second integer is the gamerule's value

        for (int i = 1; i < packet.length - 1; i += 2) {
            String gameruleName = GAMERULE_IDS[packet[i]];
            Integer gameruleValue = (int) packet[i + 1];

            newTag.setInteger(gameruleName, gameruleValue);
        }

        worldGamerules = newTag;
    }

    public void onReceiveServerPacket(NetworkPlayer plr, byte[] packet) {
        byte packetId = packet[0];
        if (packetId == 0) {
            onGamerulesPacketRecieved(packet);
        }
    }
}
