package dev.projectg.crossplatforms.config.valuedserializer;

import dev.projectg.crossplatforms.serialize.ValuedType;

import java.math.BigDecimal;

public interface Number extends ValuedType {

    BigDecimal value();
}
