package ru.bulk.fancy.codec;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.val;
import ru.bulk.fancy.FancyBase;
import ru.bulk.fancy.remote.FancyRemote;

import java.util.function.Consumer;

@Data
@EqualsAndHashCode(callSuper = true)
public class FancyChannelInitializer extends ChannelInitializer<SocketChannel> {

    public static int IP_TOS = 0b0011111;
    public static boolean TCP_NODELAY = true;
    public static ByteBufAllocator ALLOCATOR = PooledByteBufAllocator.DEFAULT;


    protected final FancyBase base;
    protected final Consumer<FancyRemote> activeHandler;
    protected final Consumer<FancyRemote> inactiveHandler;

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        val config = channel.config();
        config.setAllocator(ALLOCATOR);
        config.setOption(ChannelOption.TCP_NODELAY, TCP_NODELAY);
        config.setOption(ChannelOption.SO_KEEPALIVE, true);
        config.setOption(ChannelOption.IP_TOS, IP_TOS);

        channel.attr(FancyRemote.ATTRIBUTE_PARENT_KEY).set(base);

        val pipeline = channel.pipeline();
        pipeline.addLast("handler", new FancyHandler(activeHandler, inactiveHandler));
    }

}
