package dev.buildtool;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public interface Ship {
    float getX();
    float getY();

    Vector2 getVelocity();

    StarSystem getCurrentSystem();

    void damage(int damage);
    void onProjectileImpact(Projectile projectile);
    int getIntegrity();

    boolean overlaps(Circle with);
    boolean contains(Vector2 vector2);
    boolean isLanded();
}
