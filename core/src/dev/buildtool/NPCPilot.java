package dev.buildtool;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
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
            List<StarSystem> closestSystems= findClosestSystems();
            List<StarSystem> systemsWithHigherPrices= filterSystemsWithHigherPrices(closestSystems);
            System.out.println("Found "+systemsWithHigherPrices.size()+" systems");
            navigatingTo=systemsWithHigherPrices.get(random.nextInt(systemsWithHigherPrices.size()));
            System.out.println("Going to system "+navigatingTo.star.name);
        }
        else if(navigatingTo==currentSystem)
        {
            if(targetPlanet==null) {
                List<Planet> planetsWithHigherPrices = filterPlanetsWithHigherPrices(currentSystem.planets);
                assert !planetsWithHigherPrices.isEmpty():"No suitable planets found in "+currentSystem.getStarName();
                targetPlanet = planetsWithHigherPrices.get(random.nextInt(planetsWithHigherPrices.size()));
//                System.out.println("Going to planet " + targetPlanet.name);
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
                    System.out.println("Landed on "+targetPlanet.name);
                    targetPlanet=null;
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

    public List<StarSystem> findClosestSystems() {
        return SpaceGame.INSTANCE.starSystems.stream().filter(starSystem -> Vector2.dst(starSystem.positionX, starSystem.positionY, currentSystem.positionX, currentSystem.positionY) <= engine.jumpDistance).collect(Collectors.toList());
    }

    public static List<StarSystem> filterSystemsWithHigherPrices(List<StarSystem> systems) {
        return systems.stream().filter(starSystem -> {
            List<Planet> planets = starSystem.planets;
            List<Planet> planetsWithHigherPrices = filterPlanetsWithHigherPrices(planets);
            return !planetsWithHigherPrices.isEmpty();
        }).collect(Collectors.toList());
    }

    private static List<Planet> filterPlanetsWithHigherPrices(List<Planet> planets) {
        return planets.stream().filter(planet -> {
            if (planet.isInhabited) {
                for (Ware ware : planet.warePrices.keySet()) {
                    int warePrice = planet.warePrices.get(ware);
                    if (warePrice > Ware.BASE_PRICES.get(ware)) {
                        return true;
                    }
                }
            }
            return false;
        }).collect(Collectors.toList());
    }


    public void workOnPlanet(float deltaTime, Planet currentPlanet)
    {
        if(timeSpentOnPlanet>=secondsOfRest)
        {
            //take off
            x=currentPlanet.x;
            y=currentPlanet.y;
            landed=false;
            secondsOfRest=SpaceGame.random.nextInt(5,15);
            timeSpentOnPlanet=0;
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
                            if(purchases.size()>10)
                            {
                                purchases.removeFirst();
                            }
                            if(canBuy>0)
                            {
                                money-=canBuy*warePrice;
                                inventory.addItem(new Stack(ware,canBuy));
                                purchases.add(new NPCPurchase(ware,warePrice));
                                System.out.println("Bought "+canBuy+" "+ware.name+". Money: "+money);
                            }
                            if(money<=0)
                            {
                                break;
                            }
                        }
                    }
                }
            }
            else {
                for (Ware ware : currentlyLandedOn.warePrices.keySet()) {
                    int price=currentlyLandedOn.warePrices.get(ware);
                    int wareAmount=currentlyLandedOn.wareAmounts.get(ware);
                    for (Stack stack : inventory.stacks) {
                        if(stack!=null && stack.item==ware)
                        {
                            Iterator<NPCPurchase> it= purchases.iterator();
                            while (it.hasNext())
                            {
                                NPCPurchase next=it.next();
                                if(next.ware==ware && next.boughtFor<price)
                                {
                                    int toSell=Math.min(Ware.MAXIMUM_WARE_AMOUNT-wareAmount,stack.count);
                                    if(toSell>0) {
                                        inventory.removeItem(ware, toSell);
                                        money += toSell * price;
                                        System.out.println("Sold " + toSell + " " + ware.name + ". Money: " + money);
                                        it.remove();
                                    }
                                }
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
