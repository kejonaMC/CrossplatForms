package dev.projectg.crossplatforms.config.valuedserializer;

import dev.projectg.crossplatforms.config.serializer.ValuedType;

import java.math.BigDecimal;

public interface Number extends ValuedType {

    BigDecimal value();
}
