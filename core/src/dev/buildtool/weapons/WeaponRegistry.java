package dev.buildtool.weapons;

import java.util.TreeMap;

import dev.buildtool.SpaceGame;
import dev.buildtool.weapons.Gun;
import dev.buildtool.weapons.Shotgun;
import dev.buildtool.weapons.Weapon;

public class WeaponRegistry {
    public static final TreeMap<String, Weapon> WEAPONS=new TreeMap<>();
    public static final Weapon GUN=new Gun(10,1,15, SpaceGame.INSTANCE.basicProjectile, "Basic gun",SpaceGame.INSTANCE.laserShotSound);
    public static final Weapon SHOTGUN=new Shotgun(5,2,30,SpaceGame.INSTANCE.pelletTexture, "Shotgun",75000,SpaceGame.INSTANCE.shotGunSound);
    public static final Weapon MACHINE_GUN=new MachineGun(5,0.25f,15,SpaceGame.INSTANCE.pelletTexture,"Machine gun",SpaceGame.INSTANCE.machineGunTexture, 100000,SpaceGame.INSTANCE.machineGunSound);
    public static final Weapon AI_GUN1=new Gun(15,1,20,SpaceGame.INSTANCE.basicProjectile, "Basic AI gun",null);
    static {
        WEAPONS.put("basic gun",GUN);
    }
}
