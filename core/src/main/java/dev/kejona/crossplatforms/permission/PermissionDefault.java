package dev.kejona.crossplatforms.permission;

public enum PermissionDefault {
    FALSE,
    TRUE,
    OP;

    public boolean asBoolean() {
        switch (this) {
            case OP:
            case FALSE:
                return false;
            case TRUE:
                return true;
            default:
                throw new AssertionError();
        }
    }
}
