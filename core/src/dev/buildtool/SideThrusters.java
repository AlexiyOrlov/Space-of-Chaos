package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.List;

public class SideThrusters extends Item{
    final float steeringSpeed;
    final float strafingSpeed;
    public static final SideThrusters SLOW=new SideThrusters(0.02f, 2,"Slow thrusters", SpaceOfChaos.INSTANCE.thrusters1Texture, 3000);
    public static final SideThrusters BASIC=new SideThrusters(0.03f, 3,"Basic thrusters", SpaceOfChaos.INSTANCE.thrusters2Texture, 4000);
    public static final SideThrusters MARK2=new SideThrusters(0.04f,4,"Thrusters MK2",SpaceOfChaos.INSTANCE.sideThrusters3, 52000);
    public static final SideThrusters MARK3=new SideThrusters(0.05f,5,"Thrusters MK3",SpaceOfChaos.INSTANCE.sideThrusters4, 63000);
    public static final SideThrusters MARK4=new SideThrusters(0.06f,6,"Thrusters MK4",SpaceOfChaos.INSTANCE.sideThrusters5, 74000);
    public static ArrayList<SideThrusters> sideThrusters=new ArrayList<>();
    static {
        sideThrusters.add(BASIC);
        sideThrusters.add(MARK2);
        sideThrusters.add(MARK3);
        sideThrusters.add(MARK4);
    }
    public SideThrusters(float steeringSpeed, float strafingSpeed, String name, Texture texture,int basePrice) {
        super(1,name,texture,basePrice);
        this.steeringSpeed = steeringSpeed;
        this.strafingSpeed = strafingSpeed;
    }

    @Override
    public List<String> getTooltip() {
        List<String> tooltip= super.getTooltip();
        tooltip.add("Strafing speed: "+strafingSpeed);
        tooltip.add("Steering speed: "+steeringSpeed);
        return tooltip;
    }

    public static void initialize()
    {
        System.out.println("Side thrusters initialized");
    }
    public static SideThrusters getRandomThrusters()
    {
        return sideThrusters.get(SpaceOfChaos.random.nextInt(sideThrusters.size()));
    }
}
