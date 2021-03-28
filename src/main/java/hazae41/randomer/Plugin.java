package hazae41.randomer;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class Plugin extends JavaPlugin implements Listener {
  int minDistance;
  int maxDistance;
  int maxInhabited;

  @Override
  public void onEnable() {
    super.onEnable();

    saveDefaultConfig();

    minDistance = getConfig().getInt("min-chunk-distance");
    maxDistance = getConfig().getInt("max-chunk-distance");
    maxInhabited = getConfig().getInt("max-inhabited-seconds");

    getServer().getPluginManager().registerEvents(this, this);
  }

  static boolean isBad(Biome biome) {
    switch (biome) {
      case OCEAN:
      case COLD_OCEAN:
      case FROZEN_OCEAN:
      case WARM_OCEAN:
      case LUKEWARM_OCEAN:
      case DEEP_COLD_OCEAN:
      case DEEP_FROZEN_OCEAN:
      case DEEP_WARM_OCEAN:
      case DEEP_LUKEWARM_OCEAN:
        return true;
      default:
        return false;
    }
  }

  Location getRandomLocation(World world) {
    int tries = 0;

    while (true) {
      tries++;

      int cx = Random.getInt(minDistance, maxDistance);
      int cxs = Random.getBoolean() ? 1 : -1;

      int cz = Random.getInt(minDistance, maxDistance);
      int czs = Random.getBoolean() ? 1 : -1;

      Chunk chunk = world.getChunkAt(cxs * cx, czs * cz);
      Block block = chunk.getBlock(7, 80, 7);

      if (tries == 4096) {
        getLogger().warning("Could not find a good place");
        return block.getLocation();
      }

      if (chunk.getInhabitedTime() > maxInhabited * 20L)
        continue;
      if (isBad(block.getBiome()))
        continue;

      getLogger().info("Found a good place in " + tries + " tries.");
      return block.getLocation();
    }
  }

  @EventHandler
  public void onSpawn(PlayerSpawnLocationEvent e) {
    if (e.getPlayer().hasPlayedBefore()) return;
    World world = e.getSpawnLocation().getWorld();
    if (world == null) throw new NullPointerException();
    e.setSpawnLocation(getRandomLocation(world));
  }

  @EventHandler
  public void onRespawn(PlayerRespawnEvent e) {
    if (e.isAnchorSpawn() || e.isBedSpawn()) return;
    World world = e.getRespawnLocation().getWorld();
    if (world == null) throw new NullPointerException();
    Location location = getRandomLocation(world);
    e.setRespawnLocation(location);
  }
}
