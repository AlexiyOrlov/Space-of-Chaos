package dev.buildtool.projectiles;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.function.Predicate;

import dev.buildtool.Ship;
import dev.buildtool.SpaceOfChaos;

public class SplittingProjectile extends Projectile{


    public SplittingProjectile(Texture texture, int damage, float x, float y, float rotationDegrees, int speed, Ship shooter, Ship target, Predicate<Ship> shipPredicate) {
        super(texture, damage, x, y, rotationDegrees, speed, shooter, target, shipPredicate);
    }

    @Override
    public void update(float deltaTime, ArrayList<Projectile> projectilesToAdd, ArrayList<Projectile> projectilesToRemove) {
        super.update(deltaTime, projectilesToAdd, projectilesToRemove);
        if(time>0.25)
        {
            int rotation= SpaceOfChaos.random.nextInt(-180,180);
            for (int i = 0; i < 20; i++) {
                Projectile projectile=new Projectile(texture,damage-3,x,y,rotation,speed,shooter,target,validTargets);
                projectilesToAdd.add(projectile);
                rotation+=18;
            }
            projectilesToRemove.add(this);
        }
    }
}
