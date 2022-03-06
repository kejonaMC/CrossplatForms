package dev.projectg.crossplatforms.config.valuedserializer;

import dev.projectg.crossplatforms.config.serializer.ValuedType;

import java.math.BigDecimal;

public abstract class Number extends ValuedType {

    public abstract BigDecimal value();

    protected Number() {
        super();
    }

    public Number(String type) {
        super(type);
    }
}
