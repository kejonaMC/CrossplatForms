package dev.kejona.crossplatforms.interfacing.bedrock.custom;

import dev.kejona.crossplatforms.serialize.KeyedTypeSerializer;

/**
 * This serializer must be registered exact.
 */
public class ComponentSerializer extends KeyedTypeSerializer<CustomComponent> {

    public ComponentSerializer() {
        registerType(Dropdown.TYPE, Dropdown.class);
        registerType(Input.TYPE, Input.class);
        registerType(Label.TYPE, Label.class);
        registerType(Slider.TYPE, Slider.class);
        registerType(StepSlider.TYPE, StepSlider.class);
        registerType(Toggle.TYPE, Toggle.class);
    }
}
