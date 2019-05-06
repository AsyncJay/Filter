package co.uk.jaycarr.filter.listener;

import co.uk.jaycarr.filter.FilterPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class ConnectionListener implements Listener {

    private final FilterPlugin plugin;

    public ConnectionListener(FilterPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            UUID uuid = event.getPlayer().getUniqueId();
            this.plugin.getPlayerRegistry().loadData(uuid); // Load player data
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            UUID uuid = event.getPlayer().getUniqueId();
            this.plugin.getPlayerRegistry().saveData(uuid); // Save player data
        });
    }
}