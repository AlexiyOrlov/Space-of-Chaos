package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

public class Gun extends Weapon{
    public Gun(int damagePerProjectile, int fireDelay, int projectileSpeed, Texture texture,String name) {
        super(damagePerProjectile, fireDelay, projectileSpeed,texture, name);
    }

    @Override
    public Projectile[] shoot(StarShip starShip) {
        Projectile[] projectiles=new Projectile[1];
        projectiles[0]=new Projectile(projectileTexture,damagePerProjectile,starShip.x,starShip.y,starShip.rotation,projectileSpeed );
        return projectiles;
    }
}
