package dev.projectg.crossplatforms.parser;

import dev.projectg.crossplatforms.serialize.ValuedType;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.bedrock.custom.CustomComponent;

public interface Parser extends ValuedType {

    String parse(FormPlayer player, CustomComponent component, String primitive);
}
