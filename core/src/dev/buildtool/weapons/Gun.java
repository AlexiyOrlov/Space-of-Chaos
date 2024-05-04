package dev.buildtool.weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.util.function.Predicate;

import dev.buildtool.StarSystem;
import dev.buildtool.projectiles.Projectile;
import dev.buildtool.Ship;
import dev.buildtool.SpaceOfChaos;

public class Gun extends Weapon {
    public Gun(int damagePerProjectile, int fireDelay, int projectileSpeed, Texture texture, String name, Sound sound) {
        super(damagePerProjectile, fireDelay, projectileSpeed,texture, name, SpaceOfChaos.INSTANCE.basicGunTexture,3500, sound);
    }

    @Override
    public Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter, Ship target, Predicate<Ship> shipPredicate, StarSystem starSystem) {
        return new Projectile[] {new Projectile(projectileTexture,damagePerProjectile,originX,originY,rotation,projectileSpeed, shooter,target,shipPredicate, starSystem)};
    }
}
