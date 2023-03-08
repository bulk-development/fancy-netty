package ru.bulk.fancy.handler.context;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class FancyRemoteHandlerContext {

    private boolean cancelled;

}
