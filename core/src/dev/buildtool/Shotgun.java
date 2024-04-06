package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

public class Shotgun extends Weapon{
    public Shotgun(int damagePerProjectile, int fireDelay, int projectileSpeed, Texture projTexture, String name) {
        super(damagePerProjectile, fireDelay, projectileSpeed, projTexture, name);
    }

    @Override
    public Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter) {
        Projectile[] projectiles=new Projectile[5];
        projectiles[0]=new Projectile(projectileTexture, damagePerProjectile,originX,originY,rotation-3,projectileSpeed,shooter);
        projectiles[1]=new Projectile(projectileTexture, damagePerProjectile,originX,originY,rotation-2,projectileSpeed,shooter);
        projectiles[2]=new Projectile(projectileTexture, damagePerProjectile,originX,originY,rotation,projectileSpeed,shooter);
        projectiles[3]=new Projectile(projectileTexture, damagePerProjectile,originX,originY,rotation+2,projectileSpeed,shooter);
        projectiles[4]=new Projectile(projectileTexture, damagePerProjectile,originX,originY,rotation+3,projectileSpeed,shooter);
        return projectiles;
    }
}
