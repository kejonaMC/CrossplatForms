package dev.projectg.crossplatforms.form.bedrock.custom;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.geysermc.cumulus.util.ComponentType;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class ComponentSerializer implements TypeSerializer<CustomComponent> {

    @Override
    public CustomComponent deserialize(Type type, ConfigurationNode node) throws SerializationException {
        ComponentType componentType = node.node("type").get(ComponentType.class);
        if (componentType == null) {
            throw new SerializationException("Bedrock form at " + node.path() + " must contain a form type!");
        }

        return switch (componentType) {
            case DROPDOWN -> node.get(Dropdown.class);
            case INPUT -> node.get(Input.class);
            case LABEL -> node.get(Label.class);
            case SLIDER -> node.get(Slider.class);
            case STEP_SLIDER -> node.get(StepSlider.class);
            case TOGGLE -> node.get(Toggle.class);
        };
    }

    @Override
    public void serialize(Type type, @Nullable CustomComponent component, ConfigurationNode node) throws SerializationException {
        if (component == null) {
            node.raw(null);
            return;
        }

        switch (component.getType()) {
            case DROPDOWN:
                node.set(Dropdown.class, component);
            case INPUT:
                node.set(Input.class, component);
            case LABEL:
                node.set(Label.class, component);
            case SLIDER:
                node.set(Slider.class, component);
            case STEP_SLIDER:
                node.set(StepSlider.class, component);
            case TOGGLE:
                node.set(Toggle.class, component);
            default:
                throw new SerializationException("ComponentType " + component.getType() + " is not supported!");
        }
    }
}
