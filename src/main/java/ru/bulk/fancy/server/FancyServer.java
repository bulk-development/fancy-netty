package ru.bulk.fancy.server;

import io.netty.channel.ChannelFuture;
import ru.bulk.fancy.packet.FancyPacket;
import ru.bulk.fancy.packet.callback.Callback;
import ru.bulk.fancy.remote.FancyRemote;
import ru.bulk.fancy.FancyBase;

import java.util.List;
import java.util.Set;

public interface FancyServer extends FancyBase {

    Set<FancyRemote> getRemotes();

    ChannelFuture bind(int port);

    void broadcast(FancyPacket packet);

    <P extends FancyPacket> List<Callback<P>> broadcastCallback(FancyPacket packet);

}
