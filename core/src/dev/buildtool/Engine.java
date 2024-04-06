package dev.buildtool;

public class Engine {
    public static final Engine BASIC=new Engine("basic",5,60, 400, 0.02f);
    public static final Engine SLOW=new Engine("basic",2.5f,60, 400, 0.15f);
    public static final Engine MARK2=new Engine("mark2",6,70,400,0.03f);
    public static final Engine ENGINE_3=new Engine("mark3",7,80,400, 0.03f);
    public final float maxSpeed;
    public final float steering;
    public final String type;
    public final int jumpDistance;
    public final float aiSteering;

    public Engine(String type, float maxSpeed, float steering, int jumpDistance, float aiSteering) {
        this.maxSpeed = maxSpeed;
        this.type=type;
        this.steering = steering;
        this.jumpDistance=jumpDistance;
        this.aiSteering = aiSteering;
    }
}
