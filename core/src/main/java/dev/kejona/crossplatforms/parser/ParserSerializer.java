package dev.kejona.crossplatforms.parser;

import dev.kejona.crossplatforms.serialize.KeyedTypeSerializer;

public class ParserSerializer extends KeyedTypeSerializer<Parser> {

    public ParserSerializer() {
        registerType(PlaceholderParser.TYPE, PlaceholderParser.class);
        registerType(BlockPlaceholderParser.TYPE, BlockPlaceholderParser.class);
        registerType(ReplacementParser.TYPE, ReplacementParser.class);
    }
}
