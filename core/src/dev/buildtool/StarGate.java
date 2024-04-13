package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

public class StarGate {
    public float x,y;
    public float rotation;
    public int distanceFromStar;
    public float currentAngle;
    public Circle area;
    private boolean clockwise;
    public StarGate(int distanceFromStar, float currentAngle) {
        this.distanceFromStar = distanceFromStar;
        this.currentAngle = currentAngle;
        area=new Circle();
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
}
