package com.jellied.gamerules;

import static com.jellied.gamerules.GamerulesMod.acceptsA01ValueStr;

public enum EnumGameruleData {
    KEEP_INVENTORY("keepInventory", 0, 0, "If true, players will not drop their inventory upon death.", acceptsA01ValueStr),
    DO_DAYLIGHT_CYCLE("doDaylightCycle", 1, 1, "If false, the current time will be frozen.", acceptsA01ValueStr),
    MOB_GRIEFING("mobGriefing", 2,1, "If false, mobs such as creepers will not destroy blocks.", acceptsA01ValueStr),
    TNT_EXPLODES("tntExplodes", 3, 1, "If false, tnt will not explode when primed.", acceptsA01ValueStr),
    DO_NIGHTMARES("doNightmares", 4, 1, "If true, players sleeping in a dark environment can spawn a mob on them.", acceptsA01ValueStr),
    ALLOW_SURVIVAL_SPRINTING("allowSurvivalSprinting", 5, 0, "If true, players can sprint in survival.", acceptsA01ValueStr),
    DO_FIRE_TICK("doFireTick", 6, 1, "If false, fire will not spread.", acceptsA01ValueStr),
    PLAYERS_SLEEPING_PERCENTAGE("playersSleepingPercentage", 7, 100, "Dictates the percentage of players that must be asleep in order to skip to morning.", "Accepts values between 0-100 (percentage of players that must be sleeping to skip to night)"),
    DO_LIGHTNING("doLightning", 8, 1, "If false, lightning will never strike during thunderstorms.", acceptsA01ValueStr),
    KEEP_SCORE("keepScore", 9, 0, "If true, players don't lose score upon death", acceptsA01ValueStr);

    private final String name;
    private final int id;
    private final int defaultValue;
    private final String description;
    private final String syntaxHelp;
    EnumGameruleData(String name, int numId, int defaultValue, String description, String syntaxHelp) {
        this.name = name;
        this.id = numId;
        this.defaultValue = defaultValue;
        this.description = description;
        this.syntaxHelp = syntaxHelp;
    }

    public String getName() {
        return this.name;
    }

    // Because apparently hashmap order can vary or something, causes a mismatch between client and server maps.
    public int getId() {
        return this.id;
    }

    public int getDefaultValue() {
        return this.defaultValue;
    }

    public String getDescription() {
        return this.description;
    }

    public String getSyntaxHelp() {
        return syntaxHelp;
    }
}
