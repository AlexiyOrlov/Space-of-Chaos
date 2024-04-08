package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

public abstract class Weapon extends Item{
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

    public Projectile[] shoot(float originX, float originY, float rotation, Ship shooter)
    {
        return createProjectiles(originX, originY, rotation, shooter);
    }

    public abstract Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter);
}
