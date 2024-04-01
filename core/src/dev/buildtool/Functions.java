package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Functions {
    public static void drawScaled(SpriteBatch spriteBatch, Texture texture,float scale,float x,float y)
    {
        spriteBatch.draw(new TextureRegion(texture),x,y,0,0,texture.getWidth(),texture.getHeight(),scale,scale,0);
    }
}
