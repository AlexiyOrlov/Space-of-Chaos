package dev.buildtool;

import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.HashMap;

public class Resource {
    public final int chanceToOccur;
    public final String name;
    public int id;
    public static final HashMap<Integer,Resource> ids=new HashMap<>();
    public static final Resource IRON_ORE=new Resource(30,"Iron ore");
    public static final Resource COPPER_ORE =new Resource(14,"Copper ore");
    public static ArrayList<Resource> RESOURCES=new ArrayList<>();
    private static int nextId;

    static {
        RESOURCES.add(IRON_ORE);
        RESOURCES.add(COPPER_ORE);
    }

    public Resource(int chanceToOccur,String name) {
        this.chanceToOccur = chanceToOccur;
        this.name=name;
        id=nextId++;
        ids.put(id,this);
    }
}
