package ru.bulk.fancy.handler.context;

import lombok.Data;
import ru.bulk.fancy.packet.FancyPacket;
import ru.bulk.fancy.packet.callback.Callback;
import ru.bulk.fancy.remote.FancyRemote;

@Data
public class FancyRemotePacketHandlerContext extends FancyRemoteHandlerContext {

    private final FancyRemote remote;
    private final Callback<?> callback;
    private final FancyPacket packet;

}
