package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.HashMap;

public class Ware extends Item {
    public static ArrayList<Ware> WARES=new ArrayList<>();
    public static HashMap<Ware,Integer> BASE_PRICES =new HashMap<>();
    public static Ware MEDICATIONS=new Ware(900,true,"Medications", SpaceOfChaos.INSTANCE.medicineTexture,250000);
    public static Ware FOOD=new Ware(900,false,"Food", SpaceOfChaos.INSTANCE.foodTexture);
    public static Ware FURNITURE=new Ware(450,false,"Furniture", SpaceOfChaos.INSTANCE.furnitureTexture);
    public static Ware WATER=new Ware(900,false,"Water", SpaceOfChaos.INSTANCE.waterTexture);
    public static Ware ELECTRONICS=new Ware(700,false,"Electronics", SpaceOfChaos.INSTANCE.electronicsTexture);
    public static Ware CLOTHES=new Ware(800,false,"Clothes", SpaceOfChaos.INSTANCE.clothesTexture);
    public static Ware JEWELLERY=new Ware(900,false,"Jewellery", SpaceOfChaos.INSTANCE.jewelleryTexture);
    public static Ware FIREARMS=new Ware(600,true,"Firearms", SpaceOfChaos.INSTANCE.firearmsTexture,400000);
    public static Ware CAR_PARTS=new Ware(300,false,"Car parts", SpaceOfChaos.INSTANCE.carPartsTexture);
    public static Ware TOOLS=new Ware(600,false,"Tools", SpaceOfChaos.INSTANCE.toolsTexture,100000);
    public static Ware ALCOHOL=new Ware(900,true,"Alcohol", SpaceOfChaos.INSTANCE.alcoholTexture);
    public static Ware IRON_ORE=new Ware(650,false,"Iron ore", SpaceOfChaos.INSTANCE.ironOreTexture);
    public static Ware COPPER_ORE=new Ware(650,false,"Copper ore", SpaceOfChaos.INSTANCE.copperOreTexture);
    public static final int MAXIMUM_WARE_AMOUNT=1000;
    public static final HashMap<Ware,Float> MANUFACTURING_SPEED=new HashMap<>();
    public boolean needsLicense;
    public int licenseCost;

    static {
        WARES.add(TOOLS);
        WARES.add(CAR_PARTS);
        WARES.add(FIREARMS);
        WARES.add(JEWELLERY);
        WARES.add(CLOTHES);
        WARES.add(ELECTRONICS);
        WARES.add(WATER);
        WARES.add(FURNITURE);
        WARES.add(FOOD);
        WARES.add(MEDICATIONS);
        WARES.add(ALCOHOL);

        BASE_PRICES.put(FOOD,100);
        BASE_PRICES.put(WATER,200);
        BASE_PRICES.put(MEDICATIONS,300);
        BASE_PRICES.put(ALCOHOL,400);
        BASE_PRICES.put(CLOTHES,500);
        BASE_PRICES.put(FURNITURE,600);
        BASE_PRICES.put(TOOLS,700);
        BASE_PRICES.put(JEWELLERY,800);
        BASE_PRICES.put(ELECTRONICS,900);
        BASE_PRICES.put(CAR_PARTS,1000);
        BASE_PRICES.put(FIREARMS,1100);

        BASE_PRICES.forEach((ware, integer) -> {
            MANUFACTURING_SPEED.put(ware, (1 / (float) integer));
        });


    }

    public Ware(int maxSize, boolean needsLicenseToTrade, String name, Texture texture) {
        super(maxSize,name,texture, -1);
        needsLicense=needsLicenseToTrade;
    }

    public Ware(int maxSize, boolean needsLicense,String name, Texture texture,  int licenseCost) {
        this(maxSize, needsLicense, name, texture);
        this.needsLicense = needsLicense;
        this.licenseCost = licenseCost;
    }
}
