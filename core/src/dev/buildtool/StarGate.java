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

    public StarGate(int distanceFromStar, float currentAngle) {
        this.distanceFromStar = distanceFromStar;
        this.currentAngle = currentAngle;
        area=new Circle();
    }

    public void draw(SpriteBatch spriteBatch)
    {
        spriteBatch.begin();
        Texture starGateTexture = SpaceGame.INSTANCE.starGateTexture;
        Functions.drawRotated(spriteBatch, starGateTexture,x-starGateTexture.getWidth()/2,y-starGateTexture.getHeight()/2,rotation);
        spriteBatch.end();
        if(SpaceGame.debugDraw) {
            SpaceGame.INSTANCE.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            SpaceGame.INSTANCE.shapeRenderer.circle(x,y,128);
            SpaceGame.INSTANCE.shapeRenderer.end();
        }

    }

    public void update(float deltaTime)
    {
        this.x = (float) (distanceFromStar * MathUtils.cos(currentAngle));
        this.y = (float) (distanceFromStar *MathUtils.sin(currentAngle));
        rotation-= 10;
        currentAngle+=0.1f*MathUtils.degreesToRadians;
        area.set(x,y,128);
    }
}
