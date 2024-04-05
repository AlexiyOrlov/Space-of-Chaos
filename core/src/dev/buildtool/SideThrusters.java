package dev.buildtool;

public class SideThrusters {
    final float steeringSpeed;
    final float strafingSpeed;
    public static final SideThrusters SLOW=new SideThrusters(0.02f, 0.2f);
    public static final SideThrusters BASIC=new SideThrusters(0.03f, 3f);
    public SideThrusters(float steeringSpeed, float strafingSpeed) {
        this.steeringSpeed = steeringSpeed;
        this.strafingSpeed = strafingSpeed;
    }
}
