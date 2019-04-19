package cn.wode490390.nukkit.nde;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockTNT;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.HugeExplodeSeedParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ExplodePacket;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NDExplosion extends Explosion {

    private final Level level;
    private final Position source;
    private final double size;
    private List<Block> affectedBlocks = new ArrayList<>();
    private final Object what;

    public NDExplosion(Position center, double size, Entity what) {
        super(center, size, what);
        this.level = center.getLevel();
        this.source = center;
        this.size = Math.max(size, 0);
        this.what = what;
    }

    @Override
    public boolean explodeB() {
        List<Vector3> send = new ArrayList<>();
        Vector3 source = this.source.floor();
        if (this.what instanceof Entity) {
            EntityNondestructiveExplodeEvent ev = new EntityNondestructiveExplodeEvent((Entity) this.what, this.source, this.affectedBlocks, (1d / this.size) * 100d);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            } else {
                this.affectedBlocks = ev.getBlockList();
            }
        }
        double explosionSize = this.size * 2d;
        double minX = NukkitMath.floorDouble(this.source.getX() - explosionSize - 1);
        double maxX = NukkitMath.ceilDouble(this.source.getX() + explosionSize + 1);
        double minY = NukkitMath.floorDouble(this.source.getY() - explosionSize - 1);
        double maxY = NukkitMath.ceilDouble(this.source.getY() + explosionSize + 1);
        double minZ = NukkitMath.floorDouble(this.source.getZ() - explosionSize - 1);
        double maxZ = NukkitMath.ceilDouble(this.source.getZ() + explosionSize + 1);
        AxisAlignedBB explosionBB = new SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
        Entity[] list = this.level.getNearbyEntities(explosionBB, this.what instanceof Entity ? (Entity) this.what : null);
        for (Entity entity : list) {
            double distance = entity.distance(this.source) / explosionSize;
            if (distance <= 1) {
                Vector3 motion = entity.subtract(this.source).normalize();
                int exposure = 1;
                double impact = (1 - distance) * exposure;
                int damage = (int) (((impact * impact + impact) / 2) * 8 * explosionSize + 1);
                if (this.what instanceof Entity) {
                    entity.attack(new EntityDamageByEntityEvent((Entity) this.what, entity, DamageCause.ENTITY_EXPLOSION, damage));
                } else if (this.what instanceof Block) {
                    entity.attack(new EntityDamageByBlockEvent((Block) this.what, entity, DamageCause.BLOCK_EXPLOSION, damage));
                } else {
                    entity.attack(new EntityDamageEvent(entity, DamageCause.BLOCK_EXPLOSION, damage));
                }
                entity.setMotion(motion.multiply(impact));
            }
        }
        this.affectedBlocks.stream().map((block) -> {
            if (block.getId() == Block.TNT) {
                ((BlockTNT) block).prime(ThreadLocalRandom.current().nextInt(10, 30), this.what instanceof Entity ? (Entity) this.what : null);
            }
            return block;
        }).forEach((block) -> {
            send.add(new Vector3(block.x - source.x, block.y - source.y, block.z - source.z));
        });
        ExplodePacket pk = new ExplodePacket();
        pk.x = (float) this.source.getX();
        pk.y = (float) this.source.getY();
        pk.z = (float) this.source.getZ();
        pk.radius = (float) this.size;
        pk.records = send.toArray(new Vector3[0]);
        this.level.addChunkPacket(source.getChunkX(), source.getChunkZ(), pk);
        this.level.addParticle(new HugeExplodeSeedParticle(this.source));
        this.level.addLevelSoundEvent(source, LevelSoundEventPacket.SOUND_EXPLODE);
        return true;
    }
}
