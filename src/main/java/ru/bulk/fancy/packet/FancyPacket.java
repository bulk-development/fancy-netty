package ru.bulk.fancy.packet;

import lombok.Data;
import lombok.Setter;
import lombok.val;
import ru.bulk.fancy.buffer.FancyBuffer;

import java.util.UUID;

public abstract class FancyPacket implements Cloneable {

    @Setter
    protected UUID uniqueId;

    public UUID getUniqueId() {
        return uniqueId == null ? (uniqueId = UUID.randomUUID()) : uniqueId;
    }

    public abstract void write(FancyBuffer buffer);

    public abstract void read(FancyBuffer buffer);

    @Override
    public FancyPacket clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (FancyPacket) super.clone();
        } catch (CloneNotSupportedException ignored) {
            return null;
        }
    }

    public FancyPacket cloneEraseId() {
        val clone = clone();
        clone.setUniqueId(null);
        return clone;
    }

}
