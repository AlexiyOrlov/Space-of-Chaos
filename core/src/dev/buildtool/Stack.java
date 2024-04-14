package dev.buildtool;

import java.util.HashMap;
import java.util.Map;

public class Stack implements SaveData{
    public Item item;
    public int count;

    public Stack()
    {

    }
    public Stack(Item item, int count) {
        this.item = item;
        this.count = Math.min(count,item.maxSize);
    }

    @Override
    public Map<String, Object> getData() {
        HashMap<String,Object> data=new HashMap<>();
        data.put("item",item.name);
        data.put("count",count);
        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        String item= (String) data.get("item");
        this.item=Item.REGISTRY.get(item);
        count= (int) data.get("count");
    }
}
