package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class StarShip {
    public float x,y;
    public float rotation,acceleration;
    private final Texture texture;
    private final TextureRegion textureRegion;
    public final Vector2 direction;
    public Engine engine=Engine.BASIC;
    public Hull hull=Hull.BASIC;
    public Weapon weapon=WeaponRegistry.GUN;
    public StarSystem currentStarSystem;
    public HashMap<Ware,Boolean> licences;
    private final Array<Projectile> projectiles;
    private float fireDelay;
    public Inventory inventory;
    public int money=1000;
    public StarShip(float x, float y, float rotation,Texture texture,StarSystem currentStarSystem) {
        this.x = x;
        this.y = y;
        projectiles=new Array<>();
        this.rotation = rotation;
        this.texture=texture;
        textureRegion=new TextureRegion(texture);
        direction=new Vector2(0,0);
        inventory=new Inventory(40);
        this.currentStarSystem=currentStarSystem;
        licences=new HashMap<>();
        Ware.WARES.forEach(ware -> licences.put(ware,false));
    }

    public void draw(SpriteBatch spriteBatch)
    {
        spriteBatch.draw(textureRegion,x- (float) texture.getWidth() /2,y- (float) texture.getHeight() /2,  (float) texture.getWidth() /2, (float) texture.getHeight() /2,texture.getWidth(),texture.getHeight(),1,1,rotation);
        projectiles.forEach(projectile -> projectile.render(spriteBatch));
        float angleToStar=MathUtils.atan2(y,x);
        int halfScreenWidth=Gdx.graphics.getWidth()/2;
        int halfScreenHeight=Gdx.graphics.getHeight()/2;
        float absx=Math.abs(x);
        float absy=Math.abs(y);
        if(absx>halfScreenWidth && absy>halfScreenHeight)
        {
            float cx=1/MathUtils.tan(angleToStar)*MathUtils.tan(angleToStar);
        }
    }

    public void update(float deltaTime)
    {
        float rotationSpeed=engine.steering;
        if(Gdx.input.isKeyPressed(Input.Keys.A))
        {
            rotation+= MathUtils.degRad*rotationSpeed;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.D))
        {
            rotation-=MathUtils.degRad*rotationSpeed;
        }

        direction.set(Vector2.Y).rotateDeg(rotation);
        direction.scl(acceleration);
        x+=direction.x;
        y+=direction.y;

        if(Gdx.input.isKeyPressed(Input.Keys.S))
        {
            if(acceleration>-engine.maxSpeed)
                acceleration-=0.1f;
        }
        else if(acceleration<0)
        {
            acceleration+=0.03f;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.W))
        {
            if(acceleration< engine.maxSpeed)
                acceleration+=0.1f;
        }
        else if(acceleration>0)
        {
            acceleration-=0.03f;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.L))
        {
            for (Planet planet : currentStarSystem.planets) {
                if(planet.outline.contains(x,y))
                {
                    SpaceGame.INSTANCE.setScreen(new PlanetScreen(currentStarSystem, planet,this));
                    acceleration=0;
                    break;
                }
            }
        }

        if(Gdx.input.isTouched())
        {
            if(fireDelay<=0)
            {
                Projectile[] projectiles=weapon.shoot(this);
                this.projectiles.addAll(projectiles);
                fireDelay= weapon.fireDelay;
            }
        }
        if(fireDelay>0)
        {
            fireDelay-=deltaTime;
        }
        Array<Projectile> toRemove=new Array<>(projectiles.size);
        projectiles.forEach(projectile -> {
            projectile.update();
            if(Vector2.dst(projectile.x,projectile.y,0,0)>10000)
            {
                toRemove.add(projectile);
            }
        });
        projectiles.removeAll(toRemove,false);

        if(Gdx.input.isKeyJustPressed(Input.Keys.M))
        {
            SpaceGame.INSTANCE.setScreen(new StarMap(currentStarSystem,this));
        }
    }

    public void addItem(Stack stack)
    {
        inventory.addItem(stack);
    }
}
