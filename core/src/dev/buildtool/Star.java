package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Star {
    private final Texture texture;

    public Star(Texture texture) {
        this.texture = texture;
    }

    public void draw(SpriteBatch spriteBatch)
    {
        spriteBatch.draw(texture,0,0);
    }
}
