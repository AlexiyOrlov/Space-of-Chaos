package dev.buildtool.weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.util.List;
import java.util.function.Predicate;

import dev.buildtool.Item;
import dev.buildtool.StarSystem;
import dev.buildtool.projectiles.Projectile;
import dev.buildtool.Ship;
import dev.buildtool.SpaceOfChaos;

public abstract class Weapon extends Item {
    public final int damagePerProjectile;
    public final int projectileSpeed;
    public final Texture projectileTexture;
    public final String name;
    public final float cooldown;
    private final Sound sound;
    public Weapon(int damagePerProjectile, float fireDelay, int projectileSpeed, Texture projTexture, String name, Texture texture, int basePrice, Sound shootSound) {
        super(1,name,texture,basePrice);
        this.damagePerProjectile = damagePerProjectile;
        this.projectileSpeed = projectileSpeed;
        projectileTexture=projTexture;
        this.name=name;
        cooldown=fireDelay;
        this.sound=shootSound;
    }

    public Projectile[] shoot(float originX, float originY, float rotation, Ship shooter, Ship target, StarSystem starSystem)
    {
        if(sound!=null && SpaceOfChaos.INSTANCE.playerShip!=null && !SpaceOfChaos.INSTANCE.playerShip.isLanded() && shooter.getCurrentSystem()== SpaceOfChaos.INSTANCE.playerShip.getCurrentSystem())
            sound.play(0.1f);
        return createProjectiles(originX, originY, rotation, shooter, target,ship -> true, starSystem);
    }

    public Projectile[] shoot(float originX, float originY, float rotation, Ship shooter, Ship target, Predicate<Ship> shipPredicate, StarSystem starSystem)
    {
        shoot(originX, originY, rotation, shooter, target, starSystem);
        return createProjectiles(originX, originY, rotation, shooter, target,shipPredicate, starSystem);
    }
    public abstract Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter, Ship target, Predicate<Ship> shipPredicate, StarSystem starSystem);

    @Override
    public List<String> getTooltip() {
        List<String> tooltip= super.getTooltip();
        tooltip.add("Damage per projectile: "+damagePerProjectile);
        tooltip.add("Damage per minute: "+(60/cooldown*damagePerProjectile));
        return tooltip;
    }

    public static void initialize()
    {
        System.out.println("Weapons initialized");
    }
}
