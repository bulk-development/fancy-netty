package ru.bulk.fancy.packet;

import ru.bulk.fancy.buffer.FancyBuffer;

public abstract class EmptyPacket extends FancyPacket {

    @Override
    public void write(FancyBuffer buffer) {

    }

    @Override
    public void read(FancyBuffer buffer) {

    }

}
