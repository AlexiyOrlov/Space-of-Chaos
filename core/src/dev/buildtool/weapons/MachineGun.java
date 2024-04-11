package dev.buildtool.weapons;

import com.badlogic.gdx.graphics.Texture;

import dev.buildtool.Projectile;
import dev.buildtool.Ship;
import dev.buildtool.SpaceGame;

public class MachineGun extends Weapon{
    public MachineGun(int damagePerProjectile, float fireDelay, int projectileSpeed, Texture projTexture, String name, Texture texture, int basePrice) {
        super(damagePerProjectile, fireDelay, projectileSpeed, projTexture, name, texture, basePrice);
    }

    @Override
    public Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter, Ship target) {
        Projectile[] projectiles=new Projectile[1];
        projectiles[0]=new Projectile(this.projectileTexture,this.damagePerProjectile,originX,originY,rotation,projectileSpeed,shooter,target);
        return projectiles;
    }
}
