package dev.kejona.crossplatforms.interfacing;

public class ArgumentException extends Exception {
    private static final long serialVersionUID = 0L;

    public ArgumentException(String message) {
        super(message);
    }

    public ArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ArgumentException missingArg(String argumentId) {
        return new ArgumentException("Argument with id " + argumentId + " was not provided.");
    }
}
