package ru.bulk.fancy.remote;

import io.netty.util.AttributeKey;
import ru.bulk.fancy.packet.FancyPacket;
import ru.bulk.fancy.FancyBase;
import ru.bulk.fancy.packet.callback.Callback;

import java.util.UUID;

public interface FancyRemote extends FancyBase {

    AttributeKey<FancyBase> ATTRIBUTE_PARENT_KEY = AttributeKey.newInstance("base");
    AttributeKey<FancyRemote> ATTRIBUTE_REMOTE_KEY = AttributeKey.newInstance("remote");

    FancyBase getParent();

    void write(FancyPacket packet, UUID id);

    void write(FancyPacket packet);

    <P extends FancyPacket> Callback<P> writeAwait(FancyPacket packet, UUID id, long timeout, Runnable timeoutHandler);

    <P extends FancyPacket> Callback<P> writeAwait(FancyPacket packet, long timeout, Runnable timeoutHandler);

    <P extends FancyPacket> Callback<P> writeAwait(FancyPacket packet, UUID id, long timeout);

    <P extends FancyPacket> Callback<P> writeAwait(FancyPacket packet, long timeout);

    <P extends FancyPacket> Callback<P> writeAwait(FancyPacket packet, UUID id, Runnable timeoutHandler);

    <P extends FancyPacket> Callback<P> writeAwait(FancyPacket packet, Runnable timeoutHandler);

    <P extends FancyPacket> Callback<P> writeAwait(FancyPacket packet, UUID id);

    <P extends FancyPacket> Callback<P> writeAwait(FancyPacket packet);

}
