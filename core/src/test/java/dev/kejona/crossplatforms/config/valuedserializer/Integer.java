package dev.kejona.crossplatforms.config.valuedserializer;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.math.BigDecimal;
import java.util.Objects;

@ConfigSerializable
public class Integer implements Number {

    public static final String TYPE = "integer";

    @Required
    private int integer;

    @SuppressWarnings("unused") // configurate
    private Integer() {
    }

    public Integer(int i) {
        this.integer = i;
    }

    @Override
    public String type() {
        return TYPE;
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
        return integer == that.integer && type().equals(that.type());
    }

    @Override
    public String toString() {
        return "Integer{" + "integer=" + integer + "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(integer);
    }
}
