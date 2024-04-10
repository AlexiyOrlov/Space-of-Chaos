package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.List;

public class Hull extends Item{
    public static final Hull BASIC=new Hull(300, SpaceGame.INSTANCE.redStarshipTexture ,100,"Basic hull",3000);
    public static final Hull HORNET=new Hull(400,SpaceGame.INSTANCE.blackHullTexture, 160,"Hornet hull",40000);
    public static final Hull TRADING1=new Hull(600, SpaceGame.INSTANCE.tradingHull1Texture,200,"Trading hull",6000);
    public static final Hull BATTLE3=new Hull(450,SpaceGame.INSTANCE.battleHull3, 400,"Battle hull 3",50000);
    public static final Hull BATTLE2=new Hull(450,SpaceGame.INSTANCE.battleHull2,300,"Battle hull 2",40000);
    public static final Hull PIRATE1=new Hull(300,SpaceGame.INSTANCE.pirateHull1, 160,"Pirate hull 1",5000);
    public static ArrayList<Hull> battleHulls=new ArrayList<>();
    static {
        battleHulls.add(BATTLE2);
        battleHulls.add(BATTLE3);
    }
    public int capacity;
    public Texture look;

    public int integrity;

    public Hull(int capacity, Texture texture,int integrity,String name,int basePrice) {
        super(1,name,texture,basePrice);
        this.capacity = capacity;
        look=texture;
        this.integrity=integrity;
    }

    @Override
    public List<String> getTooltip() {
        List<String> tooltip= super.getTooltip();
        tooltip.add("Integrity: "+integrity);
        tooltip.add("Capacity: "+capacity);
        return tooltip;
    }
}
