package dev.buildtool;

import java.util.HashMap;
import java.util.Map;

public class Container implements SaveData {
    public Stack stack;
    public float x,y;
    public float rotation;

    public Container() {
    }

    public Container(Stack stack, float x, float y, float rotation) {
        this.stack = stack;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }


    @Override
    public Map<String, Object> getData() {
        HashMap<String,Object> data=new HashMap<>();
        data.put("x",x);
        data.put("y",y);
        data.put("rotation",rotation);
        data.put("stack",stack.getData());
        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        x= (float)(double) data.get("x");
        y= (float)(double) data.get("y");
        rotation= (float)(double) data.get("rotation");
        stack=new Stack();
        stack.load((Map<String, Object>) data.get("stack"));
    }
}
