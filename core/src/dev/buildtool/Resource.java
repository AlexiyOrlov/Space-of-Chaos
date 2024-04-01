package dev.buildtool;

import java.util.ArrayList;

public class Resource {
    public final int chanceToOccur;
    public final String name;

    public static final Resource IRON_ORE=new Resource(30,"Iron ore");
    public static final Resource COPPER_ORE =new Resource(14,"Copper ore");
    public static ArrayList<Resource> RESOURCES=new ArrayList<>();

    static {
        RESOURCES.add(IRON_ORE);
        RESOURCES.add(COPPER_ORE);
    }

    public Resource(int chanceToOccur,String name) {
        this.chanceToOccur = chanceToOccur;
        this.name=name;
    }
}
