package dev.kejona.crossplatforms.config.keyedserializer;

import dev.kejona.crossplatforms.serialize.KeyedType;

public interface Message extends KeyedType {

    void send();
}
