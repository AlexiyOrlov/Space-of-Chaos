package dev.buildtool;

import java.util.HashMap;
import java.util.Map;

public class WarePurchase implements SaveData {
     Ware ware;
     int amountBought;
     int pricePerUnit;
     int moneySpent;

    public WarePurchase() {
    }

    public WarePurchase(Ware ware, int amountBought, int pricePerUnit, int moneySpent) {
        this.ware = ware;
        this.amountBought = amountBought;
        this.pricePerUnit = pricePerUnit;
        this.moneySpent = moneySpent;
    }

    @Override
    public Map<String, Object> getData() {
        HashMap<String,Object> data=new HashMap<>();
        data.put("ware",ware.name);
        data.put("amount bought",amountBought);
        data.put("money spent",moneySpent);
        data.put("price per unit",pricePerUnit);
        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        ware= (Ware) Item.REGISTRY.get((String)data.get("ware"));
        amountBought= (int) data.get("amount bought");
        moneySpent= (int) data.get("money spent");
        pricePerUnit= (int) data.get("price per unit");
    }
}
