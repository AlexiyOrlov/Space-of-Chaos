package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

public abstract class Weapon {
    public int damagePerProjectile;
    public int fireDelay;
    public int projectileSpeed;
    public Texture projectileTexture;
    public final String name;
    public Weapon(int damagePerProjectile, int fireDelay, int projectileSpeed, Texture projTexture, String name) {
        this.damagePerProjectile = damagePerProjectile;
        this.fireDelay = fireDelay;
        this.projectileSpeed = projectileSpeed;
        projectileTexture=projTexture;
        this.name=name;
    }

    public abstract Projectile[] shoot(StarShip starShip);
}
