package dev.projectg.crossplatforms.action;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Getter
@ConfigSerializable
public abstract class SimpleAction<T> implements Action {

    private T value;
}
