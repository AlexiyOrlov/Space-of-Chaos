package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class StarSystem {
    public ArrayList<Planet> planets;
    public Star star;
    public HashMap<Ware,Float> priceFactors=new HashMap<>(Ware.WARES.size());
    public int positionX,positionY;
    public ArrayList<NPCPilot> ships=new ArrayList<>(),shipsToTransfer=new ArrayList<>();
    public StarGate starGate;
    public Array<Projectile> projectiles;
    public StarSystem(ArrayList<Texture> planetTextures,ArrayList<Texture> starTextures,int x,int y) {
        projectiles=new Array<>();
        this.planets = new ArrayList<>(7);
        Random random = SpaceGame.random;
        int inhabitedPlanetCount=0;
        int distance=500;
        for (int i = 0; i < random.nextInt(3,7); i++) {
            boolean inhabited=false;
            if(inhabitedPlanetCount<3)
            {
                inhabited= random.nextBoolean();
                if(inhabited)
                    inhabitedPlanetCount++;
            }
            planets.add(new Planet(planetTextures.get(random.nextInt(planetTextures.size())), distance, random.nextFloat(-MathUtils.PI,MathUtils.PI), random.nextFloat(0.01f,0.08f), inhabited, this));

            distance+=300;
        }
        starGate=new StarGate(distance, random.nextFloat(-MathUtils.PI,MathUtils.PI));
        star=new Star(starTextures.get(random.nextInt(starTextures.size())));
        positionX=x;
        positionY=y;
        if(inhabitedPlanetCount>0)
        {
            Ware.WARES.forEach(ware -> {
                float factor = 1 + random.nextFloat(-0.5f, 0.5f);
                priceFactors.put(ware, factor);
            });
            planets.forEach(planet -> {
                if(planet.isInhabited) {
                    Ware.WARES.forEach(ware -> {
                        int randomWareAmount = random.nextInt(10, 500);
                        int basePrice = Ware.BASE_PRICES.get(ware);
                        float priceMultiplier = 1;
                        if (randomWareAmount < 100) {
                            priceMultiplier = 1.2f;
                        } else if (randomWareAmount < 250) {
                            priceMultiplier = 1.1f;
                        } else if (randomWareAmount > 750) {
                            priceMultiplier = 0.8f;
                        } else if (randomWareAmount > 500) {
                            priceMultiplier = 0.9f;
                        }
                        int finalPrice = (int) (basePrice * priceMultiplier * priceFactors.get(ware));
                        planet.warePrices.put(ware, finalPrice);
                        planet.wareAmounts.put(ware, randomWareAmount);
                    });
                }
            });
        }
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer)
    {
        star.draw(spriteBatch, shapeRenderer);
        planets.forEach(planet -> planet.draw(spriteBatch,shapeRenderer));
        starGate.draw(spriteBatch);
        ships.forEach(npcPilot -> npcPilot.draw(spriteBatch,shapeRenderer));
        planets.forEach(planet -> planet.drawName(spriteBatch));
        projectiles.forEach(projectile -> projectile.render(spriteBatch));
    }

    public void update()
    {
        float dt=Gdx.graphics.getDeltaTime();
        planets.forEach(planet -> planet.update(dt));
        ships.forEach(npcPilot -> {
            npcPilot.work(dt);
            if(npcPilot.canJump)
            {
                npcPilot.canJump=false;
                shipsToTransfer.add(npcPilot);
            }
        });
        ships.removeAll(shipsToTransfer);
        shipsToTransfer.forEach(npcPilot -> {
            npcPilot.navigatingTo.ships.add(npcPilot);
            npcPilot.currentSystem=npcPilot.navigatingTo;
            if(SpaceGame.debugDraw)
                System.out.println("Jumped to "+npcPilot.currentSystem.star.name);
            npcPilot.setPosition(npcPilot.navigatingTo.starGate.x,npcPilot.navigatingTo.starGate.y);
            npcPilot.canJump=false;
        });
        shipsToTransfer.clear();
        starGate.update(dt);

        ArrayList<NPCPilot> npcPilotsToRemove=new ArrayList<>(ships.size());
        Array<Projectile> toRemove=new Array<>(projectiles.size);
        projectiles.forEach(projectile -> {
            projectile.update();
            for (NPCPilot ship : ships) {
                if(projectile.shooter!=ship) {

                    if ((projectile.target==null ||projectile.target==ship) && ship.area.overlaps(projectile.area)) {
                        ship.integrity -= projectile.damage;
                        ship.onProjectileImpact(projectile);
                        if (ship.integrity <= 0) {
                            npcPilotsToRemove.add(ship);
                        }
                        toRemove.add(projectile);
                    } else  {
                        Vector2 backVector=new Vector2(projectile.x+projectile.speed.x,projectile.y+projectile.speed.y);
                        if(ship.area.contains(backVector))
                        {
                            ship.integrity -= projectile.damage;
                            ship.onProjectileImpact(projectile);
                            if (ship.integrity <= 0) {
                                npcPilotsToRemove.add(ship);
                            }
                            toRemove.add(projectile);
                        }
                    }
                }
                PlayerShip playerShip = SpaceGame.INSTANCE.playerShip;
                if(playerShip!=null && playerShip.currentStarSystem==projectile.shooter.getCurrentSystem()) {
                    if (projectile.area.overlaps(playerShip.area) && projectile.shooter != playerShip) {
                        playerShip.integrity -= projectile.damage;
                        toRemove.add(projectile);
                        if (playerShip.integrity <= 0) {
                            SpaceGame.INSTANCE.playerShip = null;
                            Screen screen= SpaceGame.INSTANCE.getScreen();
                            if(screen instanceof SystemScreen systemScreen)
                            {
                                systemScreen.playerShip=null;
                            }
                        }
                    }
                }
            }
            if(Vector2.dst(projectile.x,projectile.y,0,0)>10000)
            {
                toRemove.add(projectile);
            }
        });
        projectiles.removeAll(toRemove,true);
        ships.removeAll(npcPilotsToRemove);
    }

    public String getStarName()
    {
        return star.name;
    }
}
