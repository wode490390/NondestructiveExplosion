package cn.wode490390.nukkit.nondestructiveexplosion;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.wode490390.nukkit.nde.NDExplosion;
import cn.wode490390.nukkit.nondestructiveexplosion.util.MetricsLite;

public class NondestructiveExplosion extends PluginBase implements Listener {

    private boolean activateNearbyTnt = false;

    @Override
    public void onEnable() {
        try {
            new MetricsLite(this, 4842);
        } catch (Throwable ignore) {

        }

        this.saveDefaultConfig();
        Config config = this.getConfig();

        String node = "activate-nearby-tnt";
        try {
            this.activateNearbyTnt = config.getBoolean(node, this.activateNearbyTnt);
        } catch (Exception e) {
            this.logLoadException(node, e);
        }

        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
        NDExplosion nde = new NDExplosion(event.getPosition(), (1d / event.getYield()) * 100d, event.getEntity());
        if (this.activateNearbyTnt) {
            nde.explodeA();
        }
        nde.explodeB();
    }

    private void logLoadException(String node, Throwable t) {
        this.getLogger().alert("An error occurred while reading the configuration '" + node + "'. Use the default value.", t);
    }
}
