package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.List;

public class Hull extends Item{
    public static final Hull BASIC=new Hull(100, SpaceGame.INSTANCE.redStarshipTexture ,100,"Basic hull",3000);
    public static final Hull HORNET=new Hull(150,SpaceGame.INSTANCE.blackHullTexture, 160,"Hornet",40000);
    public static final Hull BUMBLEBEE=new Hull(200,SpaceGame.INSTANCE.blackHull2Texture, 200,"Bumblebee",70000);
    private static final Hull TRADING1=new Hull(250, SpaceGame.INSTANCE.tradingHull1Texture,200,"Trading hull 1",50000);
    private static final Hull TRADING2=new Hull(300,SpaceGame.INSTANCE.tradingHull2Texture, 240,"Trading hull 2",60000);
    private static final Hull BATTLE3=new Hull(250,SpaceGame.INSTANCE.battleHull3, 400,"Battle hull 3",50000);
    private static final Hull BATTLE1=new Hull(250,SpaceGame.INSTANCE.battleHull3Texture,450,"Battle hull 1",60000);
    private static final Hull BATTLE2=new Hull(250,SpaceGame.INSTANCE.battleHull2,300,"Battle hull 2",40000);
    private static final Hull PIRATE1=new Hull(150,SpaceGame.INSTANCE.pirateHull1, 160,"Pirate hull 1",40000);
    private static final Hull PIRATE2=new Hull(120,SpaceGame.INSTANCE.pirateHull2Texture, 140,"Pirate hull 2",35000);
    private static final Hull PIRATE3=new Hull(180,SpaceGame.INSTANCE.pirateHull3Texture, 190,"Pirate hull 3",54000);
    public static ArrayList<Hull> battleHulls=new ArrayList<>();
    public static ArrayList<Hull> tradingHulls=new ArrayList<>();
    public static ArrayList<Hull> pirateHulls=new ArrayList<>();
    static {
        battleHulls.add(BATTLE1);
        battleHulls.add(BATTLE2);
        battleHulls.add(BATTLE3);

        tradingHulls.add(TRADING1);
        tradingHulls.add(TRADING2);

        pirateHulls.add(PIRATE1);
        pirateHulls.add(PIRATE2);
        pirateHulls.add(PIRATE3);
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
