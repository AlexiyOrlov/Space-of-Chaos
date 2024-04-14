package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

import java.util.HashMap;
import java.util.Map;

public class StarGate implements SaveData{
    public float x,y;
    public float rotation;
    public int distanceFromStar;
    public float currentAngle;
    public Circle area=new Circle();
    private boolean clockwise;

    public StarGate() {
    }

    public StarGate(int distanceFromStar, float currentAngle) {
        this.distanceFromStar = distanceFromStar;
        this.currentAngle = currentAngle;
        clockwise= SpaceOfChaos.random.nextBoolean();
    }

    public void draw(SpriteBatch spriteBatch)
    {
        spriteBatch.begin();
        Texture starGateTexture = SpaceOfChaos.INSTANCE.starGateTexture;
        Functions.drawRotated(spriteBatch, starGateTexture,x,y,rotation);
        spriteBatch.end();
        if(SpaceOfChaos.debugDraw) {
            SpaceOfChaos.INSTANCE.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            SpaceOfChaos.INSTANCE.shapeRenderer.circle(x,y,128);
            SpaceOfChaos.INSTANCE.shapeRenderer.end();
        }
    }

    public void update(float deltaTime)
    {
        this.x = distanceFromStar * MathUtils.cos(currentAngle);
        this.y = distanceFromStar *MathUtils.sin(currentAngle);
        rotation-= 10*MathUtils.degreesToRadians;
        if(!clockwise)
            currentAngle+=0.025f*MathUtils.degreesToRadians;
        else
            currentAngle-=0.025f*MathUtils.degreesToRadians;
        area.set(x,y,128);
    }

    @Override
    public Map<String, Object> getData() {
        HashMap<String,Object> data=new HashMap<>();
        data.put("area x",area.x);
        data.put("area y",area.y);
        data.put("clockwise",clockwise);
        data.put("angle",currentAngle);
        data.put("distance to star",distanceFromStar);
        data.put("rotation",rotation);
        data.put("x",x);
        data.put("y",y);
        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        area.x= (float)(double) data.get("x");
        area.y= (float)(double) data.get("y");
        clockwise= (boolean) data.get("clockwise");
        currentAngle= (float)(double) data.get("angle");
        distanceFromStar= (int) data.get("distance to star");
        rotation= (float)(double) data.get("rotation");
        x= (float)(double) data.get("x");
        y= (float)(double) data.get("y");
    }
}
