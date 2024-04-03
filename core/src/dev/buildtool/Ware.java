package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.HashMap;

public class Ware extends Item {
    public static ArrayList<Ware> WARES=new ArrayList<>();
    public static HashMap<Ware,Integer> BASE_PRICES =new HashMap<>();
    public static Ware MEDICATIONS=new Ware("medications",900,true,"Medications",SpaceGame.INSTANCE.medicineTexture);
    public static Ware FOOD=new Ware("food",900,false,"Food",SpaceGame.INSTANCE.foodTexture);
    public static Ware FURNITURE=new Ware("furniture",450,false,"Furniture",SpaceGame.INSTANCE.furnitureTexture);
    public static Ware WATER=new Ware("water",900,false,"Water",SpaceGame.INSTANCE.waterTexture);
    public static Ware ELECTRONICS=new Ware("electronics",700,false,"Electronics",SpaceGame.INSTANCE.electronicsTexture);
    public static Ware CLOTHES=new Ware("clothes",800,false,"Clothes",SpaceGame.INSTANCE.clothesTexture);
    public static Ware JEWELLERY=new Ware("jewellery",900,false,"Jewellery",SpaceGame.INSTANCE.jewelleryTexture);
    public static Ware FIREARMS=new Ware("firearms",600,true,"Firearms",SpaceGame.INSTANCE.firearmsTexture);
    public static Ware CAR_PARTS=new Ware("car_parts",300,false,"Car parts",SpaceGame.INSTANCE.carPartsTexture);
    public static Ware TOOLS=new Ware("tools",600,false,"Tools",SpaceGame.INSTANCE.toolsTexture);
    public static Ware ALCOHOL=new Ware("alcohol",900,true,"Alcohol",SpaceGame.INSTANCE.alcoholTexture);
    public static Ware IRON_ORE=new Ware("iron ore",650,false,"Iron ore",SpaceGame.INSTANCE.ironOreTexture);
    public static Ware COPPER_ORE=new Ware("copper ore",650,false,"Copper ore",SpaceGame.INSTANCE.copperOreTexture);
    public static final int MAXIMUM_WARE_AMOUNT=1000;
    public static final HashMap<Ware,Float> MANUFACTURING_SPEED=new HashMap<>();
    public boolean needsLicense;
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

    public Ware(String type, int maxSize, boolean needsLicenseToTrade, String name, Texture texture) {
        super(type, maxSize,name,texture);
        needsLicense=needsLicenseToTrade;
    }
}
