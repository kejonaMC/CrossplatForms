package dev.kejona.crossplatforms.parser;

import dev.kejona.crossplatforms.serialize.ValuedTypeSerializer;

public class ParserSerializer extends ValuedTypeSerializer<Parser> {

    public ParserSerializer() {
        registerType(PlaceholderParser.TYPE, PlaceholderParser.class);
        registerType(BlockPlaceholderParser.TYPE, BlockPlaceholderParser.class);
        registerType(ReplacementParser.TYPE, ReplacementParser.class);
    }
}
