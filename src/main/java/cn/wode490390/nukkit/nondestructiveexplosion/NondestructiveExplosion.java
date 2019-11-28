package cn.wode490390.nukkit.nondestructiveexplosion;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.plugin.PluginBase;
import cn.wode490390.nukkit.nde.NDExplosion;

public class NondestructiveExplosion extends PluginBase implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
        try {
            new MetricsLite(this);
        } catch (Throwable ignore) {

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
        NDExplosion nde = new NDExplosion(event.getPosition(), (1d / event.getYield()) * 100d, event.getEntity());
        nde.explodeA();
        nde.explodeB();
    }
}
