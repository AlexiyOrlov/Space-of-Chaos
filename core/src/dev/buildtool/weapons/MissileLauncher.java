package dev.buildtool.weapons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.util.List;

import dev.buildtool.Ship;
import dev.buildtool.StarSystem;
import dev.buildtool.projectiles.Missile;
import dev.buildtool.projectiles.Projectile;

public class MissileLauncher extends Weapon{
    public MissileLauncher(int damagePerProjectile, float fireDelay, int projectileSpeed, Texture projTexture, String name, Texture texture, int basePrice, Sound shootSound) {
        super(damagePerProjectile, fireDelay, projectileSpeed, projTexture, name, texture, basePrice, shootSound);
    }

    @Override
    public Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter, Ship target, StarSystem starSystem) {
        return new Projectile[]{new Missile(projectileTexture,25,originX,originY,rotation,projectileSpeed,shooter,target, starSystem)};
    }

    @Override
    public List<String> getTooltip() {
        List<String> strings=super.getTooltip();
        strings.add("Homing");
        return strings;
    }
}
