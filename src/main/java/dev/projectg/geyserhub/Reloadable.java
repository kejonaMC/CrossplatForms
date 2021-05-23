package dev.projectg.geyserhub;

import java.util.ArrayList;

public interface Reloadable {

    ArrayList<Reloadable> reloadables = new ArrayList<>();

    boolean reload();
}
