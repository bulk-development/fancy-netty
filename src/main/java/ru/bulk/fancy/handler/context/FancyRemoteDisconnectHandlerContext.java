package ru.bulk.fancy.handler.context;

import lombok.Data;
import ru.bulk.fancy.remote.FancyRemote;

@Data
public class FancyRemoteDisconnectHandlerContext extends FancyRemoteHandlerContext {

    private final FancyRemote remote;

    @Override
    public void setCancelled(boolean cancelled) {
        throw new UnsupportedOperationException("Not supported on disconnect handler context");
    }

}
