package dev.kejona.crossplatforms.config.valuedserializer;

import dev.kejona.crossplatforms.serialize.ValuedType;

import java.math.BigDecimal;

public interface Number extends ValuedType {

    BigDecimal value();
}
