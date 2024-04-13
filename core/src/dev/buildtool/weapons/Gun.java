package dev.buildtool.weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.util.function.Predicate;

import dev.buildtool.Projectile;
import dev.buildtool.Ship;
import dev.buildtool.SpaceOfChaos;

public class Gun extends Weapon {
    public Gun(int damagePerProjectile, int fireDelay, int projectileSpeed, Texture texture, String name, Sound sound) {
        super(damagePerProjectile, fireDelay, projectileSpeed,texture, name, SpaceOfChaos.INSTANCE.basicGunTexture,3500, sound);
    }

    @Override
    public Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter, Ship target, Predicate<Ship> shipPredicate) {
        Projectile[] projectiles=new Projectile[1];
        projectiles[0]=new Projectile(projectileTexture,damagePerProjectile,originX,originY,rotation,projectileSpeed, shooter,target);
        return projectiles;
    }
}
