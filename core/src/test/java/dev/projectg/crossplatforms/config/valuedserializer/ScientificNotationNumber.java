package dev.projectg.crossplatforms.config.valuedserializer;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.math.BigDecimal;
import java.util.Objects;

@ConfigSerializable
public class ScientificNotationNumber extends Number {

    private double mantissa;
    private int exponent;

    @SuppressWarnings("unused") // configurate
    private ScientificNotationNumber() {

    }

    public ScientificNotationNumber(double mantissa, int exponent) {
        super("scientific_notation");
        this.mantissa = mantissa;
        this.exponent = exponent;
    }

    @Override
    public BigDecimal value() {
        return new BigDecimal(mantissa).scaleByPowerOfTen(exponent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScientificNotationNumber that = (ScientificNotationNumber) o;
        return Double.compare(that.mantissa, mantissa) == 0 && exponent == that.exponent && sameType(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mantissa, exponent);
    }

    @Override
    public String toString() {
        return "ScientificNotationNumber{" + "mantissa=" + mantissa + ", exponent=" + exponent + ", type=" + identifier() + "}";
    }
}
