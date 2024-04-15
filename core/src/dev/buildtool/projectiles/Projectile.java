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
import dev.buildtool.NPCPilot;
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
    public Predicate<Ship> validTargets=ship -> true;
    protected StarSystem starSystem;

    public Vector2 velocity=new Vector2();
    public float x,y;
    public int shooterId,targetId=-1;

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
        if(shooter instanceof NPCPilot npcPilot)
        {
            shooterId=npcPilot.id;
        }
        else
            shooterId=0;
        if(target!=null)
        {
            targetId=target.getId();
        }
    }

    public Projectile(Texture texture, int damage, float x, float y, float rotationDegrees, int speed, Ship shooter, Ship target, Predicate<Ship> shipPredicate, StarSystem starSystem)
    {
        this(texture, damage, x, y, rotationDegrees, speed, shooter, target);
        validTargets=shipPredicate;
        this.starSystem=starSystem;
    }

    public void update(float deltaTime, ArrayList<Projectile> projectilesToAdd, ArrayList<Projectile> projectilesToRemove)
    {
        x+= velocity.x;
        y+= velocity.y;
        area.set(x,y,texture.getWidth()/2);
        time+=deltaTime;
//        if(shooter==null)
//            throw new RuntimeException("Shooter is null");
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
        //TODO
//        data.put("target predicate",validTargets);
        data.put("velocity x",velocity.x);
        data.put("velocity y",velocity.y);
        data.put("x",x);
        data.put("y",y);
        data.put("shooter",shooterId);
        data.put("target",targetId);
        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        x= (float)(double)  data.get("x");
        y= (float)(double)  data.get("y");
        texture=SpaceOfChaos.INSTANCE.textureHashMap.get((int) data.get("texture id"));
        area=new Circle(x,y,texture.getWidth()/2);
        area.x= (float)(double) data.get("area x");
        area.y= (float)(double)  data.get("area y");
        damage= (int) data.get("damage");
        rotation= (float)(double)  data.get("rotation");
        speed= (int) data.get("speed");
        time= (float)(double) data.get("time");
//        validTargets= (Predicate<Ship>) data.get("target predicate");
        velocity.x= (float)(double)  data.get("velocity x");
        velocity.y= (float)(double)  data.get("velocity y");
        shooterId= (int) data.get("shooter");
        for (StarSystem system : SpaceOfChaos.INSTANCE.starSystems) {
            for (Ship ship : system.ships) {
                if(ship.getId()==shooterId)
                {
                    shooter=ship;
                    break;
                }
            }
        }
        for (StarSystem system : SpaceOfChaos.INSTANCE.starSystems) {
            for (Ship ship : system.ships) {
                if(ship.getId()==targetId)
                {
                    target=ship;
                    break;
                }
            }
        }
//        if(shooter==null)
//            throw new RuntimeException("Shooter is null for id "+shooterId);
    }
}
