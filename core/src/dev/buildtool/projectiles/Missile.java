package dev.buildtool.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.function.Predicate;

import dev.buildtool.OneShotAnimation;
import dev.buildtool.Functions;
import dev.buildtool.Ship;
import dev.buildtool.SpaceOfChaos;
import dev.buildtool.StarSystem;

public class Missile extends Projectile implements DestructibleProjectile{
    private int integrity=30;
    public Missile(Texture texture, int damage, float x, float y, float rotationDegrees, int speed, Ship shooter, Ship target, StarSystem starSystem) {
        super(texture, damage, x, y, rotationDegrees, speed, shooter, target, starSystem);
    }

    @Override
    public void update(float deltaTime, ArrayList<Projectile> projectilesToAdd, ArrayList<Projectile> projectilesToRemove) {
        if(target!=null) {
           rotation=Functions.rotateTowards(rotation*MathUtils.degreesToRadians, x, y, target.getX(), target.getY(), -90*MathUtils.degreesToRadians, 0.1f)*MathUtils.radiansToDegrees;
        }
        float speed=300;
        x+= MathUtils.cosDeg(rotation+90)*deltaTime*speed;
        y+=MathUtils.sinDeg(rotation+90)*deltaTime*speed;
        area.set(x,y,texture.getWidth()/2);
        if(target!=null && (target.getCurrentSystem()!=starSystem || target.isLanded() || target.getIntegrity()<=0))
            target=null;
    }

    @Override
    public void onDestroyed(StarSystem starSystem) {
        OneShotAnimation oneShotAnimation =new OneShotAnimation(SpaceOfChaos.INSTANCE.explosionAnimation, x,y);
        starSystem.animations.add(oneShotAnimation);
        if(SpaceOfChaos.INSTANCE.playerShip!=null && starSystem==SpaceOfChaos.INSTANCE.playerShip.getCurrentSystem())
            SpaceOfChaos.INSTANCE.explosionSound.play(0.2f);
    }

    @Override
    public int getIntegrity() {
        return integrity;
    }

    @Override
    public void damage(int damage) {
        integrity-=damage;
    }
}
