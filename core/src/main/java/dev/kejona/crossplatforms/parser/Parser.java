package dev.kejona.crossplatforms.parser;

import dev.kejona.crossplatforms.serialize.ValuedType;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.bedrock.custom.CustomComponent;

public interface Parser extends ValuedType {

    String parse(FormPlayer player, CustomComponent component, String primitive);
}
