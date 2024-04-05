package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

public class Hull {
    public static final Hull BASIC=new Hull(300, SpaceGame.INSTANCE.redStarshipTexture );
    public static final Hull TRADING1=new Hull(600, SpaceGame.INSTANCE.tradingHull1Texture);
    public static final Hull BATTLE2=new Hull(450,SpaceGame.INSTANCE.battleHull2);
    public int capacity;
    public Texture look;

    public Hull(int capacity, Texture texture) {
        this.capacity = capacity;
        look=texture;
    }
}
