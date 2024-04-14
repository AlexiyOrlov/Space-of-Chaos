package dev.buildtool.weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.util.function.Predicate;

import dev.buildtool.Ship;
import dev.buildtool.StarSystem;
import dev.buildtool.projectiles.Projectile;

public class TriShot extends Weapon{
    public TriShot(int damagePerProjectile, float fireDelay, int projectileSpeed, Texture projTexture, String name, Texture texture, int basePrice, Sound shootSound) {
        super(damagePerProjectile, fireDelay, projectileSpeed, projTexture, name, texture, basePrice, shootSound);
    }

    @Override
    public Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter, Ship target, Predicate<Ship> shipPredicate, StarSystem starSystem) {
        Projectile[] projectiles=new Projectile[3];
        projectiles[0]=new Projectile(projectileTexture,damagePerProjectile,originX,originY,rotation+10,projectileSpeed,shooter,target,shipPredicate,starSystem);
        projectiles[1]=new Projectile(projectileTexture,damagePerProjectile,originX,originY,rotation,projectileSpeed,shooter,target,shipPredicate,starSystem);
        projectiles[2]=new Projectile(projectileTexture,damagePerProjectile,originX,originY,rotation-10,projectileSpeed,shooter,target,shipPredicate,starSystem);
        return projectiles;
    }
}
