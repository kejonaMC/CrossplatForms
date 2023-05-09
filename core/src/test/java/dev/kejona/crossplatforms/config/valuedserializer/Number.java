package dev.kejona.crossplatforms.config.valuedserializer;

import dev.kejona.crossplatforms.serialize.KeyedType;

import java.math.BigDecimal;

public interface Number extends KeyedType {

    BigDecimal value();
}
