package dev.buildtool.weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.util.function.Predicate;

import dev.buildtool.Ship;
import dev.buildtool.StarSystem;
import dev.buildtool.projectiles.Missile;
import dev.buildtool.projectiles.Projectile;

public class MissileLauncher extends Weapon{
    public MissileLauncher(int damagePerProjectile, float fireDelay, int projectileSpeed, Texture projTexture, String name, Texture texture, int basePrice, Sound shootSound) {
        super(damagePerProjectile, fireDelay, projectileSpeed, projTexture, name, texture, basePrice, shootSound);
    }

    @Override
    public Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter, Ship target, Predicate<Ship> shipPredicate, StarSystem starSystem) {
        return new Projectile[]{new Missile(projectileTexture,25,originX,originY,rotation,projectileSpeed,shooter,target,shipPredicate, starSystem)};
    }

    @Override
    int projectilesPerShot() {
        return 1;
    }
}
