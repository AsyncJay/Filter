package co.uk.jaycarr.filter.player;

import org.bukkit.Material;

import java.util.*;

public final class PlayerData {

    /**
     * The associated player {@link UUID}.
     */
    private final UUID uuid;
    /**
     * A set of currently filtered materials.
     */
    private final Set<Material> filteredMaterials = new HashSet<>();

    /**
     * The current filter state.
     */
    private boolean filterEnabled;

    public PlayerData(UUID uuid) {
        this.uuid = Objects.requireNonNull(uuid);
    }

    /**
     * Returns the associated player {@link UUID}.
     *
     * @return the player uuid
     */
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Returns an unmodifiable set consisting of the
     * currently filtered materials.
     *
     * @return a set of filtered materials
     */
    public Set<Material> getFilteredMaterials() {
        return Collections.unmodifiableSet(this.filteredMaterials);
    }

    /**
     * Adds the given material to the filter.
     *
     * @param material the material to filter
     */
    public void filter(Material material) {
        this.filteredMaterials.add(material);
    }

    /**
     * Removes the given material from the filter.
     *
     * @param material the material to unfilter
     */
    public void unfilter(Material material) {
        this.filteredMaterials.remove(material);
    }

    /**
     * Returns whether the given material is
     * filtered or not.
     *
     * @param material the material to check
     * @return true if filtered, otherwise false
     */
    public boolean isFiltered(Material material) {
        return this.filteredMaterials.contains(material);
    }

    /**
     * Sets the item filter state to the given
     * boolean.
     *
     * @param filterEnabled the new filter state
     */
    public void setFilterEnabled(boolean filterEnabled) {
        this.filterEnabled = filterEnabled;
    }

    /**
     * Returns the current state of the item filter.
     *
     * @return the current item filter state
     */
    public boolean isFilterEnabled() {
        return this.filterEnabled;
    }
}