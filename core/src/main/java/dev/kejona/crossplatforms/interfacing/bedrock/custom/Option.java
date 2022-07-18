package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import dev.kejona.crossplatforms.Resolver;
import org.jetbrains.annotations.Contract;

/**
 * Implementations must be immutable. Used in {@link Dropdown} and {@link StepSlider}
 */
public interface Option {
    String display();
    String returnText();

    @Contract(pure = true)
    Option with(Resolver resolver);
}
