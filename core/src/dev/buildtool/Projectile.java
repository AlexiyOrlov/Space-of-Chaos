package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    public Texture texture;
    public int damage;
    public float rotation;
    private final TextureRegion textureRegion;
    Circle area;

    public Projectile(Texture texture, int damage, float x, float y, float rotation, int speed) {
        this.texture = texture;
        this.damage = damage;
        this.speed = new Vector2(MathUtils.cos((rotation +90)*MathUtils.degreesToRadians)*speed,MathUtils.sin((rotation +90)*MathUtils.degreesToRadians)*speed);
        this.x = x;
        this.y = y;
        this.rotation=rotation;
        textureRegion=new TextureRegion(texture);
        area=new Circle(x,y,texture.getWidth()/2);
    }

    public Vector2 speed;
    public float x,y;
    public void update()
    {
        x+=speed.x;
        y+=speed.y;
        area.set(x,y,texture.getWidth()/2);
    }

    public void render(SpriteBatch spriteBatch)
    {
        spriteBatch.begin();
        spriteBatch.draw(textureRegion,x-texture.getWidth()/2,y-texture.getHeight()/2, 0, 0,texture.getWidth(),texture.getHeight(),1,1,rotation);
        spriteBatch.end();
    }
}
