package dev.buildtool.projectiles;

import com.badlogic.gdx.graphics.Texture;

import java.util.function.Predicate;

import dev.buildtool.Ship;

public class Torpedo extends Projectile{
    public Torpedo(Texture texture, int damage, float x, float y, float rotationDegrees, int speed, Ship shooter, Ship target, Predicate<Ship> shipPredicate) {
        super(texture, damage, x, y, rotationDegrees, speed, shooter, target, shipPredicate);
    }
}
