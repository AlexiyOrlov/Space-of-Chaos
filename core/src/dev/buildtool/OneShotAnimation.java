package dev.buildtool;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class OneShotAnimation {
    public float time;
    private final Animation<TextureRegion> animation;
    private final float x;
    private final float y;

    public OneShotAnimation(Animation<TextureRegion> animation, float x, float y) {
        this.animation = animation;
        this.x = x;
        this.y = y;
    }

    public void update(float deltaTime, SpriteBatch spriteBatch)
    {
        if(time<1)
        {
            TextureRegion textureRegion=animation.getKeyFrame(time);
            spriteBatch.begin();
            spriteBatch.draw(textureRegion,x-textureRegion.getRegionWidth()/2,y-textureRegion.getRegionHeight()/2);
            spriteBatch.end();
            time+=deltaTime;
        }
    }
}
