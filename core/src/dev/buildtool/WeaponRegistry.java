package dev.buildtool;

import java.util.TreeMap;

import dev.buildtool.weapons.Gun;
import dev.buildtool.weapons.Shotgun;
import dev.buildtool.weapons.Weapon;

public class WeaponRegistry {
    public static final TreeMap<String, Weapon> WEAPONS=new TreeMap<>();
    public static final Weapon GUN=new Gun(10,1,10,SpaceGame.INSTANCE.basicProjectile, "Basic gun");
    public static final Weapon SHOTGUN=new Shotgun(5,2,10,SpaceGame.INSTANCE.pelletTexture, "Shotgun");

    public static final Weapon AI_GUN1=new Gun(10,1,20,SpaceGame.INSTANCE.basicProjectile, "Basic AI gun");
    static {
        WEAPONS.put("basic gun",GUN);
    }
}
