package dev.projectg.crossplatforms.config.keyedserializer;

import dev.projectg.crossplatforms.config.serializer.KeyedType;

public interface Message extends KeyedType {

    void send();
}
