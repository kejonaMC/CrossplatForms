package dev.projectg.crossplatforms.config.keyedserializer;

import dev.projectg.crossplatforms.serialize.KeyedType;

public interface Message extends KeyedType {

    void send();
}
