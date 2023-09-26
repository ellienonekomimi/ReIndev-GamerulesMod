package com.jellied.gamerules;

import net.minecraft.src.game.nbt.NBTTagCompound;

public interface WorldInfoAccessorClient {
    NBTTagCompound getGamerules();

    void setGamerules(NBTTagCompound newGamerules);
}
