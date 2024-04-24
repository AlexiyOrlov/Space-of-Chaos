package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Hull extends Item{
    public static final Hull BASIC=new Hull(100, SpaceOfChaos.INSTANCE.redStarshipTexture ,100,"Basic hull",3000);
    public static final Hull HORNET=new Hull(150, SpaceOfChaos.INSTANCE.blackHullTexture, 160,"'Hornet'",40000);
    public static final Hull BUMBLEBEE=new Hull(200, SpaceOfChaos.INSTANCE.blackHull2Texture, 200,"'Bumblebee'",70000);
    public static final Hull DRAGONFLY=new Hull(300,SpaceOfChaos.INSTANCE.dragonflyHull, 280,"'Dragonfly'",120000);
    public static final Hull BEETLE=new Hull(340,SpaceOfChaos.INSTANCE.beetleHull, 350,"'Beetle'",150000);
    public static final Hull AI_SMALL1=new Hull(100, SpaceOfChaos.INSTANCE.aiSmallHull1, 100,"AI small hull 1",-1);
    public static final Hull AI_SMALL2=new Hull(120, SpaceOfChaos.INSTANCE.aiSmallHull2, 120,"AI small hull 2",-1);
    public static final Hull AI_MEDIUM1=new Hull(200, SpaceOfChaos.INSTANCE.aiMediumHull1,200,"AI medium hull 1",-1);
    public static final Hull AI_MEDIUM2=new Hull(230, SpaceOfChaos.INSTANCE.aiMediumHull2, 230,"AI medium hull 2",-1);
    public static final Hull AI_BIG1=new Hull(300, SpaceOfChaos.INSTANCE.aiBigHull1, 300,"AI big hull 1",-1);
    public static final Hull AI_BIG2=new Hull(330, SpaceOfChaos.INSTANCE.aiMediumHull2, 330,"AI big hull 2",-1);
    public static final Hull AI_LARGE1=new Hull(380, SpaceOfChaos.INSTANCE.aiLargeHull1, 380,"AI large hull 1",-1);
    public static final Hull AI_LARGE2=new Hull(420, SpaceOfChaos.INSTANCE.aiLargeHull2,420,"AI large hull 2",-1);
    private static final Hull TRADING1=new Hull(250, SpaceOfChaos.INSTANCE.tradingHull1Texture,200,"Trading hull 1",50000);
    private static final Hull TRADING2=new Hull(300, SpaceOfChaos.INSTANCE.tradingHull2Texture, 240,"Trading hull 2",60000);
    private static final Hull TRADING3=new Hull(340,SpaceOfChaos.INSTANCE.tradingHull3, 280,"Trading hull 3",-1);
    private static final Hull BATTLE3=new Hull(250, SpaceOfChaos.INSTANCE.battleHull3, 400,"Battle hull 3",50000);
    private static final Hull BATTLE1=new Hull(250, SpaceOfChaos.INSTANCE.battleHull3Texture,450,"Battle hull 1",60000);
    private static final Hull BATTLE2=new Hull(250, SpaceOfChaos.INSTANCE.battleHull2,300,"Battle hull 2",40000);
    private static final Hull PIRATE1=new Hull(150, SpaceOfChaos.INSTANCE.pirateHull1, 160,"Pirate hull 1",40000);
    private static final Hull PIRATE2=new Hull(120, SpaceOfChaos.INSTANCE.pirateHull2Texture, 140,"Pirate hull 2",35000);
    private static final Hull PIRATE3=new Hull(180, SpaceOfChaos.INSTANCE.pirateHull3Texture, 190,"Pirate hull 3",54000);
    public static ArrayList<Hull> battleHulls=new ArrayList<>();
    public static ArrayList<Hull> tradingHulls=new ArrayList<>();
    public static ArrayList<Hull> pirateHulls=new ArrayList<>();
    public static ArrayList<Hull> playerHulls=new ArrayList<>();
    static {
        battleHulls.add(BATTLE1);
        battleHulls.add(BATTLE2);
        battleHulls.add(BATTLE3);

        tradingHulls.add(TRADING1);
        tradingHulls.add(TRADING2);
        tradingHulls.add(TRADING3);

        pirateHulls.add(PIRATE1);
        pirateHulls.add(PIRATE2);
        pirateHulls.add(PIRATE3);

        playerHulls.add(HORNET);
        playerHulls.add(BEETLE);
        playerHulls.add(BUMBLEBEE);
        playerHulls.add(DRAGONFLY);
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

    public static void initialize()
    {
        System.out.println("Hulls initialized");
    }
}
