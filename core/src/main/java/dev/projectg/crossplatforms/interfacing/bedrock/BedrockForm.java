package dev.projectg.crossplatforms.interfacing.bedrock;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.Constants;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.action.Action;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.interfacing.Interface;
import dev.projectg.crossplatforms.serialize.ValuedType;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.List;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public abstract class BedrockForm extends Interface implements ValuedType {

    @Inject
    protected transient BedrockHandler bedrockHandler;

    protected transient final String permissionBase = Constants.Id() + ".form.";

    private final List<Action> incorrectActions = Collections.emptyList();

    /**
     * Properly execute the response handler of a form, taking into account thread safety
     * @param runnable The response handler to execute
     * @see Runnable#run()
     */
    protected final void executeHandler(Runnable runnable) {
        if (bedrockHandler.executesResponseHandlersSafely()) {
            Logger.get().debug("Executing (1) response handler on thread: " + Thread.currentThread().getName());
            runnable.run();
        } else {
            serverHandler.executeSafely(() -> {
                Logger.get().debug("Executing (2) response handler on thread: " + Thread.currentThread().getName());
                runnable.run();
            });
        }
    }

    /**
     * Note: this calls {@link #executeHandler(Runnable)}, so it should not be called to execute this method.
     */
    protected void handleIncorrect(FormPlayer player) {
        executeHandler(() -> Action.affectPlayer(player, incorrectActions));
    }
}
