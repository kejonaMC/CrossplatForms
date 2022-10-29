package dev.kejona.crossplatforms;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class TriggerException extends RuntimeException {

    /**
     * The config node that this exception has originated from.
     */
    private String sourceNode;

    public TriggerException() {
        super();
    }

    public TriggerException(String message) {
        super(message);
    }

    public TriggerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TriggerException(Throwable cause) {
        super(cause);
    }

    public TriggerException sourceNode(String node) {
        sourceNode = node;

        return this;
    }




}
