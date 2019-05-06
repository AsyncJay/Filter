package co.uk.jaycarr.filter.listener;

import co.uk.jaycarr.filter.FilterPlugin;
import co.uk.jaycarr.filter.menu.FilterMenu;
import co.uk.jaycarr.filter.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class FilterListener implements Listener {

    /**
     * The filter plugin instance.
     */
    private final FilterPlugin plugin;

    public FilterListener(FilterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        PlayerData data = this.plugin.getPlayerRegistry().getData(player.getUniqueId());

        // Ensure player data exists and their filter is enabled
        if (data == null || !data.isFilterEnabled()) {
            return;
        }

        ItemStack item = event.getItem().getItemStack();

        // Determine whether item to pickup is in filter or not
        if (!data.isFiltered(item.getType())) {
            return;
        }

        // Cancel pickup as item type is filtered
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        // Ensure inventory belongs to this plugin
        if (!(holder instanceof FilterMenu)) {
            return;
        }

        // Cancel click -- prevent removal
        event.setCancelled(true);

        FilterMenu menu = (FilterMenu) holder;

        // Nothing to remove, no response to click
        if (menu.getData().getFilteredMaterials().isEmpty()) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();

        // Ensure item to remove exists
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        // Unfilter clicked item and refresh menu
        menu.getData().unfilter(clicked.getType());
        event.getWhoClicked().openInventory(menu.getInventory());
    }
}