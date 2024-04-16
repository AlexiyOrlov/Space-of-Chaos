package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.buildtool.weapons.Weapon;
import dev.buildtool.weapons.WeaponRegistry;

public class Item implements Comparable<Item>{
    public static HashMap<String,Item> REGISTRY=new HashMap<>();
    public static final Item TARGET_RADAR=new Item(1,"Target scanner",SpaceOfChaos.INSTANCE.targetRadar, 110000){
        @Override
        public List<String> getTooltip() {
            List<String> tooltip=super.getTooltip();
            tooltip.add("Shows integrity of selected target");
            return tooltip;
        }
    };

    public static final Item ALL_TARGET_RADAR=new Item(1,"Batch target scanner",SpaceOfChaos.INSTANCE.allTargetRadar, 550000){
        @Override
        public List<String> getTooltip() {
            List<String> tooltip=super.getTooltip();
            tooltip.add("Shows integrity of all ships");
            return tooltip;
        }
    };

    public static final Item SHIP_RADAR=new Item(1,"Ship radar",SpaceOfChaos.INSTANCE.shipDetector,300000){
        @Override
        public List<String> getTooltip() {
            List<String> tooltip=super.getTooltip();
            tooltip.add("Shows ship locations on HUD");
            return tooltip;
        }
    };

    public static final Item PRICE_SCANNER=new Item(1,"Price scanner",SpaceOfChaos.INSTANCE.priceScanner, 280000){
        @Override
        public List<String> getTooltip() {
            List<String> tooltip=super.getTooltip();
            tooltip.add("Shows which ware prices are below average");
            return tooltip;
        }
    };

    public static ArrayList<Item> equipment=new ArrayList<>();

    static {
        Hull.initialize();
        SideThrusters.initialize();
        Engine.initialize();
        Weapon.initialize();
        WeaponRegistry.initialize();

        equipment.add(ALL_TARGET_RADAR);
        equipment.add(PRICE_SCANNER);
        equipment.add(SHIP_RADAR);
        equipment.add(ExplorationDrone.MARK1);
        equipment.add(Engine.MARK2);
        equipment.add(Engine.ENGINE_3);
        equipment.add(Hull.BUMBLEBEE);
        equipment.add(Hull.HORNET);
        equipment.add(Hull.DRAGONFLY);
        equipment.add(Hull.BEETLE);
        equipment.add(WeaponRegistry.TRISHOT);
        equipment.add(WeaponRegistry.SHOTGUN);
        equipment.add(WeaponRegistry.MACHINE_GUN);
        equipment.add(WeaponRegistry.CLUSTER_GUN);
        equipment.add(WeaponRegistry.GATLING_GUN);
        equipment.add(WeaponRegistry.MISSILE_LAUNCHER);
        equipment.add(SideThrusters.MARK2);
        equipment.add(SideThrusters.MARK3);
    }


    public String name;
    public final int maxSize;
    public Texture texture;
    public int basePrice;
    public Item(int maxSize, String name, Texture texture, int basePrice) {
        this.maxSize = maxSize;
        this.name=name;
        this.texture=texture;
        this.basePrice=basePrice;
        Item present=REGISTRY.get(name);
        if(present!=null)
        {
            throw new RuntimeException("Duplicate item: "+name);
        }
        REGISTRY.put(name,this);
    }

    @Override
    public int compareTo(Item item) {
        return name.compareTo(item.name);
    }

    public List<String> getTooltip()
    {
        List<String> info= new java.util.ArrayList<>(List.of(name));
        if(basePrice>0)
        {
            info.add("Price: "+basePrice);
        }
        return info;
    }
}
