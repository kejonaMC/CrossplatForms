package dev.projectg.crossplatforms.form;

import lombok.Getter;
import lombok.Setter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@Setter
@ConfigSerializable
public class ClickAction {

    @Nullable private List<String> commands;
    @Nullable private String server;
}
