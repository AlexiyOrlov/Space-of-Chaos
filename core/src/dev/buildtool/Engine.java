package dev.buildtool;

public class Engine {
    public static final Engine BASIC=new Engine("basic",5,60, 400);
    public final float maxSpeed;
    public final float steering;
    public final String type;
    public final int jumpDistance;

    public Engine(String type, float maxSpeed, float steering, int jumpDistance) {
        this.maxSpeed = maxSpeed;
        this.type=type;
        this.steering = steering;
        this.jumpDistance=jumpDistance;
    }
}
