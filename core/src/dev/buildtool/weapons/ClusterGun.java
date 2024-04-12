package dev.buildtool.weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import dev.buildtool.Projectile;
import dev.buildtool.Ship;
import dev.buildtool.SplittingProjectile;

public class ClusterGun extends Weapon{
    public ClusterGun(int damagePerProjectile, float fireDelay, int projectileSpeed, Texture projTexture, String name, Texture texture, int basePrice, Sound shootSound) {
        super(damagePerProjectile, fireDelay, projectileSpeed, projTexture, name, texture, basePrice, shootSound);
    }

    @Override
    public Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter, Ship target) {
        return new Projectile[]{new SplittingProjectile(projectileTexture,damagePerProjectile,originX,originY,rotation,projectileSpeed,shooter,target)};
    }
}
