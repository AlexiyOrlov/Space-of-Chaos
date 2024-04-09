package dev.buildtool.weapons;

import com.badlogic.gdx.graphics.Texture;

import java.util.List;

import dev.buildtool.Item;
import dev.buildtool.Projectile;
import dev.buildtool.Ship;

public abstract class Weapon extends Item {
    public final int damagePerProjectile;
    public final int projectileSpeed;
    public final Texture projectileTexture;
    public final String name;
    public final int cooldown;
    public Weapon(int damagePerProjectile,int fireDelay, int projectileSpeed, Texture projTexture, String name,Texture texture,int basePrice) {
        super(name,1,name,texture,basePrice);
        this.damagePerProjectile = damagePerProjectile;
        this.projectileSpeed = projectileSpeed;
        projectileTexture=projTexture;
        this.name=name;
        cooldown=fireDelay;
    }

    public Projectile[] shoot(float originX, float originY, float rotation, Ship shooter,Ship target)
    {
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
