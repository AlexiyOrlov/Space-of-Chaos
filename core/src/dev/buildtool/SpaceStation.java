package dev.buildtool;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpaceStation implements SaveData{
    public float x,y;
    public float angle;
    public int distanceToStar;
    public boolean clockwise;
    public Circle area;
    public float rotation;
    public Inventory equipmentInventory=new Inventory(9);

    public SpaceStation(float angle, int distanceToStar) {
        this.angle = angle;
        this.distanceToStar = distanceToStar;
        clockwise=SpaceOfChaos.random.nextBoolean();
        area=new Circle();
        ArrayList<Item> items=new ArrayList<>(Item.equipment);
        for (int i = 0; i < 9; i++) {
            if(SpaceOfChaos.random.nextInt(100)<25)
            {
                Item randomItem= items.get(SpaceOfChaos.random.nextInt(items.size()));
                equipmentInventory.addItem(new Stack(randomItem,1));
                items.remove(randomItem);
            }
        }
    }

    public void update()
    {
        this.x = distanceToStar * MathUtils.cos(angle);
        this.y = distanceToStar *MathUtils.sin(angle);
        if(!clockwise)
            angle +=0.025f*MathUtils.degreesToRadians;
        else
            angle -=0.025f*MathUtils.degreesToRadians;
        area.set(x,y,128);
        rotation-= 10*MathUtils.degreesToRadians;
    }

    public void render(SpriteBatch spriteBatch)
    {
        spriteBatch.begin();
        Functions.drawRotated(spriteBatch, SpaceOfChaos.INSTANCE.spaceStation,x,y,rotation);
        spriteBatch.end();
        if(SpaceOfChaos.debugDraw) {
            SpaceOfChaos.INSTANCE.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            SpaceOfChaos.INSTANCE.shapeRenderer.circle(x,y,128);
            SpaceOfChaos.INSTANCE.shapeRenderer.end();
        }
    }
    @Override
    public Map<String, Object> getData() {
        HashMap<String,Object> data=new HashMap<>();
        data.put("x",x);
        data.put("y",y);
        data.put("rotation", angle);
        data.put("distance",distanceToStar);
        data.put("clockwise",clockwise);
        data.put("shop",equipmentInventory.getData());
        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        x= (float)(double) data.get("x");
        y=(float) (double)data.get("y");
        angle = (float)(double) data.get("rotation");
        distanceToStar= (int) data.get("distance");
        clockwise= (boolean) data.get("clockwise");
        equipmentInventory.load((Map<String, Object>) data.get("shop"));
    }
}
