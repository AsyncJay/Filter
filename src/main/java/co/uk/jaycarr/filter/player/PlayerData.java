package co.uk.jaycarr.filter.player;

import org.bukkit.Material;

import java.util.*;

public final class PlayerData {

    private final UUID uuid;
    private final Set<Material> filteredMaterials = new HashSet<>();

    private boolean filterEnabled;

    public PlayerData(UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public Set<Material> getFilteredMaterials() {
        return Collections.unmodifiableSet(this.filteredMaterials);
    }

    public void filter(Material material) {
        this.filteredMaterials.add(material);
    }

    public void unfilter(Material material) {
        this.filteredMaterials.remove(material);
    }

    public boolean isFiltered(Material material) {
        return this.filteredMaterials.contains(material);
    }

    public void setFilterEnabled(boolean filterEnabled) {
        this.filterEnabled = filterEnabled;
    }

    public boolean isFilterEnabled() {
        return this.filterEnabled;
    }
}