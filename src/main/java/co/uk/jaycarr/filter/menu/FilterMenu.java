package co.uk.jaycarr.filter.menu;

import co.uk.jaycarr.filter.player.PlayerData;
import com.google.common.collect.ImmutableList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class FilterMenu implements InventoryHolder {

    /**
     * The item to display when the player filter is empty.
     */
    private static final ItemStack EMPTY_ITEM;
    /**
     * The lore to display on filtered items.
     */
    private static final List<String> FILTER_LORE = ImmutableList.of(
            ChatColor.GRAY + "This item is filtered and will not",
            ChatColor.GRAY + "be picked up when filter is enabled.",
            "",
            ChatColor.GRAY + "Click to remove."
    );

    static {
        EMPTY_ITEM = new ItemStack(Material.BARRIER);
        ItemMeta meta = EMPTY_ITEM.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "You have no filtered items!");
        EMPTY_ITEM.setItemMeta(meta);
    }

    /**
     * The player data associated with this menu.
     */
    private final PlayerData data;
    /**
     * The inventory associated with this menu.
     */
    private final Inventory inventory;

    public FilterMenu(PlayerData data) {
        this.data = Objects.requireNonNull(data);
        this.inventory = Bukkit.createInventory(
                this, 36, "Filter"
        );
    }

    @Override
    public Inventory getInventory() {
        // Clear current items for refresh
        this.inventory.clear();

        // Get filtered items
        Set<Material> filtered = this.data.getFilteredMaterials();

        // Filter is empty
        if (filtered.isEmpty()) {
            this.inventory.setItem(13, EMPTY_ITEM);
            return this.inventory;
        }

        // Fill inventory with filtered items
        for (Material material : filtered) {
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.setLore(FILTER_LORE);
            item.setItemMeta(meta);
            this.inventory.addItem(item);
        }

        return this.inventory;
    }

    /**
     * Returns the data associated with this menu.
     *
     * @return the player data
     */
    public PlayerData getData() {
        return this.data;
    }
}