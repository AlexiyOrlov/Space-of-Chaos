package dev.buildtool;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import dev.buildtool.projectiles.Projectile;

public interface Ship {
    float getX();
    float getY();

    Vector2 getVelocity();

    StarSystem getCurrentSystem();
    void setCurrentSystem(StarSystem starSystem);
    void damage(int damage);
    void onProjectileImpact(Projectile projectile);
    int getIntegrity();

    boolean overlaps(Circle with);
    boolean contains(Vector2 vector2);
    boolean isLanded();
    void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer);
}
