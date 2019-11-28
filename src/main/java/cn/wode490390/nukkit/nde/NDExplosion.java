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
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NDExplosion extends Explosion {

    protected static final int rays = 16;
    protected static final double stepLen = 0.3;

    protected final Level level;
    protected final Position source;
    protected final double size;
    protected List<Block> affectedBlocks = Lists.newArrayList();
    protected final Object what;

    public NDExplosion(Position center, double size, Entity what) {
        super(center, size, what);
        this.level = center.getLevel();
        this.source = center;
        this.size = Math.max(size, 0);
        this.what = what;
    }

    /**
     * Calculates which blocks will be destroyed by this explosion. If explodeB() is called without calling this, no blocks will be destroyed.
     *
     * @return success
     */
    @Override
    public boolean explodeA() {
        if (this.size < 0.1) {
            return false;
        }

        Vector3 vector = new Vector3();
        Vector3 vBlock = new Vector3();

        int mRays = rays - 1;
        for (int i = 0; i < rays; ++i) {
            for (int j = 0; j < rays; ++j) {
                for (int k = 0; k < rays; ++k) {
                    if (i == 0 || i == mRays || j == 0 || j == mRays || k == 0 || k == mRays) {
                        vector.setComponents(i / (double) mRays * 2 - 1, j / (double) mRays * 2 - 1, k / (double) mRays * 2 - 1);
                        double len = vector.length();
                        vector.setComponents((vector.x / len) * stepLen, (vector.y / len) * stepLen, (vector.z / len) * stepLen);
                        double pointerX = this.source.x;
                        double pointerY = this.source.y;
                        double pointerZ = this.source.z;

                        for (double blastForce = this.size * ThreadLocalRandom.current().nextInt(700, 1301) / 1000; blastForce > 0; blastForce -= stepLen * 0.75) {
                            int x = (int) pointerX;
                            int y = (int) pointerY;
                            int z = (int) pointerZ;
                            vBlock.x = pointerX >= x ? x : x - 1;
                            vBlock.y = pointerY >= y ? y : y - 1;
                            vBlock.z = pointerZ >= z ? z : z - 1;

                            if (vBlock.y < 0 || vBlock.y > 255) {
                                continue;
                            }

                            Block block = this.level.getBlock(vBlock);

                            if (block.getId() != 0) {
                                blastForce -= (block.getResistance() / 5 + 0.3) * stepLen;
                                if (blastForce > 0) {
                                    if (!this.affectedBlocks.contains(block)) {
                                        this.affectedBlocks.add(block);
                                    }
                                }
                            }

                            pointerX += vector.x;
                            pointerY += vector.y;
                            pointerZ += vector.z;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Executes the explosion's effects on the world. This includes destroying blocks (if any), harming and knocking back entities, and creating sounds and particles.
     *
     * @return success
     */
    @Override
    public boolean explodeB() {
        if (this.what instanceof Entity) {
            EntityNondestructiveExplodeEvent ev = new EntityNondestructiveExplodeEvent((Entity) this.what, this.source, this.affectedBlocks, (1d / this.size) * 100d);
            this.level.getServer().getPluginManager().callEvent(ev);
            if (ev.isCancelled()) {
                return false;
            } else {
                this.affectedBlocks = ev.getBlockList();
            }
        }

        double explosionSize = this.size * 2;
        for (Entity entity : this.level.getNearbyEntities(new SimpleAxisAlignedBB(
                NukkitMath.floorDouble(this.source.getX() - explosionSize - 1),
                NukkitMath.floorDouble(this.source.getY() - explosionSize - 1),
                NukkitMath.floorDouble(this.source.getZ() - explosionSize - 1),
                NukkitMath.ceilDouble(this.source.getX() + explosionSize + 1),
                NukkitMath.ceilDouble(this.source.getY() + explosionSize + 1),
                NukkitMath.ceilDouble(this.source.getZ() + explosionSize + 1)
        ), this.what instanceof Entity ? (Entity) this.what : null)) {
            double distance = entity.distance(this.source) / explosionSize;

            if (distance <= 1) {
                Vector3 motion = entity.subtract(this.source).normalize();
                double impact = 1 - distance;
                int damage = (int) (((impact * impact + impact) / 2) * 8 * explosionSize + 1);

                EntityDamageEvent ev;
                if (this.what instanceof Entity) {
                    ev = new EntityDamageByEntityEvent((Entity) this.what, entity, DamageCause.ENTITY_EXPLOSION, damage);
                } else if (this.what instanceof Block) {
                    ev = new EntityDamageByBlockEvent((Block) this.what, entity, DamageCause.BLOCK_EXPLOSION, damage);
                } else {
                    ev = new EntityDamageEvent(entity, DamageCause.BLOCK_EXPLOSION, damage);
                }

                entity.attack(ev);
                entity.setMotion(motion.multiply(impact));
            }
        }

        this.affectedBlocks.parallelStream().filter(block -> block instanceof BlockTNT)
                .forEach(block -> ((BlockTNT) block).prime(ThreadLocalRandom.current().nextInt(10, 31), this.what instanceof Entity ? (Entity) this.what : null));

        this.level.addParticle(new HugeExplodeSeedParticle(this.source));
        this.level.addLevelSoundEvent(this.source.floor(), LevelSoundEventPacket.SOUND_EXPLODE);

        return true;
    }
}
