package dev.buildtool.weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.util.function.Predicate;

import dev.buildtool.projectiles.Projectile;
import dev.buildtool.Ship;

public class MachineGun extends Weapon{
    public MachineGun(int damagePerProjectile, float fireDelay, int projectileSpeed, Texture projTexture, String name, Texture texture, int basePrice, Sound sound) {
        super(damagePerProjectile, fireDelay, projectileSpeed, projTexture, name, texture, basePrice, sound);
    }

    @Override
    public Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter, Ship target, Predicate<Ship> shipPredicate) {
        Projectile[] projectiles=new Projectile[1];
        projectiles[0]=new Projectile(this.projectileTexture,this.damagePerProjectile,originX,originY,rotation,projectileSpeed,shooter,target,shipPredicate);
        return projectiles;
    }
}
