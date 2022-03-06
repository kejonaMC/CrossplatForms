package dev.projectg.crossplatforms.config.valuedserializer;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.math.BigDecimal;

@ConfigSerializable
public class Integer extends Number {

    @Required
    private final int integer;

    @SuppressWarnings("unused") // configurate
    private Integer() {
        integer = 500;
    }

    public Integer(int i) {
        super("integer");
        this.integer = i;
    }

    @Override
    public BigDecimal value() {
        return new BigDecimal(integer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Integer that = (Integer) o;
        return integer == that.integer && sameType(that);
    }

    @Override
    public String toString() {
        return "Integer{" + "integer=" + integer + ", type=" + identifier() + "}";
    }
}
