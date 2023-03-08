package ru.bulk.fancy.handler.context;

import lombok.Data;
import ru.bulk.fancy.remote.FancyRemote;

@Data
public class FancyRemoteConnectHandlerContext extends FancyRemoteHandlerContext {

    private final FancyRemote remote;

}
