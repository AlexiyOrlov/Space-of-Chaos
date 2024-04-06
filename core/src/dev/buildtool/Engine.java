package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

public class Engine extends Item{
    public static final Engine BASIC=new Engine("basic",5,60, 400, 0.02f,SpaceGame.INSTANCE.engine1Texture,3000);
    public static final Engine SLOW=new Engine("basic",2.5f,60, 400, 0.15f,null,0);
    public static final Engine MARK2=new Engine("Engine mark2",6,70,400,0.03f,SpaceGame.INSTANCE.engine3Texture,35000);
    public static final Engine ENGINE_3=new Engine("Engine mark3",7,80,400, 0.03f,SpaceGame.INSTANCE.engine2Texture,70000);
    public final float maxSpeed;
    public final float steering;
    public final String type;
    public final int jumpDistance;
    public final float aiSteering;
    public final int price;

    public Engine(String type, float maxSpeed, float steering, int jumpDistance, float aiSteering, Texture texture,int price) {
        super(type,1,type,texture);
        this.maxSpeed = maxSpeed;
        this.type=type;
        this.steering = steering;
        this.jumpDistance=jumpDistance;
        this.aiSteering = aiSteering;
        this.price=price;
    }
}
