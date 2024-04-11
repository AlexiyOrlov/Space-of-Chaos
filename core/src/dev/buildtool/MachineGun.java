package dev.buildtool;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import dev.buildtool.weapons.Weapon;

public class MachineGun extends Weapon {
    public MachineGun(int damagePerProjectile, int fireDelay, int projectileSpeed, Texture projTexture, String name, Sound sound) {
        super(damagePerProjectile, fireDelay, projectileSpeed, projTexture, name,null,100000, sound);
    }

    @Override
    public Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter, Ship target) {
        return new Projectile[0];
    }
}
