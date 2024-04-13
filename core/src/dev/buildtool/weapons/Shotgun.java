package dev.buildtool.weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.util.function.Predicate;

import dev.buildtool.Projectile;
import dev.buildtool.Ship;
import dev.buildtool.SpaceOfChaos;

public class Shotgun extends Weapon {
    public Shotgun(int damagePerProjectile, int fireDelay, int projectileSpeed, Texture projTexture, String name, int basePrice, Sound sound) {
        super(damagePerProjectile, fireDelay, projectileSpeed, projTexture, name, SpaceOfChaos.INSTANCE.shotgunTexture, basePrice, sound);
    }

    @Override
    public Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter, Ship target, Predicate<Ship> shipPredicate) {
        Projectile[] projectiles=new Projectile[5];
        projectiles[0]=new Projectile(projectileTexture, damagePerProjectile,originX,originY,rotation-3,projectileSpeed,shooter,target,shipPredicate);
        projectiles[1]=new Projectile(projectileTexture, damagePerProjectile,originX,originY,rotation-2,projectileSpeed,shooter,target,shipPredicate);
        projectiles[2]=new Projectile(projectileTexture, damagePerProjectile,originX,originY,rotation,projectileSpeed,shooter,target,shipPredicate);
        projectiles[3]=new Projectile(projectileTexture, damagePerProjectile,originX,originY,rotation+2,projectileSpeed,shooter,target,shipPredicate);
        projectiles[4]=new Projectile(projectileTexture, damagePerProjectile,originX,originY,rotation+3,projectileSpeed,shooter,target,shipPredicate);
        return projectiles;
    }
}
