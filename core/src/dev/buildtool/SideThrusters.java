package dev.buildtool;

public class SideThrusters {
    final float steeringSpeed;

    public static final SideThrusters BASIC=new SideThrusters(0.03f);
    public SideThrusters(float steeringSpeed) {
        this.steeringSpeed = steeringSpeed;
    }
}
