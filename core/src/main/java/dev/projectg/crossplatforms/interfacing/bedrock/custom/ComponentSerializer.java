package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import dev.projectg.crossplatforms.config.serializer.ValuedTypeSerializer;

/**
 * This serializer must be registered exact.
 */
public class ComponentSerializer extends ValuedTypeSerializer<CustomComponent> {

    public ComponentSerializer() {
        registerType("DROPDOWN", Dropdown.class);
        registerType("INPUT", Input.class);
        registerType("LABEL", Label.class);
        registerType("SLIDER", Slider.class);
        registerType("STEP_SLIDER", StepSlider.class);
        registerType("TOGGLE", Toggle.class);
    }
}
