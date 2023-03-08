package ru.bulk.fancy.util;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.logging.Logger;

@UtilityClass
public class NettyUtil {

    public static int MAX_PACKET_LENGTH =
            Integer.parseInt(System.getProperty("inmine.max.packet", String.valueOf(Integer.MAX_VALUE)));
    public static int LOOP_GROUP_THREADS = Integer.parseInt(System.getProperty("inmine.threads.io", "2"));

    public boolean EPOLL = Epoll.isAvailable();

    public Class<? extends SocketChannel> CLIENT_CHANNEL = EPOLL
            ? EpollSocketChannel.class
            : NioSocketChannel.class;

    public Class<? extends ServerSocketChannel> SERVER_CHANNEL = EPOLL
            ? EpollServerSocketChannel.class
            : NioServerSocketChannel.class;

    public BiFunction<String, Boolean, EventLoopGroup> LOOP_GROUP = (name, daemon) -> EPOLL
            ? new EpollEventLoopGroup(LOOP_GROUP_THREADS, new NamedThreadFactory(name, daemon))
            : new NioEventLoopGroup(LOOP_GROUP_THREADS, new NamedThreadFactory(name, daemon));

    public EventLoopGroup getWorkerLoopGroup() {
        return LOOP_GROUP.apply("Netty Worker Thread #", false);
    }

    public EventLoopGroup getBossLoopGroup() {
        return LOOP_GROUP.apply("Netty IO Thread #", false);
    }

    public Logger LOGGER = Logger.getLogger("Fancy");

    public void disableLeakDetector() {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.DISABLED);
    }

    static class NamedThreadFactory implements ThreadFactory {

        private final String name;
        private final boolean daemon;
        private final AtomicInteger counter;

        public NamedThreadFactory(String name, boolean daemon) {
            this.name = name;
            this.daemon = daemon;
            this.counter = new AtomicInteger(1);
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(daemon);
            thread.setName(name + counter.getAndIncrement());
            return thread;
        }

    }

}
