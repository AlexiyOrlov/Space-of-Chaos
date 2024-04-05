package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

public class Gun extends Weapon{
    public Gun(int damagePerProjectile, int fireDelay, int projectileSpeed, Texture texture,String name) {
        super(damagePerProjectile, fireDelay, projectileSpeed,texture, name);
    }

    @Override
    public Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter) {
        Projectile[] projectiles=new Projectile[1];
        projectiles[0]=new Projectile(projectileTexture,damagePerProjectile,originX,originY,rotation,projectileSpeed, shooter);
        return projectiles;
    }
}
