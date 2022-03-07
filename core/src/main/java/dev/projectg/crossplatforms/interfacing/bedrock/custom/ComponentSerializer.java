package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.config.serializer.ValuedTypeSerializer;

/**
 * This serializer must be registered exact.
 */
public class ComponentSerializer extends ValuedTypeSerializer<CustomComponent> {

    public ComponentSerializer() {
        registerType("dropdown", Dropdown.class);
        registerType("input", Input.class);
        registerType("label", Label.class);
        registerType("slider", Slider.class);
        registerType("step_slider", StepSlider.class);
        registerType("toggle", Toggle.class);
    }
}
