package dev.buildtool;

import com.badlogic.gdx.Gdx;
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
    public ArrayList<Ship> ships=new ArrayList<>();
    public ArrayList<Ship> shipsToTransfer=new ArrayList<>();
    public StarGate starGate;
    public Array<Projectile> projectiles;
    ArrayList<Container> itemContainers;
    public boolean occupied;
    public StarSystem(ArrayList<Texture> planetTextures, ArrayList<Texture> starTextures, int x, int y, boolean occupiedByAI) {
        occupied=occupiedByAI;
        projectiles=new Array<>();
        this.planets = new ArrayList<>(7);
        itemContainers=new ArrayList<>();
        Random random = SpaceGame.random;
        star=new Star(starTextures.get(random.nextInt(starTextures.size())));
        int inhabitedPlanetCount=0;
        int distance=500;
        for (int i = 0; i < random.nextInt(3,7); i++) {
            if(occupiedByAI){
                planets.add(new Planet(planetTextures.get(random.nextInt(planetTextures.size())), distance, random.nextFloat(-MathUtils.PI, MathUtils.PI), random.nextFloat(0.01f, 0.08f), this, Planet.Kind.OCCUPIED ));
            }
            else {
                boolean inhabited = false;
                if (inhabitedPlanetCount < 3) {
                    inhabited = random.nextBoolean();
                    if (inhabited)
                        inhabitedPlanetCount++;
                }
                planets.add(new Planet(planetTextures.get(random.nextInt(planetTextures.size())), distance, random.nextFloat(-MathUtils.PI, MathUtils.PI), random.nextFloat(0.01f, 0.08f), this, inhabited? Planet.Kind.INHABITED: Planet.Kind.UNINHABITED));
            }
            distance+=300;
        }
        starGate=new StarGate(distance, random.nextFloat(-MathUtils.PI,MathUtils.PI));
        positionX=x;
        positionY=y;
        if(inhabitedPlanetCount>0)
        {
            Ware.WARES.forEach(ware -> {
                float factor = 1 + random.nextFloat(-0.5f, 0.5f);
                priceFactors.put(ware, factor);
            });
            planets.forEach(planet -> {
                if(planet.kind== Planet.Kind.INHABITED) {
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
        SpriteBatch uiBatch=SpaceGame.INSTANCE.uiBatch;
        uiBatch.begin();
        uiBatch.draw(SpaceGame.INSTANCE.skyTexture, 0,0,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiBatch.end();
        star.draw(spriteBatch, shapeRenderer);
        planets.forEach(planet -> planet.draw(spriteBatch,shapeRenderer));
        starGate.draw(spriteBatch);
        ships.forEach(ship -> {
            if(ship instanceof NPCPilot npcPilot)
                npcPilot.draw(spriteBatch,shapeRenderer);
            else if(ship instanceof PlayerShip playerShip)
                playerShip.draw(spriteBatch,shapeRenderer);
        });
        spriteBatch.begin();
        itemContainers.forEach(container -> {
            Functions.drawRotatedScaled(spriteBatch,SpaceGame.INSTANCE.containerTexture, container.x,container.y,container.rotation,0.5f);
            container.rotation+=1;
        });
        spriteBatch.end();
        planets.forEach(planet -> planet.drawName(spriteBatch));
        projectiles.forEach(projectile -> projectile.render(spriteBatch));
    }

    public void update()
    {
        float dt=Gdx.graphics.getDeltaTime();
        planets.forEach(planet -> planet.update(dt));
        ships.forEach(ship -> {
            if(ship instanceof NPCPilot npcPilot) {
                npcPilot.work(dt);
                if (npcPilot.canJump) {
                    shipsToTransfer.add(ship);
                }
            }
        });
        ships.removeAll(shipsToTransfer);
        shipsToTransfer.forEach(ship -> {
            if(ship instanceof NPCPilot npcPilot) {
                npcPilot.navigatingTo.ships.add(ship);
                npcPilot.currentSystem = npcPilot.navigatingTo;
                if (SpaceGame.debugDraw)
                    System.out.println("Jumped to " + npcPilot.currentSystem.star.name);
                npcPilot.setPosition(npcPilot.navigatingTo.starGate.x, npcPilot.navigatingTo.starGate.y);
                npcPilot.afterJump();
            }
        });
        shipsToTransfer.clear();
        starGate.update(dt);

        ArrayList<Ship> shipsToRemove=new ArrayList<>(ships.size());
        Array<Projectile> toRemove=new Array<>(projectiles.size);
        projectiles.forEach(Projectile::update);
        for (Ship ship : ships) {
            for (Projectile projectile : projectiles) {
                if(projectile.shooter!=ship)
                {
                    if(projectile.target==null || (projectile.target==ship))
                    {
                        if(ship.overlaps(projectile.area)) {
                            ship.damage(projectile.damage);
                            if (ship.getIntegrity() <= 0) {
                                shipsToRemove.add(ship);
                                if(ship instanceof PlayerShip)
                                    SpaceGame.INSTANCE.playerShip=null;
                            }
                            projectile.onImpact();
                            toRemove.add(projectile);
                        }else {
                            Vector2 backVector = new Vector2(projectile.x + projectile.speed.x, projectile.y + projectile.speed.y);
                            if (ship.contains(backVector)) {
                                ship.damage(projectile.damage);
                                ship.onProjectileImpact(projectile);
                                if (ship.getIntegrity() <= 0) {
                                    shipsToRemove.add(ship);
                                    if(ship instanceof PlayerShip)
                                        SpaceGame.INSTANCE.playerShip=null;
                                }
                                projectile.onImpact();
                                toRemove.add(projectile);
                            }
                        }
                    }
                }
                if(Vector2.dst(projectile.x,projectile.y,0,0)>10000)
                {
                    toRemove.add(projectile);
                }
            }
        }
        projectiles.removeAll(toRemove,true);
        ships.removeAll(shipsToRemove);
    }

    public String getStarName()
    {
        return star.name;
    }
}
