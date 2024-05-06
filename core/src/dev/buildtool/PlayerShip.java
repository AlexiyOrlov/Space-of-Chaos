package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.buildtool.projectiles.Projectile;
import dev.buildtool.weapons.Weapon;
import dev.buildtool.weapons.WeaponRegistry;

public class PlayerShip implements Ship,SaveData {
    public float x,y;
    public float rotation,acceleration,leftAcceleration,rightAcceleration;
    public Vector2 direction=new Vector2();
    StarSystem currentStarSystem;
    public HashMap<Ware,Boolean> licences=new HashMap<>();
    private float fireDelay,secondaryFireDelay;
    public Inventory inventory=new Inventory(40);
    public Circle area=new Circle();
    public int money=1000;
    public Deque<WarePurchase> warePurchases=new ArrayDeque<>();
    public int integrity;
    boolean hasScanner=true;
    private final Inventory shipParts=new Inventory(5);
    public boolean damageOnlyAIShips=true;
    NPCPilot homingTarget;
    int reticleRotation;
    public int currentSystemId;
    public static final int rows=4,columns=8;
    public boolean mouseAction;
    //TODO save
    public ArrayList<Ship> hiredShips=new ArrayList<>();

    public PlayerShip() {
    }

    public PlayerShip(float x, float y, float rotation, StarSystem currentStarSystem) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        direction=new Vector2(0,0);
        inventory=new Inventory(32);
        setCurrentSystem(currentStarSystem);
        licences=new HashMap<>();
        Ware.WARES.forEach(ware -> {
            licences.put(Ware.CAR_PARTS,true);
            licences.put(Ware.ELECTRONICS,true);
            licences.put(Ware.JEWELLERY,true);
            licences.put(Ware.TOOLS,true);
            licences.put(Ware.FURNITURE,true);
            licences.put(Ware.CLOTHES,true);
            licences.put(Ware.WATER,true);
            licences.put(Ware.FOOD,true);
            licences.put(Ware.ALCOHOL,false);
            licences.put(Ware.MEDICATIONS,false);
            licences.put(Ware.FIREARMS,false);
        });
        area=new Circle();
        setHull(new Stack(Hull.BASIC,1));
        setPrimaryWeapon(new Stack(WeaponRegistry.GUN,1));
        setEngine(new Stack(Engine.BASIC,1));
        setThrusters(new Stack(SideThrusters.BASIC,1));
        integrity=getHull().integrity;
    }

    public void setPrimaryWeapon(Stack weapon)
    {
        if(!(weapon.item instanceof Weapon))
            throw new RuntimeException("Must be a weapon");
        shipParts.stacks[1]=new Stack(weapon.item,1);
    }

    public Weapon getPrimaryWeapon()
    {
        Stack weapon = shipParts.stacks[1];
        return weapon==null ?null: (Weapon) weapon.item;
    }
    public void setSecondaryWeapon(Stack weapon)
    {
        if(!(weapon.item instanceof Weapon))
            throw new RuntimeException("Must be a weapon");
        shipParts.stacks[4]=new Stack(weapon.item,1);
    }
    public Weapon getSecondaryWeapon(){
        Stack weapon=shipParts.stacks[4];
        return weapon==null ? null : (Weapon) weapon.item;
    }

    @Override
    public void setLeader(Ship ship) {

    }

    @Override
    public Ship getLeader() {
        return null;
    }

    @Override
    public void setTarget(Ship target) {

    }

    @Override
    public Ship getTarget() {
        return null;
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

    @Override
    public SideThrusters getThrusters() {
        return getSideThrusters();
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
        Functions.drawRotated(spriteBatch,getHull().look,x,y,rotation);
        spriteBatch.end();

        if(SpaceOfChaos.debugDraw) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.circle(area.x, area.y, area.radius);
            shapeRenderer.end();
        }

        ArrayList<String> hudText=new ArrayList<>();
        BitmapFont font= SpaceOfChaos.INSTANCE.bitmapFont;
        SpriteBatch uibatch= SpaceOfChaos.INSTANCE.uiBatch;
        int backBufferWidth = Gdx.graphics.getBackBufferWidth();
        int backBufferHeight = Gdx.graphics.getBackBufferHeight();

        for (Planet planet : currentStarSystem.planets) {
            if(planet.outline.overlaps(area))
            {
                hudText.add("Press 'E' to land");
                break;
            }
        }

        if(currentStarSystem.starGate.area.overlaps(area))
        {
            hudText.add("Press 'M' to open star map");
        }

        if(currentStarSystem.spaceStation!=null && currentStarSystem.spaceStation.area.overlaps(area))
        {
            hudText.add("Press 'E' to land");
        }

        int y=50;
        uibatch.begin();
        for (String s : hudText) {
            GlyphLayout glyphLayout=new GlyphLayout(font,s);
            font.draw(uibatch,s,backBufferWidth/2-glyphLayout.width/2,backBufferHeight/2-y);
            y+=20;
        }
        font.draw(uibatch,"Target mode",60,backBufferHeight/2);
        uibatch.end();
        uibatch.begin();
        if(damageOnlyAIShips)
        {
            font.draw(uibatch,"Enemy",70,backBufferHeight/2-20);
        }
        else {
            font.draw(uibatch,"All",70,backBufferHeight/2-20);
        }
        uibatch.end();
    }

    public void update(float deltaTime, Viewport viewport)
    {
        if(homingTarget!=null && !Functions.validTarget(homingTarget,this))
            homingTarget=null;
        reticleRotation+=deltaTime*90;
        if(SpaceOfChaos.INSTANCE.updateWorld) {
            if(Gdx.input.isKeyJustPressed(Input.Keys.TAB))
            {
                damageOnlyAIShips=!damageOnlyAIShips;
                if(damageOnlyAIShips && homingTarget!=null && homingTarget.pilotAI!=PilotAI.AI)
                    homingTarget=null;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                if (leftAcceleration < getSideThrusters().strafingSpeed)
                    leftAcceleration += 0.15f;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                if (rightAcceleration < getSideThrusters().strafingSpeed)
                    rightAcceleration += 0.15f;
            }
            //to the left
            this.x += MathUtils.cosDeg(rotation + 90 + 90) * leftAcceleration;
            this.y += MathUtils.sinDeg(rotation + 90 + 90) * leftAcceleration;

            //to the right
            this.x += MathUtils.cosDeg(rotation + 90 - 90) * rightAcceleration;
            this.y += MathUtils.sinDeg(rotation + 90 - 90) * rightAcceleration;

            if (leftAcceleration > 0)
                leftAcceleration -= 0.1f;
            if (rightAcceleration > 0)
                rightAcceleration -= 0.1f;

            direction.set(Vector2.Y).rotateDeg(rotation);
            direction.scl(acceleration);
            x += direction.x;
            y += direction.y;

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                if (acceleration > -getEngine().maxSpeed)
                    acceleration -= 0.05f;
            } else if (acceleration < 0) {
                acceleration += 0.015f;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                if (acceleration < getEngine().maxSpeed)
                    acceleration += 0.1f;
            } else if (acceleration > 0) {
                acceleration -= 0.03f;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                for (Planet planet : currentStarSystem.planets) {
                    if (planet.outline.overlaps(area)) {
                        if (planet.kind== Planet.Kind.INHABITED)
                            SpaceOfChaos.INSTANCE.setScreen(new PlanetScreen2(currentStarSystem, planet, this));
                        else {
                            SpaceOfChaos.INSTANCE.setScreen(new UselessPlanetScreen(planet,this,currentStarSystem));
                        }
                        acceleration = 0;
                        break;
                    }
                }
                if(currentStarSystem.spaceStation!=null && currentStarSystem.spaceStation.area.overlaps(area))
                {
                    SpaceOfChaos.INSTANCE.setScreen(new SpaceStationScreen(currentStarSystem.spaceStation,this));
                }
            }
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !mouseAction) {
                if (fireDelay <= 0) {
                    Projectile[] projectiles = getPrimaryWeapon().shoot(x, y, rotation, this, homingTarget, currentStarSystem);
                    if (projectiles != null) {
                        currentStarSystem.projectiles.addAll(List.of(projectiles));
                        fireDelay = getPrimaryWeapon().cooldown;
                    }
                }
            }
            if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && !mouseAction){
                Weapon secondaryWeapon=getSecondaryWeapon();
                if(secondaryWeapon!=null && secondaryFireDelay<=0)
                {
                    Projectile[] projectiles = secondaryWeapon.shoot(x, y, rotation, this, homingTarget, currentStarSystem);
                    if (projectiles != null) {
                        currentStarSystem.projectiles.addAll(List.of(projectiles));
                        secondaryFireDelay = secondaryWeapon.cooldown;
                    }
                }
            }

            if(fireDelay>0)
                fireDelay -= deltaTime;
            if(secondaryFireDelay>0)
                secondaryFireDelay-=deltaTime;

            if (Gdx.input.isKeyJustPressed(Input.Keys.M) && currentStarSystem.starGate.area.overlaps(area)) {
                SpaceOfChaos.INSTANCE.setScreen(new StarMap(currentStarSystem, this));
                acceleration = 0;
            }

            area.set(x, y, (float) getHull().texture.getWidth() / 2);

            Vector2 mouseWorld = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            rotation = Functions.rotateTowards(rotation * MathUtils.degreesToRadians, x, y, mouseWorld.x, mouseWorld.y, -MathUtils.degreesToRadians * 90, getSideThrusters().steeringSpeed) * MathUtils.radiansToDegrees;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.R))
        {
            toggleMouseAction();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.F))
        {
            ArrayList<Container> toRemove=new ArrayList<>();
            for (Container container : currentStarSystem.itemContainers) {
                if (Vector2.dst(x, y, container.x, container.y) < 40) {
                    inventory.addItem(container.stack);
                    toRemove.add(container);
                }
            }
            currentStarSystem.itemContainers.removeAll(toRemove);
        }
    }

    public boolean toggleMouseAction()
    {
        mouseAction=!mouseAction;
        if(mouseAction)
        {
            Gdx.graphics.setCursor(Gdx.graphics.newCursor(SpaceOfChaos.INSTANCE.crossCursor, 16,16));
        }
        else {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }
        return mouseAction;
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

    @Override
    public void setCurrentSystem(StarSystem starSystem) {
        currentStarSystem=starSystem;
        currentSystemId=starSystem.id;
    }

    @Override
    public void damage(int damage) {
        integrity-=damage;
    }

    @Override
    public void onProjectileImpact(Projectile projectile) {
        hiredShips.forEach(ship -> {
            if(ship.getTarget()==null)
            {
                ship.setTarget(projectile.shooter);
            }
        });
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

    @Override
    public boolean overlaps(Circle with) {
        return area.overlaps(with);
    }

    @Override
    public boolean contains(Vector2 vector2) {
        return area.contains(vector2);
    }

    @Override
    public boolean isLanded() {
        return false;
    }

    @Override
    public Map<String, Object> getData() {
        HashMap<String,Object> data=new HashMap<>();
        data.put("acceleration",acceleration);
        data.put("damage only AI ships",damageOnlyAIShips);
        data.put("primary fire delay",fireDelay);
        data.put("has scanner",hasScanner);
//        data.put("homing target",homingTarget);
        data.put("integrity",integrity);
        data.put("inventory",inventory.getData());
        data.put("left acceleration",leftAcceleration);
        int i=0;
        for (Ware ware : licences.keySet()) {
            boolean license= licences.get(ware);
            data.put("license "+i,license);
            data.put("licensed ware "+i,ware.name);
            i++;
        }
        data.put("money",money);
        data.put("right acceleration",rightAcceleration);
        data.put("rotation",rotation);
        data.put("secondary fire delay",secondaryFireDelay);
        data.put("ship parts",shipParts.getData());
        data.put("x",x);
        data.put("y",y);
        i=0;
        for (WarePurchase warePurchase : warePurchases) {
            data.put("ware purchase "+i,warePurchase.getData());
            i++;
        }
        data.put("ware purchase count",i);
        data.put("current system id",currentSystemId);
        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        acceleration=(float) (double) data.get("acceleration");
        fireDelay= (float)(double) data.get("primary fire delay");
        damageOnlyAIShips= (boolean) data.get("damage only AI ships");
        hasScanner= (boolean) data.get("has scanner");
        integrity= (int) data.get("integrity");
        leftAcceleration= (float)(double) data.get("left acceleration");
        for (int i = 0; i < Ware.WARES.size(); i++) {
            boolean license= (boolean) data.get("license "+i);
            Ware licenseWare= (Ware) Item.REGISTRY.get((String) data.get("licensed ware "+i));
            this.licences.put(licenseWare,license);
        }
        money= (int) data.get("money");
        rightAcceleration= (float)(double) data.get("right acceleration");
        rotation= (float)(double) data.get("rotation");
        secondaryFireDelay= (float)(double) data.get("secondary fire delay");
        x= (float)(double) data.get("x");
        y= (float)(double) data.get("y");
        inventory.load((Map<String, Object>) data.get("inventory"));
        shipParts.load((Map<String, Object>) data.get("ship parts"));
        int purchaseCount= (int) data.get("ware purchase count");
        for (int i = 0; i < purchaseCount; i++) {
            WarePurchase warePurchase=new WarePurchase();
            warePurchase.load((Map<String, Object>) data.get("ware purchase "+i));
            warePurchases.add(warePurchase);
        }
        currentSystemId= (int) data.get("current system id");
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public PilotAI getAI() {
        return null;
    }
}
