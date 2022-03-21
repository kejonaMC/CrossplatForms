package dev.projectg.crossplatforms.interfacing.bedrock;

import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.config.serializer.ValuedType;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.interfacing.Interface;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.Form;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.function.Consumer;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public abstract class BedrockForm extends Interface implements ValuedType {

    protected transient final String permissionBase = Constants.Id() + ".form.";

    /**
     * Determines a way to safely execute the given response handler, and sets it on the given form.
     */
    protected final void setResponseHandler(Form form,
                                            Consumer<String> responseHandler,
                                            ServerHandler serverHandler,
                                            BedrockHandler bedrockHandler) {
        if (bedrockHandler.executesResponseHandlersSafely()) {
            form.setResponseHandler(responseHandler);
            Logger.getLogger().debug("Executing (1) response handler for " + form.getType() + " on thread: " + Thread.currentThread().getName());
        } else {
            form.setResponseHandler((data) -> serverHandler.executeSafely(() -> {
                Logger.getLogger().debug("Executing (2) response handler for " + form.getType() + " on thread: " + Thread.currentThread().getName());
                responseHandler.accept(data);
            }));
        }
    }
}
