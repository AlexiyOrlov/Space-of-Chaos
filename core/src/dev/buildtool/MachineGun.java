package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

public class MachineGun extends Weapon{
    public MachineGun(int damagePerProjectile, int fireDelay, int projectileSpeed, Texture projTexture, String name) {
        super(damagePerProjectile, fireDelay, projectileSpeed, projTexture, name);
    }

    @Override
    public Projectile[] createProjectiles(float originX, float originY, float rotation, Ship shooter) {
        return new Projectile[0];
    }
}
