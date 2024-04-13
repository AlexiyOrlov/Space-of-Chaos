package dev.buildtool.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.function.Predicate;

import dev.buildtool.Functions;
import dev.buildtool.Ship;
import dev.buildtool.StarSystem;

public class Missile extends Projectile{
    public Missile(Texture texture, int damage, float x, float y, float rotationDegrees, int speed, Ship shooter, Ship target, Predicate<Ship> shipPredicate, StarSystem starSystem) {
        super(texture, damage, x, y, rotationDegrees, speed, shooter, target, shipPredicate,starSystem);
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
}
