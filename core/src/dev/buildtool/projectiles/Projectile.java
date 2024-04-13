package dev.buildtool.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.function.Predicate;

import dev.buildtool.Functions;
import dev.buildtool.Ship;

public class Projectile {
    public Texture texture;
    public int damage;
    public float rotation;
    public Circle area;
    public final Ship shooter,target;
    protected int speed;
    protected float time;
    public Predicate<Ship> validTargets;

    public Projectile(Texture texture, int damage, float x, float y, float rotationDegrees, int speed, Ship shooter, Ship target) {
        this.texture = texture;
        this.damage = damage;
        this.velocity = new Vector2(MathUtils.cos((rotationDegrees +90)*MathUtils.degreesToRadians)* speed,MathUtils.sin((rotationDegrees +90)*MathUtils.degreesToRadians)* speed);
        this.x = x;
        this.y = y;
        this.rotation=rotationDegrees;
        area=new Circle(x,y,texture.getWidth()/2);
        this.shooter=shooter;
        this.target=target;
        this.speed=speed;
        validTargets=ship -> true;
    }

    public Projectile(Texture texture, int damage, float x, float y, float rotationDegrees, int speed, Ship shooter, Ship target,Predicate<Ship> shipPredicate)
    {
        this(texture, damage, x, y, rotationDegrees, speed, shooter, target);
        validTargets=shipPredicate;
    }

    public Vector2 velocity;
    public float x,y;
    public void update(float deltaTime, ArrayList<Projectile> projectilesToAdd, ArrayList<Projectile> projectilesToRemove)
    {
        x+= velocity.x;
        y+= velocity.y;
        area.set(x,y,texture.getWidth()/2);
        time+=deltaTime;
    }

    public void render(SpriteBatch spriteBatch)
    {
        spriteBatch.begin();
        Functions.drawRotated(spriteBatch,texture,x,y,rotation);
        spriteBatch.end();
    }
}
