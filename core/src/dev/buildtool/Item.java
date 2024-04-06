package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

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
}
