package co.uk.jaycarr.filter;

import co.uk.jaycarr.filter.command.ItemFilterCommand;
import co.uk.jaycarr.filter.listener.ConnectionListener;
import co.uk.jaycarr.filter.listener.FilterListener;
import co.uk.jaycarr.filter.menu.FilterMenu;
import co.uk.jaycarr.filter.player.PlayerRegistry;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class FilterPlugin extends JavaPlugin {

    private PlayerRegistry playerRegistry;

    @Override
    public void onEnable() {
        // Initialise player registry
        this.playerRegistry = new PlayerRegistry(this);

        // Load all data for online players
        if (this.isEnabled()) {
            this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
                for (Player player : this.getServer().getOnlinePlayers()) {
                    this.playerRegistry.loadData(player.getUniqueId());
                }
            });
        }

        // Register listeners
        this.getServer().getPluginManager().registerEvents(new ConnectionListener(this), this);
        this.getServer().getPluginManager().registerEvents(new FilterListener(this), this);

        // Register command
        this.getCommand("itemfilter").setExecutor(new ItemFilterCommand(this));
    }

    @Override
    public void onDisable() {
        // Close open filter menus
        this.getServer().getOnlinePlayers().stream()
                .filter(player -> player.getOpenInventory().getTopInventory().getHolder() instanceof FilterMenu)
                .forEach(Player::closeInventory);

        // Save all data from cache
        this.playerRegistry.getCache().keySet()
                .forEach(uuid -> this.playerRegistry.saveData(uuid));
    }

    public PlayerRegistry getPlayerRegistry() {
        return this.playerRegistry;
    }
}