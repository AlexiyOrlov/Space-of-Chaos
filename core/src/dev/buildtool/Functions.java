package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

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
        spriteBatch.draw(new TextureRegion(texture),x,y, texture.getWidth() /2,texture.getHeight()/2,texture.getWidth(),texture.getHeight(),1,1,rotationDegrees*MathUtils.degreesToRadians);
    }
}
