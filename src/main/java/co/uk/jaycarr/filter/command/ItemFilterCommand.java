package co.uk.jaycarr.filter.command;

import co.uk.jaycarr.filter.FilterPlugin;
import co.uk.jaycarr.filter.menu.FilterMenu;
import co.uk.jaycarr.filter.player.PlayerData;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;

public final class ItemFilterCommand implements CommandExecutor {

    /**
     * The usage messages for this command.
     */
    private static final Set<String> CMD_USAGE = ImmutableSet.of(
            ChatColor.GREEN + ChatColor.BOLD.toString() + "Item Filter Help",
            ChatColor.GREEN + "/itemfilter toggle" + ChatColor.GRAY + " - Toggle your item filter on or off.",
            ChatColor.GREEN + "/itemfilter add" + ChatColor.GRAY + " - Adds the item in your main hand to your filter.",
            ChatColor.GREEN + "/itemfilter view" + ChatColor.GRAY + " - View and edit your currently filtered items."
    );

    /**
     * The filter plugin instance.
     */
    private final FilterPlugin plugin;

    public ItemFilterCommand(FilterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        // No given sub-command, send usage
        if (args.length == 0) {
            CMD_USAGE.forEach(sender::sendMessage);
            return true;
        }

        Player player = (Player) sender;
        PlayerData data = this.plugin.getPlayerRegistry().getData(player.getUniqueId());

        // Ensure player data exists
        if (data == null) {
            player.sendMessage(ChatColor.RED + "No player data available. Try relogging.");
            return true;
        }

        if (args[0].equalsIgnoreCase("toggle")) {
            // Flip filter enabled state
            data.setFilterEnabled(!data.isFilterEnabled());
            player.sendMessage(ChatColor.GRAY + "Your item filter has been " +
                    (data.isFilterEnabled() ? "enabled" : "disabled") + "!");
        } else if (args[0].equalsIgnoreCase("add")) {
            // Get held item type -- main hand item cannot be null
            Material held = player.getInventory().getItemInMainHand().getType();

            // Cannot filter AIR
            if (held == Material.AIR) {
                player.sendMessage(ChatColor.RED + "You must be holding an item.");
                return true;
            }

            // Check if item type is already filtered
            if (data.isFiltered(held)) {
                player.sendMessage(ChatColor.RED + "Held item is already filtered.");
                return true;
            }

            // Add item type to filter
            data.filter(held);
            player.sendMessage(ChatColor.GREEN + "Added " + formatMaterial(held) + " to your item filter!");
        } else if (args[0].equalsIgnoreCase("view")) {
            // Open filter menu
            player.openInventory(new FilterMenu(data).getInventory());
        } else {
            // Not a valid sub-command, send usage
            CMD_USAGE.forEach(player::sendMessage);
        }

        return true;
    }

    /**
     * Formats the given material enum into a readable format.
     *
     * @param material the material
     * @return the formatted material name
     */
    private static String formatMaterial(Material material) {
        return WordUtils.capitalizeFully(material.name().replace("_", " "));
    }
}