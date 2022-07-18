package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import dev.kejona.crossplatforms.Resolver;

/**
 * Implementations must be immutable. Used in {@link Dropdown} and {@link StepSlider}
 */
public interface Option {
    String display();
    String returnText();
    Option with(Resolver resolver);
}
