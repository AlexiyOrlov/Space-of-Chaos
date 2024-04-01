package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

public class Item implements Comparable<Item>{
    public String type,name;
    public final int maxSize;
    public Texture texture;
    public Item(String type, int maxSize,String name,Texture texture) {
        this.type = type;
        this.maxSize = maxSize;
        this.name=name;
        this.texture=texture;
    }

    @Override
    public int compareTo(Item item) {
        return type.compareTo(item.type);
    }
}
