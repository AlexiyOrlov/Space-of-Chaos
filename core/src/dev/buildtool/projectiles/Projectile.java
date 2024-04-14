package dev.buildtool.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import dev.buildtool.Functions;
import dev.buildtool.SaveData;
import dev.buildtool.Ship;
import dev.buildtool.SpaceOfChaos;
import dev.buildtool.StarSystem;

public class Projectile implements SaveData {
    public Texture texture;
    public int damage;
    /**
     * In degrees
     */
    public float rotation;
    public Circle area;
    public Ship shooter;
    public Ship target;
    protected int speed;
    protected float time;
    public Predicate<Ship> validTargets;
    protected StarSystem starSystem;

    public Projectile() {
    }

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

    public Projectile(Texture texture, int damage, float x, float y, float rotationDegrees, int speed, Ship shooter, Ship target, Predicate<Ship> shipPredicate, StarSystem starSystem)
    {
        this(texture, damage, x, y, rotationDegrees, speed, shooter, target);
        validTargets=shipPredicate;
        this.starSystem=starSystem;
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

    public void onDestroyed(StarSystem starSystem){

    }

    @Override
    public Map<String, Object> getData() {
        HashMap<String,Object> data=new HashMap<>();
        data.put("area x",area.x);
        data.put("area y",area.y);
        data.put("damage",damage);
        data.put("rotation",rotation);
        //shooter
        data.put("speed",speed);
        //star system
        //target
        data.put("texture id", SpaceOfChaos.INSTANCE.textureHashMap.inverse().get(texture));
        data.put("time",time);
        //valid targets
        data.put("target predicate",validTargets);
        data.put("velocity x",velocity.x);
        data.put("velocity y",velocity.y);
        data.put("x",x);
        data.put("y",y);
        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        area.x= (float) data.get("area x");
        area.y= (float) data.get("area y");
        damage= (int) data.get("damage");
        rotation= (float) data.get("rotation");
        speed= (int) data.get("speed");
        texture=SpaceOfChaos.INSTANCE.textureHashMap.get((int) data.get("texture id"));
        time= (float) data.get("time");
        validTargets= (Predicate<Ship>) data.get("target predicate");
        velocity.x= (float) data.get("velocity x");
        velocity.y= (float) data.get("velocity y");
        x= (float) data.get("x");
        y= (float) data.get("y");
    }
}
