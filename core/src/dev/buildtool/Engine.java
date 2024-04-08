package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

import java.util.List;

public class Engine extends Item{
    public static final Engine BASIC=new Engine("Basic engine",5, 400, 0.02f,SpaceGame.INSTANCE.engine1Texture,3000);
    public static final Engine SLOW=new Engine("Slow engine",2.5f, 400, 0.15f,null,0);
    public static final Engine MARK2=new Engine("Engine mark2",6, 400,0.03f,SpaceGame.INSTANCE.engine3Texture,35000);
    public static final Engine ENGINE_3=new Engine("Engine mark3",7, 400, 0.03f,SpaceGame.INSTANCE.engine2Texture,70000);
    public final float maxSpeed;
    public final String type;
    public final int jumpDistance;
    public final float aiSteering;
    public final int price;

    public Engine(String type, float maxSpeed, int jumpDistance, float aiSteering, Texture texture, int price) {
        super(type,1,type,texture,price);
        this.maxSpeed = maxSpeed;
        this.type=type;
        this.jumpDistance=jumpDistance;
        this.aiSteering = aiSteering;
        this.price=price;
    }

    @Override
    public List<String> getTooltip() {
        List<String> tooltip= super.getTooltip();
        tooltip.add("Max speed: "+maxSpeed);
        tooltip.add("Jump distance: "+jumpDistance);
        return tooltip;
    }
}
