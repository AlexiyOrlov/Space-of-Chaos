package dev.buildtool;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class NPCPilot {
    public Hull hull=Hull.TRADING1;
    public Engine engine=Engine.BASIC;
    public Weapon weapon=WeaponRegistry.GUN;

    public float x,y;
    public boolean landed=true;
    public Planet currentlyLandedOn;
    private int secondsOfRest=SpaceGame.random.nextInt(5,15);
    public float timeSpentOnPlanet=secondsOfRest;
    private float rotationDegrees =SpaceGame.random.nextFloat(360);

    private Inventory inventory;
    private StarSystem navigatingTo;
    public int money=10000;
    public NPCPilot(Planet currentlyLandedOn) {
        this.currentlyLandedOn = currentlyLandedOn;
        inventory=new Inventory(40);
    }

    public void work(float deltaTime)
    {
        Random random=SpaceGame.random;
        if(landed)
        {
            if(timeSpentOnPlanet>=secondsOfRest)
            {
                //take off
                x=currentlyLandedOn.x;
                y=currentlyLandedOn.y;
                landed=false;
                secondsOfRest=SpaceGame.random.nextInt(5,15);
            }
            else {
                timeSpentOnPlanet+=deltaTime;
            }

            boolean inventoryEmpty=true;
            for (Stack stack : inventory.stacks) {
                if(stack!=null)
                {
                    inventoryEmpty=false;
                    break;
                }
            }
            if(inventoryEmpty)
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
                            if(money<=0)
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }
        else {
            StarSystem currentSystem=currentlyLandedOn.starSystem;
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
            assert !systemsWithHigherPrices.isEmpty();

            if(navigatingTo==null)
            {
                navigatingTo=systemsWithHigherPrices.get(random.nextInt(systemsWithHigherPrices.size()));
            }
            else {
                StarGate starGate=currentSystem.starGate;
//                float xdist=starGate.x-x;
//                float ydist=starGate.y-y;
//                float dotProduct=Vector2.dot(xdist,ydist,x,y);
//                if(dotProduct<0.1f)
//                {
//
//                }
//                else {
//                    rotation= Functions.rotateTowards(rotation,x,y,starGate.x,starGate.y,-90,0.1f);
//                }
            }
            float rotated=Functions.rotateTowards(rotationDegrees*MathUtils.degreesToRadians,x,y,SpaceGame.INSTANCE.playerShip.x,SpaceGame.INSTANCE.playerShip.y,-MathUtils.degreesToRadians*90,1)*MathUtils.radiansToDegrees;
            rotationDegrees=rotated;
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
                shapeRenderer.circle(x, y, this.hull.look.getWidth() / 2);
                shapeRenderer.end();
            }
        }
    }
}
