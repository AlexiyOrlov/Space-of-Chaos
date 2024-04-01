package dev.buildtool;

public class Engine {
    public static final Engine BASIC=new Engine("basic",5,60);
    public float maxSpeed;
    public float steering;
    public String type;

    public Engine(String type, float maxSpeed, float steering) {
        this.maxSpeed = maxSpeed;
        this.type=type;
        this.steering = steering;
    }
}
