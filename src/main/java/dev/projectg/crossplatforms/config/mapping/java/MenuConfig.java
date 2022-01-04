package dev.projectg.crossplatforms.config.mapping.java;

import dev.projectg.crossplatforms.config.mapping.InterfaceCollection;
import lombok.Getter;

public class MenuConfig extends InterfaceCollection<JavaMenu> {

    @Getter
    private final int defaultVersion = 1;
}
