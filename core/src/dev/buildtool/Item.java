package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.List;

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
