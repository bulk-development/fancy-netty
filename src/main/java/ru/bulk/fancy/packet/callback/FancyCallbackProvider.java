package ru.bulk.fancy.packet.callback;

import lombok.Getter;
import lombok.val;
import ru.bulk.fancy.util.FancyException;
import ru.bulk.fancy.packet.FancyPacket;
import ru.bulk.fancy.remote.FancyRemote;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@SuppressWarnings("unchecked")
public class FancyCallbackProvider {

    protected Map<UUID, Callback<?>> callbackMap = new ConcurrentHashMap<>();

    public void tick() {
        val timestamp = System.currentTimeMillis();
        callbackMap.values().forEach(callback -> {
            if (timestamp - callback.getTimestamp() > callback.timeout) {
                callbackMap.remove(callback.id);
                closeCallback(callback);
                if (callback.timeoutHandler != null)
                    callback.timeoutHandler.run();
            }
        });
    }

    public <P extends FancyPacket> Callback<P> get(UUID id) {
        return (Callback<P>) callbackMap.get(id);
    }

    public <P extends FancyPacket> Callback<P> remove(UUID id) {
        Callback<P> callback = (Callback<P>) callbackMap.remove(id);
        closeCallback(callback);
        return callback;
    }

    public <P extends FancyPacket> Callback<P> create(FancyRemote remote, UUID id, long timeout, Runnable timeoutHandler) {
        Callback<P> callback = new Callback<>(id, remote, timeout, timeoutHandler);
        callbackMap.put(id, callback);
        return callback;
    }

    public boolean completeCallback(FancyPacket packet) {
        Callback<FancyPacket> callback = get(packet.getUniqueId());
        if (callback != null) {
            try {
                callbackMap.remove(packet.getUniqueId());
                return callback.complete(packet);
            } catch (Throwable e) {
                throw new FancyException("Exception while complete callback, id " + callback.getId(), e);
            }
        }
        return false;
    }

    protected void closeCallback(Callback<?> callback) {
        if (callback == null) return;

        CompletableFuture<?> handler = callback.getFuture();
        if (handler != null && !handler.isDone())
            handler.completeExceptionally(new FancyException("Time outed callback, id " + callback.getId()));
    }

}
