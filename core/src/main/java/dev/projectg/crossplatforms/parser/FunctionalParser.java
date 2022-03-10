package dev.projectg.crossplatforms.parser;

import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.CustomComponent;

@FunctionalInterface
public interface FunctionalParser {

    String parse(FormPlayer player, CustomComponent component, String primitive);
}
