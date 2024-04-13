package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

import java.util.List;

public class SideThrusters extends Item{
    final float steeringSpeed;
    final float strafingSpeed;
    public static final SideThrusters SLOW=new SideThrusters(0.02f, 2,"Slow thrusters", SpaceOfChaos.INSTANCE.thrusters1Texture, 3000);
    public static final SideThrusters BASIC=new SideThrusters(0.03f, 3,"Basic thrusters", SpaceOfChaos.INSTANCE.thrusters2Texture, 4000);
    public static final SideThrusters MARK2=new SideThrusters(0.04f,4,"Thrusters MK2",null,26000);
    public static final SideThrusters MARK3=new SideThrusters(0.05f,5,"Thrusters MK3",null,32000);
    public static final SideThrusters MARK4=new SideThrusters(0.06f,6,"Thrusters MK4",null,37000);
    public SideThrusters(float steeringSpeed, float strafingSpeed, String name, Texture texture,int basePrice) {
        super(1,name,texture,basePrice);
        this.steeringSpeed = steeringSpeed;
        this.strafingSpeed = strafingSpeed;
    }

    @Override
    public List<String> getTooltip() {
        List<String> tooltip= super.getTooltip();
        tooltip.add("Strafing speed: "+strafingSpeed);
        return tooltip;
    }
}
