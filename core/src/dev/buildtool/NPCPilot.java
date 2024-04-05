package dev.buildtool;

import com.badlogic.gdx.Gdx;
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

public class NPCPilot implements Ship {
    public Hull hull;
    public SideThrusters sideThrusters;
    public Engine engine;
    public Weapon weapon;

    public float x,y;
    public Planet currentlyLandedOn;
    private int secondsOfRest=SpaceGame.random.nextInt(5,15);
    public float timeSpentOnPlanet=secondsOfRest;
    private float rotationDegrees =SpaceGame.random.nextFloat(360);

    private Inventory inventory;
    public StarSystem navigatingTo;
    private float acceleration;
    public boolean canJump;
    public int money=1000;
    private final Deque<NPCPurchase> purchases=new ArrayDeque<>();
    public StarSystem currentSystem;
    private Planet targetPlanet;
    public final Circle area;
    boolean landed;
    Random random=SpaceGame.random;
    private float fireCooldown;
    public PilotAI pilotAI;
    public Ship target;
    public int integrity;

    public NPCPilot(Planet currentlyLandedOn, PilotAI type, Weapon weapon, Hull hull, Engine engine,SideThrusters sideThrusters) {
        this.currentlyLandedOn = currentlyLandedOn;
        currentSystem=currentlyLandedOn.starSystem;
        inventory=new Inventory(40);
        area=new Circle();
        pilotAI=type;
        this.weapon=weapon;
        this.hull=hull;
        this.engine=engine;
        integrity=hull.integrity;
        this.sideThrusters=sideThrusters;
    }

    public void work(float deltaTime)
    {
        switch (pilotAI){
            case TRADER -> useTraderAI();
            case GUARD -> guardAI(deltaTime);
        }

        area.set(x,y,hull.look.getWidth()/2);
        if(fireCooldown>0)
        {
            fireCooldown-=deltaTime;
        }
    }

    private void useTraderAI() {
        if(navigatingTo==null)
        {
            List<StarSystem> closestSystems= findClosestSystems();
            List<StarSystem> systemsWithHigherPrices= filterSystemsWithHigherPrices(closestSystems);
            if(systemsWithHigherPrices.isEmpty())
            {
                navigatingTo=closestSystems.get(random.nextInt(closestSystems.size()));
                System.out.println("Profitable planets not found. Going to "+navigatingTo.getStarName());
            }
            else {
                navigatingTo = systemsWithHigherPrices.get(random.nextInt(systemsWithHigherPrices.size()));
                System.out.println("Going to system " + navigatingTo.star.name);
            }
        }
        else if(navigatingTo==currentSystem)
        {
            if(targetPlanet==null) {
                List<Planet> planetsWithHigherPrices = filterPlanetsWithProfitablePrices(currentSystem.planets);
                if(!planetsWithHigherPrices.isEmpty())
                {
                    targetPlanet = planetsWithHigherPrices.get(random.nextInt(planetsWithHigherPrices.size()));
                }
                else{
                    System.out.println("No suitable planets");
                    navigatingTo=null;
                }
            }
            else {
                assert targetPlanet.starSystem==currentSystem;
                rotateTowards(targetPlanet.x,targetPlanet.y);
                move();
                if(Vector2.dst(x,y,targetPlanet.x,targetPlanet.y)<20)
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
                move();
                canJump=false;
            }
            else {
                canJump=true;
            }
        }
    }

    private void guardAI(float deltaTime)
    {
        if(target!=null) {
            rotateTowards(target.getX(), target.getY());
            if (Vector2.dst(target.getX(), target.getY(), x, y) > Gdx.graphics.getBackBufferHeight() / 2) {
                move();
            }
            Vector2 forward = new Vector2(MathUtils.cosDeg(rotationDegrees), MathUtils.sinDeg(rotationDegrees));
            Vector2 dist = new Vector2(target.getX(), target.getY()).sub(x, y).nor();
            float dot = Vector2.dot(forward.x, forward.y, dist.x, dist.y);
            if (Math.abs(dot) < 0.05f) {
                if (fireCooldown <= 0) {
                    Projectile[] projectiles = weapon.shoot(x, y, rotationDegrees, this);
                    currentSystem.projectiles.addAll(projectiles);
                    fireCooldown = weapon.cooldown;
                }
            }
            if (fireCooldown > 0) {
                fireCooldown -= deltaTime;
            }
        }
    }

    public List<StarSystem> findClosestSystems() {
        return SpaceGame.INSTANCE.starSystems.stream().filter(starSystem -> Vector2.dst(starSystem.positionX, starSystem.positionY, currentSystem.positionX, currentSystem.positionY) <= engine.jumpDistance).collect(Collectors.toList());
    }

    public List<StarSystem> filterSystemsWithHigherPrices(List<StarSystem> systems) {
        return systems.stream().filter(starSystem -> {
            List<Planet> planets = starSystem.planets;
            List<Planet> planetsWithHigherPrices = filterPlanetsWithProfitablePrices(planets);
            return !planetsWithHigherPrices.isEmpty();
        }).collect(Collectors.toList());
    }

    private List<Planet> filterPlanetsWithProfitablePrices(List<Planet> planets) {
        if(!inventory.isEmpty()) {
            return planets.stream().filter(planet -> {
                if (planet.isInhabited) {
                    for (Ware ware : planet.warePrices.keySet()) {
                        int warePrice = planet.warePrices.get(ware);
                        if (warePrice > Ware.BASE_PRICES.get(ware)) {
                            for (Stack stack : inventory.stacks) {
                                if (stack != null && stack.item == ware) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            }).collect(Collectors.toList());
        }
        else {
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

    public void move()
    {
        this.x+=MathUtils.cosDeg(rotationDegrees+90)*engine.maxSpeed;
        this.y+=MathUtils.sinDeg(rotationDegrees+90)*engine.maxSpeed;
    }

    public void rotateTowards(float x,float y)
    {
        rotationDegrees = Functions.rotateTowards(rotationDegrees * MathUtils.degreesToRadians, this.x, this.y, x, y, -MathUtils.degreesToRadians * 90, sideThrusters.steeringSpeed) * MathUtils.radiansToDegrees;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
}
