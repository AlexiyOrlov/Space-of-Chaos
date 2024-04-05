package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

public abstract class Weapon {
    public int damagePerProjectile;
    public float fireDelay;
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

    public Projectile[] shoot(float originX,float originY,float rotation)
    {
        if(fireDelay<=0)
        {
            return createProjectiles(originX, originY, rotation);
        }
        return null;
    }

    public abstract Projectile[] createProjectiles(float originX, float originY, float rotation);

    public void work(float deltaTime)
    {
        if(fireDelay>0)
        {
            fireDelay-=deltaTime;
        }
    }
}
