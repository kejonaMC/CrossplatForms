package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.config.serializer.ValuedTypeSerializer;

/**
 * This serializer must be registered exact.
 */
public class ComponentSerializer extends ValuedTypeSerializer<CustomComponent> {

    public ComponentSerializer() {
        registerType(Dropdown.TYPE, Dropdown.class);
        registerType(Input.TYPE, Input.class);
        registerType(Label.TYPE, Label.class);
        registerType(Slider.TYPE, Slider.class);
        registerType(StepSlider.TYPE, StepSlider.class);
        registerType(Toggle.TYPE, Toggle.class);
    }
}
