package dev.buildtool;

import java.util.TreeMap;

public class WeaponRegistry {
    public static final TreeMap<String,Weapon> WEAPONS=new TreeMap<>();
    public static final Weapon GUN=new Gun(10,1,10,SpaceGame.INSTANCE.basicProjectile, "Basic gun");
    public static final Weapon SHOTGUN=new Gun(10,2,10,SpaceGame.INSTANCE.pelletTexture, "Shotgun");

    static {
        WEAPONS.put("basic gun",GUN);
    }
}
