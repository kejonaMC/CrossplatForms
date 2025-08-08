package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.spigot.adapter.ModernSpigotAdapter;
import dev.kejona.crossplatforms.spigot.adapter.SpigotAdapter;
import dev.kejona.crossplatforms.spigot.adapter.Versioned;

public class CrossplatFormsSpigot extends SpigotBase {

    private static final ModernSpigotAdapter MODERN_ADAPTER = new ModernSpigotAdapter();

    @Override
    public Versioned<SpigotAdapter> findVersionAdapter() {
        // Geen versie-specifieke adapter meer - gebruik de moderne API
        return Versioned.supported(MODERN_ADAPTER);
    }
}