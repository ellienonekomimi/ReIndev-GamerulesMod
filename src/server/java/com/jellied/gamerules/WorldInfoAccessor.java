package com.jellied.gamerules;

import net.minecraft.src.game.nbt.NBTTagCompound;

public interface WorldInfoAccessor{
    NBTTagCompound getGamerules();

    void setGamerules(NBTTagCompound newGamerules);
}
