package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;

public class Hull {
    public static final Hull BASIC=new Hull(300, SpaceGame.INSTANCE.redStarshipTexture ,100);
    public static final Hull TRADING1=new Hull(600, SpaceGame.INSTANCE.tradingHull1Texture,200);
    public static final Hull BATTLE3=new Hull(450,SpaceGame.INSTANCE.battleHull3, 400);
    public static final Hull BATTLE2=new Hull(450,SpaceGame.INSTANCE.battleHull2,300);
    public static final Hull PIRATE1=new Hull(300,SpaceGame.INSTANCE.pirateHull1, 160);
    public static ArrayList<Hull> battleHulls=new ArrayList<>();
    static {
        battleHulls.add(BATTLE2);
        battleHulls.add(BATTLE3);
    }
    public int capacity;
    public Texture look;

    public int integrity;

    public Hull(int capacity, Texture texture,int integrity) {
        this.capacity = capacity;
        look=texture;
        this.integrity=integrity;
    }
}
