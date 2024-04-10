package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

import java.util.List;

public class SideThrusters extends Item{
    final float steeringSpeed;
    final float strafingSpeed;
    public static final SideThrusters SLOW=new SideThrusters(0.02f, 2f,"Slow thrusters",SpaceGame.INSTANCE.thrusters1Texture, 3000);
    public static final SideThrusters BASIC=new SideThrusters(0.03f, 3f,"Basic thrusters",SpaceGame.INSTANCE.thrusters2Texture, 4000);
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
