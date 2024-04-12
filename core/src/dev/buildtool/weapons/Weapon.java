package dev.buildtool.weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.util.List;

import dev.buildtool.Item;
import dev.buildtool.Projectile;
import dev.buildtool.Ship;
import dev.buildtool.SpaceGame;

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

    public Projectile[] shoot(float originX, float originY, float rotation, Ship shooter,Ship target)
    {
        if(sound!=null && shooter.getCurrentSystem()== SpaceGame.INSTANCE.playerShip.currentStarSystem)
            sound.play(0.1f);
        return createProjectiles(originX, originY, rotation, shooter, target);
    }

    public abstract Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter, Ship target);

    @Override
    public List<String> getTooltip() {
        List<String> tooltip= super.getTooltip();
        tooltip.add("Damage per projectile: "+damagePerProjectile);
        tooltip.add("Damage per minute: "+(60/cooldown*damagePerProjectile));
        return tooltip;
    }
}
