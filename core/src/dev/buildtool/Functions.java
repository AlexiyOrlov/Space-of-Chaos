package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Functions {
    public static void drawScaled(SpriteBatch spriteBatch, Texture texture,float scale,float x,float y)
    {
        spriteBatch.draw(new TextureRegion(texture),x,y,0,0,texture.getWidth(),texture.getHeight(),scale,scale,0);
    }

    public static void drawScaled(SpriteBatch spriteBatch,Texture texture,float scale,float x,float y,float rotationDegrees)
    {
        spriteBatch.draw(new TextureRegion(texture),x,y,0,0,texture.getWidth(),texture.getHeight(),scale,scale, MathUtils.degreesToRadians*rotationDegrees);
    }

    public static void drawRotated(SpriteBatch spriteBatch,Texture texture,float x,float y,float rotationDegrees)
    {
        spriteBatch.draw(new TextureRegion(texture),x-texture.getWidth()/2,y-texture.getHeight()/2, texture.getWidth() /2,texture.getHeight()/2,texture.getWidth(),texture.getHeight(),1,1,rotationDegrees);
    }

    public static float rotateTowards(float fromAngle,float xFrom,float yFrom,float xTo,float yTo,float correctionAngle,float rotationSpeed)
    {
        return MathUtils.lerpAngle(fromAngle,MathUtils.atan2(yTo-yFrom,xTo-xFrom)+correctionAngle,rotationSpeed);
    }
}
