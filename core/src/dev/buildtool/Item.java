package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

import java.util.List;

public class Item implements Comparable<Item>{
    public String type,name;
    public final int maxSize;
    public Texture texture;
    public int basePrice;
    public Item(String type, int maxSize, String name, Texture texture, int basePrice) {
        this.type = type;
        this.maxSize = maxSize;
        this.name=name;
        this.texture=texture;
        this.basePrice=basePrice;
    }

    @Override
    public int compareTo(Item item) {
        return type.compareTo(item.type);
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
