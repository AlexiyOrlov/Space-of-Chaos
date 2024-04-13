package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

public class ExplorationDrone extends Item{
    public int explorationRate;
    public int integrity;
    public static final ExplorationDrone MARK1=new ExplorationDrone("drone mk1",1,"Exploration drone MK1", SpaceOfChaos.INSTANCE.droneTexture1, 1,1000);
    public ExplorationDrone(String type, int maxSize, String name, Texture texture,int explorationSpeed,int integrity) {
        super(maxSize, name, texture, 40000);
        explorationRate=explorationSpeed;
        this.integrity=integrity;
    }
}
