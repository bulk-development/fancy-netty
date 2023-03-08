package ru.bulk.fancy.packet.registry;

import ru.bulk.fancy.packet.FancyPacket;
import ru.bulk.fancy.remote.FancyRemote;

@FunctionalInterface
public interface FancyPacketHandler<P extends FancyPacket> {

    void handle(FancyRemote remote, P packet);

}
