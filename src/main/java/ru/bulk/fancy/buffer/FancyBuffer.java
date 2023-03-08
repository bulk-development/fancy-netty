package ru.bulk.fancy.buffer;

import ru.bulk.fancy.util.UnsafeUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;


public abstract class FancyBuffer {

    public abstract byte readByte();

    public abstract void writeByte(byte val);

    public abstract short readShort();

    public abstract void writeShort(short val);

    public abstract int readInt();

    public abstract void writeInt(int val);

    public abstract long readLong();

    public abstract void writeLong(long val);

    public abstract float readFloat();

    public abstract void writeFloat(float val);

    public boolean readBoolean() {
        return readByte() == (byte) 1;
    }

    public void writeBoolean(boolean val) {
        writeByte(val ? (byte) 1 : (byte) 0);
    }

    public abstract byte[] readBytes();

    public abstract void writeBytes(byte[] bytes);

    public int readVarInt() {
        int tmp;
        if ((tmp = this.readByte()) >= 0)
            return tmp;
        int result = tmp & 0x7f;
        if ((tmp = this.readByte()) >= 0) {
            result |= tmp << 7;
        } else {
            result |= (tmp & 0x7f) << 7;
            if ((tmp = this.readByte()) >= 0) {
                result |= tmp << 14;
            } else {
                result |= (tmp & 0x7f) << 14;
                if ((tmp = this.readByte()) >= 0) {
                    result |= tmp << 21;
                } else {
                    result |= (tmp & 0x7f) << 21;
                    result |= this.readByte() << 28;
                }
            }
        }
        return result;
    }

    /**
     * Writes an unsigned varint
     */
    public void writeVarInt(int val) {
        while (true) {
            int bits = val & 0x7f;
            val >>>= 7;
            if (val == 0) {
                this.writeByte((byte) bits);
                return;
            }
            this.writeByte((byte) (bits | 0x80));
        }
    }

    /**
     * Writes a signed varint
     */
    @SuppressWarnings("NumericOverflow")
    public int readSignedVarInt() {
        int raw = readVarInt();
        // This undoes the trick in writeSignedVarInt()
        int temp = (((raw << 31) >> 31) ^ raw) >> 1;
        // This extra step lets us deal with the largest signed values by treating
        // negative results from read unsigned methods as like unsigned values.
        // Must re-flip the top bit if the original read value had it set.
        return temp ^ (raw & (1 << 31));
    }

    /**
     * Reads a signed varint
     */
    public void writeSignedVarInt(int val) {
        // Great trick from http://code.google.com/apis/protocolbuffers/docs/encoding.html#types
        writeVarInt((val << 1) ^ (val >> 31));
    }

    /**
     * Reads an unsigned varlong
     */
    public long readVarLong() {
        long value = 0;
        byte temp;
        for (int i = 0; i < 10; i++) {
            temp = this.readByte();
            value |= ((long) (temp & 0x7F)) << (i * 7);
            if ((temp & 0x80) != 0x80)
                break;
        }
        return value;
    }

    /**
     * Writes an unsigned varlong
     */
    public void writeVarLong(long l) {
        byte temp;
        do {
            temp = (byte) (l & 0x7F);
            l >>>= 7;
            if (l != 0)
                temp |= 0x80;
            this.writeByte(temp);
        } while (l != 0);
    }

    /**
     * Reads a signed varlong
     */
    @SuppressWarnings("NumericOverflow")
    public long readSignedVarLong() {
        long raw = readVarLong();
        // This undoes the trick in writeSignedVarInt()
        long temp = (((raw << 63) >> 63) ^ raw) >> 1;
        // This extra step lets us deal with the largest signed values by treating
        // negative results from read unsigned methods as like unsigned values.
        // Must re-flip the top bit if the original read value had it set.
        return temp ^ (raw & (1L << 63));
    }

    /**
     * Writes a signed varlong
     */
    public void writeSignedVarLong(long val) {
        // Great trick from http://code.google.com/apis/protocolbuffers/docs/encoding.html#types
        writeVarLong((val << 1) ^ (val >> 63));
    }

    public String readString() {
        return new String(readBytes(), StandardCharsets.UTF_8);
    }

    public void writeString(String s) {
        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
        writeBytes(bytes);
    }

    public String readStringNullable() {
        if (readBoolean())
            return readString();
        return null;
    }

    public void writeStringNullable(String s) {
        writeBoolean(s != null);
        if (s != null) {
            writeString(s);
        }
    }

    public <E extends Enum<E>> E readEnum(Class<E> clazz) {
        return clazz.getEnumConstants()[readVarInt()];
    }

    public void writeEnum(Enum<?> e) {
        writeVarInt(e.ordinal());
    }


    public <T, C extends Collection<T>> C readCollection(Function<Integer, C> collectionCreator, Supplier<T> reader) {
        int size = readVarInt();
        C collection = collectionCreator.apply(size);
        for (int i = 0; i < size; ++i)
            collection.add(reader.get());
        return collection;
    }

    public <T> void writeCollection(Collection<T> collection, Consumer<T> writer) {
        writeVarInt(collection.size());
        collection.forEach(writer);
    }

    public <T> T[] readArray(IntFunction<T[]> creator, Supplier<T> reader) {
        int size = readVarInt();
        T[] arr = creator.apply(size);
        for (int i = 0; i < arr.length; i++)
            arr[i] = reader.get();
        return arr;
    }

    public <T> void writeArray(T[] array, Consumer<T> writer) {
        writeVarInt(array.length);
        for (T element : array) writer.accept(element);
    }

    public List<String> readStringList() {
        return readCollection(ArrayList::new, this::readString);
    }

    public void writeStringList(List<String> list) {
        writeCollection(list, this::writeString);
    }

    public UUID readUUID() {
        return new UUID(readLong(), readLong());
    }

    public void writeUUID(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    public void writeUUIDNullable(UUID uuid) {
        writeBoolean(uuid != null);
        if (uuid != null) writeUUID(uuid);
    }

    public UUID readUUIDNullable() {
        if (readBoolean())
            return readUUID();
        return null;
    }

    public void writeObject(FancySerializable object) {
        object.serialize(this);
    }

    public <T extends FancySerializable> T readObject(T object) {
        object.deserialize(this);
        return object;
    }

    public <T extends FancySerializable> T readObject(Class<T> objectClass) {
        return readObject(UnsafeUtil.allocateInstance(objectClass));
    }

    public void writeObjectNullable(FancySerializable object) {
        writeBoolean(object != null);
        if (object != null) writeObject(object);
    }

    public <T extends FancySerializable> T readObjectNullable(T object, boolean nullIfNotPresent) {
        if (readBoolean())
            return readObject(object);

        return nullIfNotPresent ? null : object;
    }

    public <T extends FancySerializable> T readObjectNullable(T object) {
        return readObjectNullable(object, false);
    }

    public <T extends FancySerializable> T readObjectNullable(Class<T> objectClass) {
        return readBoolean() ? readObject(UnsafeUtil.allocateInstance(objectClass)) : null;
    }

}
