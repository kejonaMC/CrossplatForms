package dev.projectg.crossplatforms.permission;

public enum DefaultPermission {
    FALSE,
    TRUE,
    OP;

    public static DefaultPermission from(boolean bool) {
        if (bool) {
            return TRUE;
        } else {
            return FALSE;
        }
    }
}
