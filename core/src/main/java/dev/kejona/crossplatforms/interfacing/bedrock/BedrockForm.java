package dev.kejona.crossplatforms.interfacing.bedrock;

import com.google.inject.Inject;
import dev.kejona.crossplatforms.Constants;
import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.action.Action;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.interfacing.Interface;
import dev.kejona.crossplatforms.resolver.Resolver;
import dev.kejona.crossplatforms.serialize.ValuedType;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.util.FormImage;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@ToString
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public abstract class BedrockForm extends Interface implements ValuedType {

    @Inject
    protected transient BedrockHandler bedrockHandler;

    protected final transient String permissionBase = Constants.Id() + ".form.";

    // this needs to be moved to the form implementation if form specific actions are introduced
    private List<Action<? super BedrockForm>> incorrectActions = Collections.emptyList();

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
    protected void handleIncorrect(FormPlayer player, Resolver resolver) {
        executeHandler(() -> Action.affectPlayer(player, incorrectActions, resolver, this));
    }

    @Nullable
    public static FormImage createFormImage(@Nullable String data) {
        if (data == null || data.isEmpty()) {
            return null;
        } else {
            FormImage.Type type;
            if (data.startsWith("https://") || data.startsWith("http://")) {
                type = FormImage.Type.URL;
            } else {
                type = FormImage.Type.PATH;
            }
            return FormImage.of(type, data);
        }
    }
}
