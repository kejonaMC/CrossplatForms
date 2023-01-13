package dev.kejona.crossplatforms.spigot.v1_13_R2;

import dev.kejona.crossplatforms.spigot.v1_12_R1.Adapter_v1_12_R1;
import org.bukkit.Material;

public class Adapter_v1_13_R2 extends Adapter_v1_12_R1 {

    /**
     * The flattening occurred during 1.13. This returns a different enum constant than previous versions.
     */
    @Override
    public Material playerHeadMaterial() {
        return Material.PLAYER_HEAD;
    }
}
