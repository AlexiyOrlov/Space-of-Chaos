package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class NPCPilot {
    public Hull hull=Hull.TRADING1;
    public Engine engine=Engine.SLOW;
    public Weapon weapon=WeaponRegistry.GUN;

    public float x,y;
    public Planet currentlyLandedOn;
    private int secondsOfRest=SpaceGame.random.nextInt(5,15);
    public float timeSpentOnPlanet=secondsOfRest;
    private float rotationDegrees =SpaceGame.random.nextFloat(360);

    private Inventory inventory;
    public StarSystem navigatingTo;
    private float acceleration;
    public boolean canJump;
    public int money=10000;
    private final Deque<NPCPurchase> purchases=new ArrayDeque<>();
    public StarSystem currentSystem;
    private Planet targetPlanet;
    private Circle area;
    boolean landed;
    Random random=SpaceGame.random;

    public NPCPilot(Planet currentlyLandedOn) {
        this.currentlyLandedOn = currentlyLandedOn;
        currentSystem=currentlyLandedOn.starSystem;
        inventory=new Inventory(40);
        area=new Circle();
    }

    public void work(float deltaTime)
    {

        if(navigatingTo==null)
        {
            List<StarSystem> closestSystems= SpaceGame.INSTANCE.starSystems.stream().filter(starSystem -> Vector2.dst(starSystem.positionX,starSystem.positionY,currentSystem.positionX,currentSystem.positionY)<= engine.jumpDistance).collect(Collectors.toList());
            List<StarSystem> systemsWithHigherPrices=closestSystems.stream().filter(starSystem -> {
                List<Planet> planets=starSystem.planets;
                List<Planet> planetsWithHigherPrices= planets.stream().filter(planet -> {
                    if(planet.isInhabited) {
                        for (Ware ware : planet.warePrices.keySet()) {
                            int warePrice = planet.warePrices.get(ware);
                            if (warePrice > Ware.BASE_PRICES.get(ware)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
                return !planetsWithHigherPrices.isEmpty();
            }).collect(Collectors.toList());
            System.out.println("Found "+systemsWithHigherPrices.size()+" systems");
            assert !systemsWithHigherPrices.isEmpty();
            navigatingTo=systemsWithHigherPrices.get(random.nextInt(systemsWithHigherPrices.size()));
            System.out.println("Going to system "+navigatingTo.star.name);
        }
        else if(navigatingTo==currentSystem)
        {
            if(targetPlanet==null) {
                List<Planet> planetsWithHigherPrices = currentSystem.planets.stream().filter(planet -> {
                    if (planet.isInhabited) {
                        for (Ware ware : planet.warePrices.keySet()) {
                            int price = planet.warePrices.get(ware);
                            if(!purchases.isEmpty()) {
                                for (NPCPurchase purchase : purchases) {
                                    if (ware == purchase.ware) {
                                        if (purchase.boughtFor <= price) {
                                            return true;
                                        }
                                    }
                                }
                            }
                            else {
                                return price>Ware.BASE_PRICES.get(ware);
                            }
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
                targetPlanet = planetsWithHigherPrices.get(random.nextInt(planetsWithHigherPrices.size()));
                System.out.println("Going to " + targetPlanet.name);
            }
            else {
                assert targetPlanet.starSystem==currentSystem;
                rotateTowards(targetPlanet.x,targetPlanet.y);
                moveTo(targetPlanet.x, targetPlanet.y);
                if(targetPlanet.outline.overlaps(this.area))
                {
                    currentlyLandedOn=targetPlanet;
                    targetPlanet.ships.add(this);
                    landed=true;
                }
            }
        }
        else {
            StarGate starGate=currentSystem.starGate;
//                float xdist=starGate.x-x;
//                float ydist=starGate.y-y;
//                Vector2 vector2=new Vector2(xdist,ydist).nor();
//                Vector2 vector21=new Vector2(MathUtils.cosDeg(rotationDegrees-90),MathUtils.sinDeg(rotationDegrees-90));

//                float dotProduct=vector21.dot(vector2);
//                float compare = 1 + dotProduct;
            if(Vector2.dst(x,y,starGate.x,starGate.y)>20)
            {
                rotateTowards(starGate.x,starGate.y);
                moveTo(starGate.x,starGate.y);
                canJump=false;
            }
            else {
                canJump=true;
            }
        }

        area.set(x,y,hull.look.getWidth()/2);
    }

    public void workOnPlanet(float deltaTime)
    {
        if(timeSpentOnPlanet>=secondsOfRest)
        {
            //take off
            x=currentlyLandedOn.x;
            y=currentlyLandedOn.y;
            currentlyLandedOn=null;
            secondsOfRest=SpaceGame.random.nextInt(5,15);

        }
        else {
            timeSpentOnPlanet+=deltaTime;
            if(inventory.isEmpty())
            {
                if(money>0) {
                    for (Ware ware : currentlyLandedOn.warePrices.keySet()) {
                        int warePrice = currentlyLandedOn.warePrices.get(ware);
                        if (warePrice < Ware.BASE_PRICES.get(ware)) {
                            //buy
                            int wareCount=currentlyLandedOn.wareAmounts.get(ware);
                            int canBuy=Math.min(wareCount,money/warePrice);
                            money-=canBuy*warePrice;
                            inventory.addItem(new Stack(ware,canBuy));
                            if(purchases.size()>10)
                            {
                                purchases.removeFirst();
                            }
                            purchases.add(new NPCPurchase(ware,canBuy));
                            if(canBuy>0)
                                System.out.println("Bought "+canBuy+" "+ware.name);
                            if(money<=0)
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer)
    {
        if(!landed) {
            spriteBatch.begin();
            Functions.drawRotated(spriteBatch, hull.look,  x, y, rotationDegrees);
            spriteBatch.end();

            if(SpaceGame.debugDraw) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.circle(area.x, area.y, area.radius);
                shapeRenderer.end();
            }
        }
    }

    public void setPosition(float x,float y)
    {
        this.x=x;
        this.y=y;
    }

    public void moveTo(float x,float y)
    {
        this.x+=MathUtils.cosDeg(rotationDegrees+90)*engine.maxSpeed;
        this.y+=MathUtils.sinDeg(rotationDegrees+90)*engine.maxSpeed;
    }

    public void rotateTowards(float x,float y)
    {
        //0.02f
        rotationDegrees = Functions.rotateTowards(rotationDegrees * MathUtils.degreesToRadians, this.x, this.y, x, y, -MathUtils.degreesToRadians * 90, 0.1f) * MathUtils.radiansToDegrees;
    }
}
