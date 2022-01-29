package dev.projectg.crossplatforms.interfacing.bedrock.modal;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.interfacing.ClickAction;
import dev.projectg.crossplatforms.interfacing.bedrock.BedrockForm;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.interfacing.InterfaceManager;
import dev.projectg.crossplatforms.utils.PlaceholderUtils;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;

import javax.annotation.Nonnull;
import java.util.UUID;

@ToString
@ConfigSerializable
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class ModalForm extends BedrockForm {

    private String content = "";

    @Required
    private ModalButton button1;

    @Required
    private ModalButton button2;

    @Override
    public void sendForm(@NotNull UUID bedrockPlayer, @Nonnull InterfaceManager interfaceManager) {
        Logger logger = Logger.getLogger();

        Player player = Bukkit.getServer().getPlayer(bedrockPlayer);
        if (player == null) {
            logger.severe("Unable to find a Bukkit Player for the given UUID: " + bedrockPlayer);
            return;
        }

        BedrockHandler bedrockHandler = CrossplatForms.getInstance().getBedrockHandler();
        if (!bedrockHandler.isBedrockPlayer(bedrockPlayer)) {
            logger.severe("Player with UUID " + bedrockPlayer + " is not a Bedrock Player!");
            return;
        }

        org.geysermc.cumulus.ModalForm form = org.geysermc.cumulus.ModalForm.of(
                PlaceholderUtils.setPlaceholders(player, super.getTitle()),
                PlaceholderUtils.setPlaceholders(player, content),
                PlaceholderUtils.setPlaceholders(player, button1.getText()),
                PlaceholderUtils.setPlaceholders(player, button2.getText()));

        // Set the response handler
        form.setResponseHandler((responseData) -> {
            ModalFormResponse response = form.parseResponse(responseData);
            if (!response.isCorrect()) {
                // isCorrect() = !isClosed() && !isInvalid()
                // player closed the form or returned invalid info (see FormResponse)
                return;
            }

            int id = response.getClickedButtonId();
            ClickAction action = switch (id) {
                case 0 -> button1;
                case 1 -> button2;
                default -> throw new AssertionError();
            };

            // Handle effects of pressing the button
            action.affectPlayer(interfaceManager, player);
        });

        // Send the form to the floodgate player
        bedrockHandler.sendForm(bedrockPlayer, form);
    }
}
