package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import dev.buildtool.weapons.Weapon;

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

    private final Inventory inventory;
    public StarSystem navigatingTo;
    private float acceleration;
    public boolean canJump;
    public int money=1000;
    private final Deque<NPCPurchase> purchases=new ArrayDeque<>();
    private final HashMap<Ware,Integer> boughtFor=new HashMap<>();
    public StarSystem currentSystem;
    private Planet targetPlanet;
    public final Circle area;
    boolean landed;
    Random random=SpaceGame.random;
    private float fireCooldown;
    public PilotAI pilotAI;
    public Ship target;
    public int integrity;
    Planet homePlanet;
    private boolean strafeDirection;
    private float sideMovementTime;
    private float leftAcceleration,rightAcceleration,frontAcceleration;

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

    public NPCPilot(PilotAI type, Weapon weapon,Hull hull,Engine engine,SideThrusters sideThrusters,Planet homePlanet)
    {
        this(homePlanet,type,weapon,hull,engine,sideThrusters);
        this.homePlanet = homePlanet;
    }
    public void work(float deltaTime)
    {
        switch (pilotAI){
            case TRADER -> useTraderAI();
            case GUARD -> guardAI(deltaTime);
            case PIRATE -> pirateAI();
        }

        area.set(x,y,hull.look.getWidth()/2);
        if(fireCooldown>0)
        {
            fireCooldown-=deltaTime;
        }
        if(leftAcceleration>0)
        {
            leftAcceleration-=0.1f;
        }
        if(rightAcceleration>0)
        {
            rightAcceleration-=0.1f;
        }
        if(frontAcceleration>0)
        {
            frontAcceleration-=0.03f;
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
                if(SpaceGame.debugDraw)
                    System.out.println("Profitable planets not found. Going to "+navigatingTo.getStarName());
            }
            else {
                navigatingTo = systemsWithHigherPrices.get(random.nextInt(systemsWithHigherPrices.size()));
                if(SpaceGame.debugDraw)
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
                    if(SpaceGame.debugDraw)
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
                    if(SpaceGame.debugDraw)
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
//            float angle=findPlayerIntercept(new Vector2(target.getX(),target.getY()),new Vector2(target.getVelocity().x,target.getVelocity().y),)
//                    Vector2 prediction=Functions.intercept(new Vector2(x,y),new Vector2(target.getX(),target.getY()),target.getVelocity(),weapon.projectileSpeed);
            rotateTowards(target.getX(), target.getY());
            if (Vector2.dst(target.getX(), target.getY(), x, y) > Gdx.graphics.getBackBufferHeight() / 2) {
                move();
            }
            else {
                if(sideMovementTime<=0)
                {
                    sideMovementTime=random.nextInt(3,6);
                    strafeDirection= random.nextBoolean();
                }
                else {
                    sideMovementTime-=deltaTime;
                }
                strafe(strafeDirection);
            }
            if(isLookingAt(target.getX(),target.getY())) {
                fire();
            }
            if(target.getIntegrity()<=0)
            {
                target=null;
            }
        }
        else {
            rotateTowards(homePlanet.x, homePlanet.y);
            if(Vector2.dst(x,y, homePlanet.x, homePlanet.y)>260)
            {
                move();
            }
        }
    }

    private void fire() {
        if (fireCooldown <= 0) {
            if(target==null)
            {
                throw new RuntimeException("Target is null");
            }
            Projectile[] projectiles = weapon.shoot(x, y, rotationDegrees, this,target);
            currentSystem.projectiles.addAll(projectiles);
            fireCooldown = weapon.cooldown;
        }
    }

    private void pirateAI()
    {
        if(target==null)
        {

            Optional<Ship> randomShip=currentSystem.ships.stream().filter(ship -> (ship instanceof PlayerShip playerShip && !playerShip.inventory.isEmpty()) ||
                    (ship instanceof NPCPilot npcPilot && npcPilot.pilotAI==PilotAI.TRADER && !npcPilot.inventory.isEmpty())).findAny();
            randomShip.ifPresent(ship -> target = ship);

            StarGate starGate = currentSystem.starGate;
            if(Vector2.dst(starGate.x, starGate.y,x,y)>200){
                rotateTowards(starGate.x,starGate.y);
                move();
            }
        }
        else {
            if(Vector2.dst(x,y,target.getX(),target.getY())>200)
            {
                rotateTowards(target.getX(),target.getY());
                move();
                if (isLookingAt(target.getX(),target.getY()))
                {
                    fire();
                }
            }
            if(target.getCurrentSystem()!=currentSystem)
                target=null;
        }
    }

    public boolean isLookingAt(float x,float y)
    {
        Vector2 forward = new Vector2(MathUtils.cosDeg(rotationDegrees), MathUtils.sinDeg(rotationDegrees));
        Vector2 dist = new Vector2(target.getX(), target.getY()).sub(x, y).nor();
        float dot = Vector2.dot(forward.x, forward.y, dist.x, dist.y);
        return Math.abs(dot) < 0.1f;
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
                        Integer lastPurchasePrice=boughtFor.get(ware);
                        if(lastPurchasePrice!=null && lastPurchasePrice<warePrice)
                        {
                            return true;
                        }
                        else {
                            if (warePrice > Ware.BASE_PRICES.get(ware)) {
                                for (Stack stack : inventory.stacks) {
                                    if (stack != null && stack.item == ware) {
                                        return true;
                                    }
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
                        }}
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
            if(pilotAI==PilotAI.TRADER) {
                if (inventory.isEmpty()) {
                    if (money > 0) {
                        for (Ware ware : currentlyLandedOn.warePrices.keySet()) {
                            int warePrice = currentlyLandedOn.warePrices.get(ware);
                            if (warePrice < Ware.BASE_PRICES.get(ware)) {
                                //buy
                                int wareCount = currentlyLandedOn.wareAmounts.get(ware);
                                int canBuy = Math.min(wareCount, money / warePrice);
                                if (purchases.size() > 10) {
                                    purchases.removeFirst();
                                }
                                if (canBuy > 0) {
                                    money -= canBuy * warePrice;
                                    inventory.addItem(new Stack(ware, canBuy));
                                    purchases.add(new NPCPurchase(ware, warePrice));
                                    if (SpaceGame.debugDraw)
                                        System.out.println("Bought " + canBuy + " " + ware.name + ". Money: " + money);
                                    boughtFor.put(ware, warePrice);
                                }
                                if (money <= 0) {
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    //sell
                    for (Ware ware : currentlyLandedOn.warePrices.keySet()) {
                        int price = currentlyLandedOn.warePrices.get(ware);
                        int wareAmount = currentlyLandedOn.wareAmounts.get(ware);
                        for (Stack stack : inventory.stacks) {
                            if (stack != null && stack.item == ware) {
                                Iterator<NPCPurchase> it = purchases.iterator();
                                while (it.hasNext()) {
                                    NPCPurchase next = it.next();
                                    if (next.ware == ware && next.boughtFor < price) {
                                        int toSell = Math.min(Ware.MAXIMUM_WARE_AMOUNT - wareAmount, stack.count);
                                        if (toSell > 0) {
                                            inventory.removeItem(ware, toSell);
                                            money += toSell * price;
                                            if (SpaceGame.debugDraw)
                                                System.out.println("Sold " + toSell + " " + ware.name + ". Money: " + money);
                                            it.remove();
                                            boughtFor.remove(ware);
                                        }
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
        if(frontAcceleration<engine.maxSpeed)
        {
            frontAcceleration+=0.1f;
        }
        this.x+=MathUtils.cosDeg(rotationDegrees+90)*frontAcceleration;
        this.y+=MathUtils.sinDeg(rotationDegrees+90)*frontAcceleration;
    }

    public void strafe(boolean left)
    {
        if(left) {
            if(leftAcceleration<sideThrusters.strafingSpeed)
            {
                leftAcceleration+=0.15f;
            }
            this.x += MathUtils.cosDeg(rotationDegrees + 90 + 90) * leftAcceleration;
            this.y += MathUtils.sinDeg(rotationDegrees + 90 + 90) * leftAcceleration;
        }else {
            if(rightAcceleration<sideThrusters.strafingSpeed)
            {
                rightAcceleration+=0.15f;
            }
            this.x += MathUtils.cosDeg(rotationDegrees + 90 - 90) * rightAcceleration;
            this.y += MathUtils.sinDeg(rotationDegrees + 90 - 90) * rightAcceleration;
        }
    }

    public void rotateTowards(float x,float y)
    {
        rotationDegrees =Functions.rotateTowards(rotationDegrees * MathUtils.degreesToRadians, this.x, this.y, x, y, -MathUtils.degreesToRadians * 90, sideThrusters.steeringSpeed*3) * MathUtils.radiansToDegrees;
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
        return null;
    }

    @Override
    public StarSystem getCurrentSystem() {
        return currentSystem;
    }

    @Override
    public int getIntegrity() {
        return integrity;
    }

    public void onProjectileImpact(Projectile projectile)
    {

        float integrityPercent = (float) integrity / hull.integrity;
        if(integrityPercent<=0.3f)
        {
            Planet closestPlanet=null;
            for (Planet planet : currentSystem.planets) {
                if(closestPlanet==null || Vector2.dst(x,y,planet.x,planet.y)<Vector2.dst(x,y,closestPlanet.x,closestPlanet.y))
                {
                    closestPlanet=planet;
                }
            }
            boolean goToPlanet= !(Vector2.dst(x, y, currentSystem.starGate.x, currentSystem.starGate.y) < Vector2.dst(x, y, closestPlanet.x, closestPlanet.y));
            if(goToPlanet)
            {
                rotateTowards(closestPlanet.x,closestPlanet.y);
            }
            else {
                rotateTowards(currentSystem.starGate.x,currentSystem.starGate.y);
            }
            move();
        }
        else if(integrityPercent <=0.8f)
        {
            setNewTarget(projectile.shooter);
            if(pilotAI==PilotAI.GUARD) {
                currentSystem.ships.forEach(ship -> {
                    if (ship instanceof NPCPilot npcPilot) {
                        if (npcPilot.pilotAI == PilotAI.GUARD) {
                            npcPilot.setNewTarget(projectile.shooter);
                        }
                    }
                });
            }
        }
    }

    public void setNewTarget(Ship ship)
    {
        if(target==null)
        {
            target=ship;
        }
    }
//    private float findPlayerIntercept(Vector2 playerPos, Vector2 playerVel, int delta,Vector2 position,float projectileVelocity)
//    {
//        // calculate the speeds
//        float v = projectileVelocity * delta;
//        float u = (float) Math.sqrt(playerVel.x * playerVel.x +
//                playerVel.y * playerVel.y);
//
//        // calculate square distance
//        float c = (position.x - playerPos.x) * (position.x - playerPos.x) +
//                (position.y - playerPos.y) * (position.y - playerPos.y);
//
//        // calculate first two quadratic coefficients
//        float a = v * v - u * u;
//        float b = playerVel.x * (position.x - playerPos.x) +
//                playerVel.y * (position.y - playerPos.y);
//
//        // collision time
//        float t = -1.0f; // invalid value
//
//        // if speeds are equal
//        if (Math.abs(a) < (1e-5f)) // some small number, e.g. 1e-5f
//            t = c / (2.0f * b);
//        else {
//            // discriminant
//            b /= a;
//            float d = b * b + c / a;
//
//            // real roots exist
//            if (d > 0.0f) {
//                // if single root
//                if (Math.abs(d) < 1e-5f)
//                    t = b / a;
//                else {
//                    // how many positive roots?
//                    float e = (float) Math.sqrt(d);
//                    if (Math.abs(b) < e)
//                        t = b + e;
//                    else if (b > 0.0f)
//                        t = b - e;
//                }
//            }
//        }
//
//        // check if a valid root has been found
//        if (t < 0.0f) {
//            // nope.
//            // throw an exception here?
//            // or otherwise change return value format
//        }
//
//        // compute components and return direction angle
//        float x = playerVel.x + (playerPos.x - position.x) / t;
//        float y = playerVel.y + (playerPos.y - position.y) / t;
//        return (float) Math.atan2(y, x);
//    }
}
