package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

import dev.buildtool.weapons.Weapon;
import dev.buildtool.weapons.WeaponRegistry;

public class PlayerShip implements Ship{
    public float x,y;
    public float rotation,acceleration,leftAcceleration,rightAcceleration;
    private final Texture texture;
    private final TextureRegion textureRegion;
    public final Vector2 direction;
    public StarSystem currentStarSystem;
    public HashMap<Ware,Boolean> licences;
    private float fireDelay;
    public Inventory inventory;
    public final Circle area;
    public int money=1000;
    public Deque<WarePurchase> warePurchases=new ArrayDeque<>();
    public int integrity;
    private Inventory shipParts=new Inventory(4);
    public PlayerShip(float x, float y, float rotation, Texture texture, StarSystem currentStarSystem) {
        this.x = x;
        this.y = y;
        setHull(new Stack(Hull.BASIC,1));
        setWeapon(new Stack(WeaponRegistry.GUN,1));
        setEngine(new Stack(Engine.BASIC,1));
        setThrusters(new Stack(SideThrusters.BASIC,1));
        this.rotation = rotation;
        this.texture=texture;
        textureRegion=new TextureRegion(texture);
        direction=new Vector2(0,0);
        inventory=new Inventory(40);
        this.currentStarSystem=currentStarSystem;
        licences=new HashMap<>();
        Ware.WARES.forEach(ware -> licences.put(ware,false));
        area=new Circle();
        integrity=getHull().integrity;
    }

    public void setWeapon(Stack weapon)
    {
        if(!(weapon.item instanceof Weapon))
            throw new RuntimeException("Must be a weapon");
        shipParts.stacks[1]=new Stack(weapon.item,1);
    }

    public Weapon getWeapon()
    {
        return (Weapon) shipParts.stacks[1].item;
    }

    public void setHull(Stack hull)
    {
        if(!(hull.item instanceof Hull))
            throw new RuntimeException("Must be a hull");
        shipParts.stacks[0]=new Stack(hull.item,1);
    }

    public Hull getHull()
    {
        return (Hull) shipParts.stacks[0].item;
    }

    public Inventory getShipParts() {
        return shipParts;
    }

    public void setEngine(Stack engine)
    {
        if(!(engine.item instanceof Engine))
            throw new RuntimeException("Must be an engine");
        shipParts.stacks[2]=new Stack(engine.item,1);
    }

    public Engine getEngine()
    {
        return (Engine) shipParts.stacks[2].item;
    }

    public void setThrusters(Stack thrusters)
    {
        if(!(thrusters.item instanceof SideThrusters))
            throw new RuntimeException("Must be side thrusters");
        shipParts.stacks[3]=new Stack(thrusters.item,1);
    }

    public SideThrusters getSideThrusters()
    {
        return (SideThrusters) shipParts.stacks[3].item;
    }
    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer)
    {
        spriteBatch.begin();
        spriteBatch.draw(textureRegion,x- (float) texture.getWidth() /2,y- (float) texture.getHeight() /2,  (float) texture.getWidth() /2, (float) texture.getHeight() /2,texture.getWidth(),texture.getHeight(),1,1,rotation);
        spriteBatch.end();

        if(SpaceGame.debugDraw) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.circle(area.x, area.y, area.radius);
            shapeRenderer.end();
        }

        BitmapFont font=SpaceGame.INSTANCE.bitmapFont;
        SpriteBatch uibatch=SpaceGame.INSTANCE.uiBatch;
        int backBufferWidth = Gdx.graphics.getBackBufferWidth();
        int backBufferHeight = Gdx.graphics.getBackBufferHeight();
        for (Planet planet : currentStarSystem.planets) {
            if(planet.outline.overlaps(area))
            {
                GlyphLayout glyphLayout=new GlyphLayout(font,"Press 'L' to land");
                uibatch.begin();
                font.draw(uibatch,"Press 'L' to land", (float) backBufferWidth /2- glyphLayout.width/2, (float) backBufferHeight /2-50);
                uibatch.end();
                break;
            }
        }

        if(currentStarSystem.starGate.area.overlaps(area))
        {
            GlyphLayout glyphLayout=new GlyphLayout(font,"Press 'M' to open star map");
            uibatch.begin();
            font.draw(uibatch,"Press 'M' to open star map",backBufferWidth/2- glyphLayout.width/2,backBufferHeight/2-50);
            uibatch.end();
        }
    }

    public void update(float deltaTime, Viewport viewport)
    {
        if(Gdx.input.isKeyPressed(Input.Keys.A))
        {
            if(leftAcceleration< getSideThrusters().strafingSpeed)
                leftAcceleration+=0.15f;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.D))
        {
            if(rightAcceleration<getSideThrusters().strafingSpeed)
                rightAcceleration+=0.15f;
        }
        //to the left
        this.x+=MathUtils.cosDeg(rotation+90+90)*leftAcceleration;
        this.y+=MathUtils.sinDeg(rotation+90+90)*leftAcceleration;

        //to the right
        this.x+=MathUtils.cosDeg(rotation+90-90)*rightAcceleration;
        this.y+=MathUtils.sinDeg(rotation+90-90)*rightAcceleration;

        if(leftAcceleration>0)
            leftAcceleration-=0.1f;
        if(rightAcceleration>0)
            rightAcceleration-=0.1f;

        direction.set(Vector2.Y).rotateDeg(rotation);
        direction.scl(acceleration);
        x+=direction.x;
        y+=direction.y;

        if(Gdx.input.isKeyPressed(Input.Keys.S))
        {
            if(acceleration>-getEngine().maxSpeed)
                acceleration-=0.1f;
        }
        else if(acceleration<0)
        {
            acceleration+=0.03f;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.W))
        {
            if(acceleration< getEngine().maxSpeed)
                acceleration+=0.1f;
        }
        else if(acceleration>0)
        {
            acceleration-=0.03f;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.L))
        {
            for (Planet planet : currentStarSystem.planets) {
                if(planet.outline.overlaps(area))
                {
                    if(planet.isInhabited)
                        SpaceGame.INSTANCE.setScreen(new PlanetScreen2(currentStarSystem,planet,this));
                    else
                        SpaceGame.INSTANCE.setScreen(new PlanetScreen(currentStarSystem, planet,this));
                    acceleration=0;
                    break;
                }
            }
        }
        if(Gdx.input.isTouched())
        {
            if(fireDelay<=0) {
                Projectile[] projectiles = getWeapon().shoot(x, y, rotation,this ,null);
                if (projectiles != null) {
                    currentStarSystem.projectiles.addAll(projectiles);
                    fireDelay = getWeapon().cooldown;
                }
            }
        }

        fireDelay-=deltaTime;

        if(Gdx.input.isKeyJustPressed(Input.Keys.M) && currentStarSystem.starGate.area.overlaps(area))
        {
            SpaceGame.INSTANCE.setScreen(new StarMap(currentStarSystem,this));
            acceleration=0;
        }

        area.set(x,y, (float) texture.getWidth() /2);

        Vector2 mouseWorld=viewport.unproject(new Vector2(Gdx.input.getX(),Gdx.input.getY()));
        rotation=Functions.rotateTowards(rotation*MathUtils.degreesToRadians,x,y,mouseWorld.x,mouseWorld.y,-MathUtils.degreesToRadians*90,getSideThrusters().steeringSpeed)*MathUtils.radiansToDegrees;
    }

    public void addItem(Stack stack)
    {
        inventory.addItem(stack);
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public Vector2 getVelocity() {
        return new Vector2(MathUtils.cosDeg(rotation+90+90)*(leftAcceleration-rightAcceleration),MathUtils.cosDeg(rotation+90-90)*(leftAcceleration-rightAcceleration));
    }

    @Override
    public StarSystem getCurrentSystem() {
        return currentStarSystem;
    }

    public int occupiedCapacity()
    {
        int occupied=0;
        for (Stack stack : inventory.stacks) {
            if(stack!=null)
                occupied+=stack.count;
        }
        return occupied;
    }

    @Override
    public int getIntegrity() {
        return integrity;
    }
}
