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
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import dev.buildtool.projectiles.Projectile;
import dev.buildtool.weapons.Weapon;

public class NPCPilot implements Ship, SaveData {
    public static int nextId=1;
    public int id;
    public Hull hull;
    public SideThrusters sideThrusters;
    public Engine engine;
    public Weapon primaryWeapon,secondaryWeapon;

    public float x,y;
    public Planet currentlyLandedOn;
    private int secondsOfRest= SpaceOfChaos.random.nextInt(5,15);
    public float timeSpentOnPlanet=secondsOfRest;
    private float rotationDegrees = SpaceOfChaos.random.nextFloat(360);

    private Inventory inventory=new Inventory(40);
    public StarSystem navigatingTo;
    public boolean canJump;
    public int money=1000;
    private final Deque<NPCPurchase> purchases=new ArrayDeque<>();
    private final HashMap<Ware,Integer> boughtFor=new HashMap<>();
    public StarSystem currentSystem;
    private Planet targetPlanet;
    public final Circle area=new Circle();
    boolean landed;
    Random random= SpaceOfChaos.random;
    private float fireCooldown,secondaryCooldown;
    public PilotAI pilotAI;
    public Ship target;
    public int integrity;
    Planet homePlanet;
    private boolean strafeDirection;
    private float sideMovementTime;
    private float leftAcceleration,rightAcceleration,frontAcceleration;
    private Container containerToCollect;
    private Planet closestPlanet;
    private State state;
    public int homePlanetId;
    public int currentSystemId;

    @Override
    public Map<String, Object> getData() {
        HashMap<String,Object> data=new HashMap<>();
        data.put("area x",area.x);
        data.put("area y",area.y);
        int w=0;
        for (Ware ware : boughtFor.keySet()) {
            data.put("ware "+w,ware.name);
            data.put("ware price "+w,boughtFor.get(ware));
            w++;
        }
        data.put("ware entries",w);
        data.put("can jump",canJump);
        data.put("hull",hull.name);
        data.put("weapon",primaryWeapon.name);
        if(secondaryWeapon!=null)
            data.put("secondary weapon",secondaryWeapon.name);
        data.put("engine",engine.name);
        data.put("side thrusters",sideThrusters.name);
        data.put("type",pilotAI.toString());
        data.put("home planet",homePlanetId);
        data.put("current system id",currentSystemId);
        data.put("id",id);
        data.put("integrity",integrity);
        data.put("x",x);
        data.put("y",y);
        //currentlylandendon
        //current system
        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        area.x= (float)(double) data.get("area x");
        area.y= (float)(double) data.get("area y");
        int wareEntries= (int) data.get("ware entries");
        for (int i = 0; i < wareEntries; i++) {
            String wareName= (String) data.get("ware +"+i);
            Ware ware= (Ware) Item.REGISTRY.get(wareName);
            int price= (int) data.get("ware price "+i);
            boughtFor.put(ware,price);
        }
        canJump= (boolean) data.get("can jump");
        hull= (Hull) Item.REGISTRY.get((String) data.get("hull"));
        primaryWeapon= (Weapon) Item.REGISTRY.get((String) data.get("weapon"));
        if(data.containsKey("secondary weapon"))
            secondaryWeapon= (Weapon) Item.REGISTRY.get((String) data.get("secondary weapon"));
        engine= (Engine) Item.REGISTRY.get((String) data.get("engine"));
        sideThrusters= (SideThrusters) Item.REGISTRY.get((String) data.get("side thrusters"));
        pilotAI=PilotAI.valueOf((String) data.get("type"));
        homePlanetId= (int) data.get("home planet");
        currentSystemId= (int) data.get("current system id");
        id= (int) data.get("id");
        integrity= (int) data.get("integrity");
        x= (float)(double) data.get("x");
        y= (float)(double) data.get("y");
    }

    enum State {
        ESCAPING_TO_SYSTEM,
        GOING_TO_REPAIR,
        FINE
    }

    public NPCPilot() {
    }

    public NPCPilot(Planet currentlyLandedOn, PilotAI type, Weapon primaryWeapon, Hull hull, Engine engine, SideThrusters sideThrusters, StarSystem currentStarSystem, Planet homePlanet) {
        this.currentlyLandedOn = currentlyLandedOn;
        currentSystem=currentlyLandedOn.starSystem;
        inventory=new Inventory(40);
        pilotAI=type;
        this.primaryWeapon = primaryWeapon;
        this.hull=hull;
        this.engine=engine;
        integrity=hull.integrity;
        this.sideThrusters=sideThrusters;
        homePlanetId= homePlanet.id;
        currentSystemId=currentStarSystem.id;
        id=nextId++;
    }

    public NPCPilot(PilotAI type, Weapon primaryWeapon, Hull hull, Engine engine, SideThrusters sideThrusters, Planet homePlanet, StarSystem currentStarSystem)
    {
        this(homePlanet,type, primaryWeapon,hull,engine,sideThrusters, currentStarSystem,homePlanet);
        this.homePlanet = homePlanet;
    }

    public NPCPilot(PilotAI pilotAI, Weapon primaryWeapon, Hull hull, Engine engine, SideThrusters sideThrusters, Planet homePlanet, StarSystem currentStarSystem,Weapon secondaryWeapon) {
        this(pilotAI,primaryWeapon,hull,engine,sideThrusters,homePlanet, currentStarSystem);
        this.secondaryWeapon=secondaryWeapon;
    }

    public void work(float deltaTime)
    {
        if(homePlanet==null)
        {
            for (StarSystem starSystem : SpaceOfChaos.INSTANCE.starSystems) {
                if(starSystem.id==currentSystemId)
                {
                    currentSystem=starSystem;
                    for (Planet planet : starSystem.planets) {
                        if(planet.id==homePlanetId)
                        {
                            homePlanet=planet;
                            break;
                        }
                    }
                    break;
                }
            }
        }

        area.set(x,y,hull.look.getWidth()/2);
        if(fireCooldown>0)
        {
            fireCooldown-=deltaTime;
        }
        if(secondaryCooldown>0)
        {
            secondaryCooldown-=deltaTime;
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

        switch (pilotAI){
            case TRADER -> traderAI();
            case GUARD -> guardAI(deltaTime);
            case PIRATE -> pirateAI();
            case AI -> aiai(deltaTime);
        }

    }

    private float getIntegrityPercent()
    {
        return (float) integrity/ hull.integrity;
    }
    private void traderAI() {
        if(getIntegrityPercent()<0.3f)
        {
            escapeSequence();
        }
        else {
            if(target!=null)
            {
                rotateTowards(target.getX(),target.getY());
                if(Vector2.dst(target.getX(),target.getY(),x,y)> SpaceOfChaos.getWindowHeight()/2)
                {
                    move();
                }
                else {
                    fire();
                }
                if(target.getCurrentSystem()!=currentSystem || target.getIntegrity()<=0 || target.isLanded())
                    target=null;
            }
            else if(navigatingTo==null)
            {
                List<StarSystem> closestSystems= findClosestSystems();
                List<StarSystem> systemsWithHigherPrices= filterSystemsWithHigherPrices(closestSystems);
                if(systemsWithHigherPrices.isEmpty())
                {
                    navigatingTo=closestSystems.get(random.nextInt(closestSystems.size()));
                    if(SpaceOfChaos.debugDraw)
                        System.out.println("Profitable planets not found. Going to "+navigatingTo.getStarName());
                }
                else {
                    navigatingTo = systemsWithHigherPrices.get(random.nextInt(systemsWithHigherPrices.size()));
                    if(SpaceOfChaos.debugDraw)
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
                        if(SpaceOfChaos.debugDraw)
                            System.out.println("No suitable planets");
                        navigatingTo=null;
                    }
                }
                else {
                    if(targetPlanet.starSystem!=currentSystem)
                    {
                        targetPlanet=null;
                    }
                    else {
                        rotateTowards(targetPlanet.x, targetPlanet.y);
                        if (Vector2.dst(x, y, targetPlanet.x, targetPlanet.y) < 20) {
                            currentlyLandedOn = targetPlanet;
                            land(targetPlanet);
                            if (SpaceOfChaos.debugDraw)
                                System.out.println("Landed on " + targetPlanet.name);
                            targetPlanet = null;
                        } else {
                            move();
                        }
                    }
                }
            }
            else {
                StarGate starGate=currentSystem.starGate;
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
    }

    private void escapeSequence() {
        if(!inventory.isEmpty())
        {
            for (int i=0;i<inventory.stacks.length;i++) {
                Stack stack=inventory.stacks[i];
                if (stack != null) {
                    Container container = new Container(stack, x, y, random.nextInt(-180, 180));
                    currentSystem.itemContainers.add(container);
                    inventory.stacks[i]=null;
                }
            }
        }
        if(state==null) {
            float distanceToClosestPlanet = Float.MAX_VALUE;
            if (closestPlanet == null) {
                for (Planet planet : currentSystem.planets) {
                    if (planet.kind== Planet.Kind.INHABITED) {
                        float distanceToPlanet = Vector2.dst(x, y, planet.x, planet.y);
                        if (distanceToPlanet < distanceToClosestPlanet)
                        {
                            distanceToClosestPlanet=distanceToPlanet;
                            closestPlanet = planet;
                        }
                    }
                }
            }
            StarGate starGate = currentSystem.starGate;
            float distanceToStarGate = Vector2.dst(starGate.x, starGate.y, x, y);
            if (distanceToStarGate < distanceToClosestPlanet) {
                state = State.ESCAPING_TO_SYSTEM;
            } else {
                state = State.GOING_TO_REPAIR;
            }
        }else{
            switch (state){
                case GOING_TO_REPAIR -> {
                    rotateTowards(closestPlanet.x, closestPlanet.y);
                    move();
                    if(Vector2.dst(x,y,closestPlanet.x,closestPlanet.y)<20)
                    {
                        land(closestPlanet);
                    }
                }
                case ESCAPING_TO_SYSTEM -> {
                    if(navigatingTo==null){
                        findClosestSystems().stream().findAny().ifPresent(starSystem -> navigatingTo=starSystem);
                    }
                    else {
                        StarGate starGate = currentSystem.starGate;
                        rotateTowards(starGate.x, starGate.y);
                        move();
                        if(Vector2.dst(x,y,starGate.x,starGate.y)<20)
                        {
                            canJump=true;
                        }
                    }
                }
            }
        }
    }

    private void land(Planet on)
    {
        on.ships.add(this);
        landed=true;
    }

    private void guardAI(float deltaTime)
    {
        if(target!=null) {
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
            if(target.getIntegrity()<=0 || target.getCurrentSystem()!=currentSystem || target.isLanded())
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
            for (Ship ship : currentSystem.ships) {
                if(ship instanceof NPCPilot npcPilot && npcPilot.pilotAI==PilotAI.AI)
                {
                    target=ship;
                    break;
                }
            }
        }

    }

    private void fire() {
        if(target==null)
        {
            throw new RuntimeException("Target is null");
        }
        if (fireCooldown <= 0) {
            Projectile[] projectiles = primaryWeapon.shoot(x, y, rotationDegrees, this,target, currentSystem);
            currentSystem.projectiles.addAll(List.of(projectiles));
            fireCooldown = primaryWeapon.cooldown;
        }
        if(secondaryCooldown<=0 && secondaryWeapon!=null)
        {
            Projectile[] projectiles=secondaryWeapon.shoot(x,y,rotationDegrees,this,target, currentSystem);
            currentSystem.projectiles.addAll(List.of(projectiles));
            secondaryCooldown=secondaryWeapon.cooldown;
        }
    }

    private void pirateAI()
    {
        if(getIntegrityPercent()<0.3f)
        {
            escapeSequence();
        }
        else {
            if (containerToCollect != null) {
                if (Vector2.dst(containerToCollect.x, containerToCollect.y, x, y) < 20) {
                    inventory.addItem(containerToCollect.stack);
                    currentSystem.itemContainers.remove(containerToCollect);
                    containerToCollect = null;
                } else {
                    rotateTowards(containerToCollect.x, containerToCollect.y);
                    move();
                }
            } else if (!inventory.isEmpty()) {
                if(targetPlanet==null)
                {
                    List<Planet> inhabited= currentSystem.planets.stream().filter(planet -> planet.kind== Planet.Kind.INHABITED).toList();
                    targetPlanet=inhabited.get(random.nextInt(inhabited.size()));
                }
                else {
                    if(Vector2.dst(targetPlanet.x,targetPlanet.y,x,y)<20)
                    {
                        land(targetPlanet);
                    }
                    else {
                        rotateTowards(targetPlanet.x, targetPlanet.y);
                        move();
                    }
                }
            } else {
                if (target == null) {

                    Optional<Ship> randomShip = currentSystem.ships.stream().filter(ship -> (ship instanceof PlayerShip playerShip && !playerShip.inventory.isEmpty()) ||
                            (ship instanceof NPCPilot npcPilot && npcPilot.pilotAI == PilotAI.TRADER && !npcPilot.inventory.isEmpty() && !npcPilot.landed)).findAny();
                    randomShip.ifPresent(ship -> target = ship);

                    StarGate starGate = currentSystem.starGate;
                    if (Vector2.dst(starGate.x, starGate.y, x, y) > 200) {
                        rotateTowards(starGate.x, starGate.y);
                        move();
                    }
                } else {
                    rotateTowards(target.getX(), target.getY());
                    if (Vector2.dst(x, y, target.getX(), target.getY()) > 200) {
                        move();
                    }
                    if (Vector2.dst(x, y, target.getX(), target.getY()) < Gdx.graphics.getBackBufferHeight() / 2) {
                        if (isLookingAt(target.getX(), target.getY())) {
                            fire();
                        }
                    }
                    if (target.getCurrentSystem() != currentSystem || target.getIntegrity() <= 0 || target.isLanded())
                        target = null;
                }
            }

            currentSystem.itemContainers.stream().reduce((container, container2) -> Vector2.dst(container.x, container.y, x, y) < Vector2.dst(container2.x, container2.y, x, y) ? container : container2).ifPresent(container -> {
                containerToCollect = container;
                target=null;
            });
        }
    }

    private void aiai(float deltaTime)
    {
        if(target!=null)
        {
            rotateTowards(target.getX(),target.getY());
            if(Vector2.dst(x,y,target.getX(),target.getY())>Gdx.graphics.getBackBufferHeight()/2)
            {
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
                fire();
            }
            if(target.getIntegrity()<=0 || target.isLanded() || target.getCurrentSystem()!=currentSystem)
                target=null;
        }
        else if(state==null)
        {
            if(navigatingTo!=null)
            {
                StarGate starGate=currentSystem.starGate;
                rotateTowards(starGate.x,starGate.y);
                if(Vector2.dst(starGate.x,starGate.y,x,y)<20)
                {
                    canJump=true;
                }
                else {
                    move();
                }
            }
            else {
                List<Ship> potentialTargets = currentSystem.ships.stream().filter(ship -> ship instanceof PlayerShip || (ship instanceof NPCPilot npcPilot && npcPilot.pilotAI != PilotAI.AI)).toList();
                if (potentialTargets.isEmpty()) {
                    if (targetPlanet == null)
                        targetPlanet = currentSystem.planets.get(random.nextInt(currentSystem.planets.size()));
                    if (Vector2.dst(targetPlanet.x, targetPlanet.y, x, y) > 200) {
                        rotateTowards(targetPlanet.x, targetPlanet.y);
                        move();
                    }
                } else {
                    target = potentialTargets.get(random.nextInt(potentialTargets.size()));
                    if(target.getCurrentSystem()!=currentSystem)
                        throw new RuntimeException("System mismatch");
                }
            }
        }
    }

    public boolean isLookingAt(float x,float y)
    {
        Vector2 forward = new Vector2(MathUtils.cosDeg(rotationDegrees), MathUtils.sinDeg(rotationDegrees));
        Vector2 dist = new Vector2(this.x, this.y).sub(x, y).nor();
        float dot = Vector2.dot(forward.x, forward.y, dist.x, dist.y);
        return Math.abs(dot) < 0.1f;
    }

    public List<StarSystem> findClosestSystems() {
        return SpaceOfChaos.INSTANCE.starSystems.stream().filter(starSystem -> !starSystem.occupied && Vector2.dst(starSystem.positionX, starSystem.positionY, currentSystem.positionX, currentSystem.positionY) <= engine.jumpDistance).collect(Collectors.toList());
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
                if (planet.kind== Planet.Kind.INHABITED) {
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
                if (planet.kind== Planet.Kind.INHABITED) {
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
        if(pilotAI==PilotAI.PIRATE)
        {
            for (int i = 0; i < inventory.stacks.length; i++) {
                Stack stack=inventory.stacks[i];
                if(stack!=null)
                {
                    if(stack.item instanceof Ware ware) {
                        int warePrice = currentPlanet.warePrices.get(ware);
                        int moneyGain = stack.count*warePrice;
                        inventory.stacks[i]=null;
                        money+=moneyGain;
                    }
                }
            }
        }

        if(state==State.GOING_TO_REPAIR)
        {
            //TODO
            int toRepair=hull.integrity-integrity;
            int repairCost=10*toRepair;
            int toSpend=Math.min(money,repairCost);
            if(toSpend>0) {
                int willRepair = toSpend / 10;
                money -= toSpend;
                integrity += willRepair;
                state = State.FINE;
                secondsOfRest = SpaceOfChaos.random.nextInt(20, 40);
            }
            else {
                state=null;
                Functions.log("No money on repairs");
            }
        }
        else {
            if (timeSpentOnPlanet >= secondsOfRest) {
                //take off
                x = currentPlanet.x;
                y = currentPlanet.y;
                landed = false;
                secondsOfRest = SpaceOfChaos.random.nextInt(5, 15);
                timeSpentOnPlanet = 0;
                state=null;
            } else {
                timeSpentOnPlanet += deltaTime;
                if (pilotAI == PilotAI.TRADER) {
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
                                        if (SpaceOfChaos.debugDraw)
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
                                                if (SpaceOfChaos.debugDraw)
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
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer)
    {
        if(!landed) {
            spriteBatch.begin();
            Functions.drawRotated(spriteBatch, hull.look,  x, y, rotationDegrees);
            spriteBatch.end();

            if(SpaceOfChaos.debugDraw) {
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.circle(area.x, area.y, area.radius);
                shapeRenderer.end();

                float integrityPercent= (float) integrity / hull.integrity;
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.rect(x-50,y+ hull.look.getHeight()/2,100*integrityPercent,10);
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
    public void setCurrentSystem(StarSystem starSystem) {
        currentSystem=starSystem;
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
        return landed;
    }

    @Override
    public void damage(int damage) {
        integrity-=damage;
        if(integrity<=0)
        {
            for (Stack stack : inventory.stacks) {
                if(stack!=null) {
                    Container container = new Container(new Stack(stack.item, random.nextInt((int) Math.max(1, stack.count * 0.7f))), x + random.nextInt(-30, 30), y + random.nextInt(-30, 30), random.nextInt(-180, 180));
                    currentSystem.itemContainers.add(container);
                }
            }
        }
    }

    public void onProjectileImpact(Projectile projectile)
    {

        float integrityPercent = (float) integrity / hull.integrity;
        if(integrityPercent <=0.8f)
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

    public void afterJump()
    {
        canJump = false;
        navigatingTo=null;
        if(state==State.ESCAPING_TO_SYSTEM && money>9)
        {
            state=State.GOING_TO_REPAIR;
        }
        else
            state=null;
    }

    @Override
    public int getId() {
        return id;
    }
}
