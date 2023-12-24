package io.redspace.ironsspellbooks.capabilities.magic;

import io.redspace.ironsspellbooks.api.network.ISerializable;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.ICastDataSerializable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.INBTSerializable;

public class RecastInstance implements ISerializable, INBTSerializable<CompoundTag> {
    protected String spellId;
    protected int spellLevel;
    protected int remainingRecasts;
    protected int totalRecasts;
    protected ICastDataSerializable castData;
    protected int ticksToLive;
    protected int remainingTicks;

    public RecastInstance() {
    }

    public RecastInstance(String spellId, int spellLevel, int remainingRecasts, int ticksToLive, ICastDataSerializable castData) {
        this.spellId = spellId;
        this.spellLevel = spellLevel;
        this.remainingRecasts = remainingRecasts;
        this.totalRecasts = remainingRecasts + 1;
        this.ticksToLive = ticksToLive;
        this.remainingTicks = ticksToLive;
        this.castData = castData;
    }

    public String getSpellId() {
        return spellId;
    }

    public int getSpellLevel() {
        return spellLevel;
    }

    public int getRemainingRecasts() {
        return remainingRecasts;
    }

    public int getTotalRecasts() {
        return totalRecasts;
    }

    public int getTicksToLive() {
        return ticksToLive;
    }

    public int getTicksRemaining() {
        return remainingTicks;
    }

    public ICastDataSerializable getCastData() {
        return castData;
    }

    @Override
    public void writeToBuffer(FriendlyByteBuf buffer) {
        buffer.writeUtf(spellId);
        buffer.writeInt(spellLevel);
        buffer.writeInt(remainingRecasts);
        buffer.writeInt(totalRecasts);
        buffer.writeInt(ticksToLive);
        buffer.writeInt(remainingTicks);

        if (castData != null) {
            buffer.writeBoolean(true);
            castData.writeToBuffer(buffer);
        } else {
            buffer.writeBoolean(false);
        }
    }

    @Override
    public void readFromBuffer(FriendlyByteBuf buffer) {
        spellId = buffer.readUtf();
        spellLevel = buffer.readInt();
        remainingRecasts = buffer.readInt();
        totalRecasts = buffer.readInt();
        ticksToLive = buffer.readInt();
        remainingTicks = buffer.readInt();

        var hasCastData = buffer.readBoolean();
        if (hasCastData) {
            var tmpCastData = SpellRegistry.getSpell(spellId).getEmptyCastData();
            tmpCastData.readFromBuffer(buffer);
            castData = tmpCastData;
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        tag.putString("spellId", spellId);
        tag.putInt("spellLevel", spellLevel);
        tag.putInt("remainingRecasts", remainingRecasts);
        tag.putInt("totalRecasts", totalRecasts);
        tag.putInt("ticksToLive", ticksToLive);
        tag.putInt("ticksRemaining", remainingTicks);

        if (castData != null) {
            tag.put("cd", castData.serializeNBT());
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        spellId = compoundTag.getString("spellId");
        spellLevel = compoundTag.getInt("spellLevel");
        remainingRecasts = compoundTag.getInt("remainingRecasts");
        totalRecasts = compoundTag.getInt("totalRecasts");
        ticksToLive = compoundTag.getInt("ticksToLive");
        remainingTicks = compoundTag.getInt("ticksRemaining");

        if (compoundTag.contains("cd")) {
            castData = SpellRegistry.getSpell(spellId).getEmptyCastData();
            if (castData != null) {
                castData.deserializeNBT((CompoundTag) compoundTag.get("cd"));
            }
        }
    }

    @Override
    public String toString() {
        var cd = castData == null ? "" : castData.serializeNBT().toString();
        return String.format("spellId:%s, spellLevel:%d, remainingRecasts:%d, totalRecasts:%d, ticksToLive:%d, ticksRemaining:%d, castData:%s", spellId, spellLevel, remainingRecasts, totalRecasts, ticksToLive, remainingTicks, cd);
    }
}
