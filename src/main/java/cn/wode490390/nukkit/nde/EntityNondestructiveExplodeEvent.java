package cn.wode490390.nukkit.nde;

import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.level.Position;

import java.util.List;

public class EntityNondestructiveExplodeEvent extends EntityExplodeEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public EntityNondestructiveExplodeEvent(Entity entity, Position position, List<Block> blocks, double yield) {
        super(entity, position, blocks, yield);
    }
}
