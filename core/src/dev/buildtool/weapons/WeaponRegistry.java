package dev.buildtool.weapons;

import java.util.ArrayList;
import java.util.TreeMap;

import dev.buildtool.SpaceOfChaos;

public class WeaponRegistry {
    public static final TreeMap<String, Weapon> WEAPONS=new TreeMap<>();
    public static final Weapon GUN=new Gun(10,1,20, SpaceOfChaos.INSTANCE.basicProjectile, "Basic gun", SpaceOfChaos.INSTANCE.laserShotSound);
    public static final Weapon SHOTGUN=new Shotgun(5,2,30, SpaceOfChaos.INSTANCE.pelletTexture, "Shotgun",75000, SpaceOfChaos.INSTANCE.shotGunSound);
    public static final Weapon MACHINE_GUN=new MachineGun(5,0.25f,15, SpaceOfChaos.INSTANCE.pelletTexture,"Machine gun", SpaceOfChaos.INSTANCE.machineGunTexture, 100000, SpaceOfChaos.INSTANCE.shot2sound);
    public static final Weapon AI_GUN1=new Gun(15,1,20, SpaceOfChaos.INSTANCE.basicProjectile, "Basic AI gun",SpaceOfChaos.INSTANCE.aiGun);
    public static final Weapon CLUSTER_GUN=new ClusterGun(20,1.5f,20, SpaceOfChaos.INSTANCE.redProjectileTexture, "Cluster gun", SpaceOfChaos.INSTANCE.clusterGunTexture, 310000, SpaceOfChaos.INSTANCE.blasterSound);
    public static final Weapon MISSILE_LAUNCHER=new MissileLauncher(50,3,10,SpaceOfChaos.INSTANCE.missileTexture,"Missile launcher",SpaceOfChaos.INSTANCE.missileLauncherTexture, 200000,SpaceOfChaos.INSTANCE.missileSound);
    public static final Weapon GATLING_GUN=new MachineGun(4,0.1f,15,SpaceOfChaos.INSTANCE.pelletTexture, "Gatling gun",SpaceOfChaos.INSTANCE.gatlingGunTexture, 300000,SpaceOfChaos.INSTANCE.machineGunSound);
    public static final Weapon TRISHOT=new TriShot(14,2,20,SpaceOfChaos.INSTANCE.redProjectileTexture, "Trishot",SpaceOfChaos.INSTANCE.triShotTexture, 80000,SpaceOfChaos.INSTANCE.drrrSound);
    public static final ArrayList<Weapon> TIER1_WEAPONS=new ArrayList<>();
    public static final ArrayList<Weapon> TIER2_WEAPONS=new ArrayList<>();
    public static final ArrayList<Weapon> TIER3_WEAPONS=new ArrayList<>();
    static {
        WEAPONS.put("basic gun",GUN);
        TIER1_WEAPONS.add(GUN);
        TIER1_WEAPONS.add(AI_GUN1);

        TIER2_WEAPONS.add(SHOTGUN);
//        TIER2_WEAPONS.add(CLUSTER_GUN);
        TIER2_WEAPONS.add(MACHINE_GUN);

        TIER3_WEAPONS.add(TRISHOT);
        TIER3_WEAPONS.add(GATLING_GUN);
        TIER3_WEAPONS.add(MISSILE_LAUNCHER);
    }

    public static void initialize()
    {
        System.out.println("Weapon registry initialized");
    }

    public static Weapon getRandomTier1Weapon()
    {
        return TIER1_WEAPONS.get(SpaceOfChaos.random.nextInt(TIER1_WEAPONS.size()));
    }

    public static Weapon getRandomTier2Weapon()
    {
        return TIER2_WEAPONS.get(SpaceOfChaos.random.nextInt(TIER2_WEAPONS.size()));
    }

    public static Weapon getRandomTier3Weapon()
    {
        return TIER3_WEAPONS.get(SpaceOfChaos.random.nextInt(TIER3_WEAPONS.size()));
    }
}
