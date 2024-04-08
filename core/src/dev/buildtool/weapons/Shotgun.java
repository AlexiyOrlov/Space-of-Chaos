package dev.buildtool.weapons;

import com.badlogic.gdx.graphics.Texture;

import dev.buildtool.Projectile;
import dev.buildtool.Ship;
import dev.buildtool.SpaceGame;
import dev.buildtool.Weapon;

public class Shotgun extends Weapon {
    public Shotgun(int damagePerProjectile, int fireDelay, int projectileSpeed, Texture projTexture, String name) {
        super(damagePerProjectile, fireDelay, projectileSpeed, projTexture, name, SpaceGame.INSTANCE.shotgunTexture, 70000);
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
