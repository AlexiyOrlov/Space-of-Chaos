package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import dev.buildtool.projectiles.DestructibleProjectile;
import dev.buildtool.projectiles.Projectile;

public class StarSystem implements SaveData{
    public ArrayList<Planet> planets=new ArrayList<>(7);
    public Star star;
    public HashMap<Ware,Float> priceFactors=new HashMap<>(Ware.WARES.size());
    public int positionX,positionY;
    public ArrayList<Ship> ships=new ArrayList<>();
    public ArrayList<Ship> shipsToTransfer=new ArrayList<>();
    public StarGate starGate;
    public ArrayList<Projectile> projectiles=new ArrayList<>();
    ArrayList<Container> itemContainers=new ArrayList<>();
    public final ArrayList<OneShotAnimation> animations=new ArrayList<>();
    public int id;
    public boolean occupied;
    public static final float HIGHEST_PRICE_MULTIPLIER=1.2f,HIGH_PRICE_MULTIPLIER=1.1f,LOWEST_PRICE_MULTIPLIER=0.8f, LOW_PRICE_MULTIPLIER =0.9f;

    public StarSystem() {
    }

    public StarSystem(ArrayList<Texture> planetTextures, ArrayList<Texture> starTextures, int x, int y, boolean occupiedByAI, int id) {
        this.id=id;
        occupied=occupiedByAI;
        Random random = SpaceOfChaos.random;
        star=new Star(starTextures.get(random.nextInt(starTextures.size())));
        int inhabitedPlanetCount=0;
        int distance=500;
        for (int i = 0; i < random.nextInt(3,7); i++) {
            if(occupiedByAI){
                planets.add(new Planet(planetTextures.get(random.nextInt(planetTextures.size())), distance, random.nextFloat(-MathUtils.PI, MathUtils.PI), random.nextFloat(0.01f, 0.08f), this, Planet.Kind.OCCUPIED, i));
            }
            else {
                boolean inhabited = false;
                if (inhabitedPlanetCount < 3) {
                    inhabited = random.nextBoolean();
                    if (inhabited)
                        inhabitedPlanetCount++;
                }
                planets.add(new Planet(planetTextures.get(random.nextInt(planetTextures.size())), distance, random.nextFloat(-MathUtils.PI, MathUtils.PI), random.nextFloat(0.01f, 0.08f), this, inhabited? Planet.Kind.INHABITED: Planet.Kind.UNINHABITED, i));
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
                            priceMultiplier = HIGHEST_PRICE_MULTIPLIER;
                        } else if (randomWareAmount < 250) {
                            priceMultiplier = HIGH_PRICE_MULTIPLIER;
                        } else if (randomWareAmount > 750) {
                            priceMultiplier = LOWEST_PRICE_MULTIPLIER;
                        } else if (randomWareAmount > 500) {
                            priceMultiplier = LOW_PRICE_MULTIPLIER;
                        }
                        int finalPrice = (int) (basePrice * priceMultiplier * priceFactors.get(ware));
                        planet.warePrices.put(ware, finalPrice);
                        planet.wareAmounts.put(ware, randomWareAmount);
                    });
                }
            });
        }
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer, float deltaTime)
    {
        SpriteBatch uiBatch= SpaceOfChaos.INSTANCE.uiBatch;
        uiBatch.begin();
        uiBatch.draw(SpaceOfChaos.INSTANCE.skyTexture, 0,0,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiBatch.end();
        star.draw(spriteBatch, shapeRenderer);
        planets.forEach(planet -> planet.draw(spriteBatch,shapeRenderer));
        starGate.draw(spriteBatch);
        projectiles.forEach(projectile -> projectile.render(spriteBatch));
        ships.forEach(ship -> ship.draw(spriteBatch,shapeRenderer));
        ArrayList<OneShotAnimation> animsToRemove=new ArrayList<>();
        animations.forEach(oneShotAnimation -> {
            oneShotAnimation.update(deltaTime,spriteBatch);
            if(oneShotAnimation.time>=1)
            {
                animsToRemove.add(oneShotAnimation);
            }
        });
        animations.removeAll(animsToRemove);
        spriteBatch.begin();
        itemContainers.forEach(container -> {
            Functions.drawRotatedScaled(spriteBatch, SpaceOfChaos.INSTANCE.containerTexture, container.x,container.y,container.rotation,0.5f);
            container.rotation+=1;
        });
        spriteBatch.end();
        planets.forEach(planet -> planet.drawName(spriteBatch));
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
                if (SpaceOfChaos.debugDraw)
                    System.out.println("Jumped to " + npcPilot.currentSystem.star.name);
                npcPilot.setPosition(npcPilot.navigatingTo.starGate.x, npcPilot.navigatingTo.starGate.y);
                npcPilot.afterJump();
            }
        });
        shipsToTransfer.clear();
        starGate.update(dt);

        ArrayList<Projectile> toAdd=new ArrayList<>();
        ArrayList<Ship> shipsToRemove=new ArrayList<>(ships.size());
        ArrayList<Projectile> toRemove=new ArrayList<>(projectiles.size());
        ArrayList<Projectile> otherProjectiles=new ArrayList<>(projectiles);
        projectiles.forEach(projectile -> {
            otherProjectiles.remove(projectile);
            projectile.update(dt, toAdd, toRemove);
            if(projectile instanceof DestructibleProjectile destructibleProjectile) {
                for (Projectile otherProjectile : otherProjectiles) {
                    if(projectile.shooter!=otherProjectile.shooter && otherProjectile.area.overlaps(projectile.area) )
                    {
                        destructibleProjectile.damage(otherProjectile.damage);
                        if(destructibleProjectile.getIntegrity()<=0)
                        {
                            toRemove.add(projectile);
                            projectile.onDestroyed(this);
                        }
                        toRemove.add(otherProjectile);
                        otherProjectile.onDestroyed(this);
                        break;
                    }
                }
            }
        });
        for (Ship ship : ships) {
            for (Projectile projectile : projectiles) {
                if(projectile.shooter!=ship)
                {
                    if((projectile.target==null || (projectile.target==ship)) && projectile.validTargets.test(ship) && (projectile.target==null || projectile.shooter.getCurrentSystem()==projectile.target.getCurrentSystem()))
                    {
                        if(ship.overlaps(projectile.area)) {
                            ship.damage(projectile.damage);
                            if (ship.getIntegrity() <= 0) {
                                shipsToRemove.add(ship);
                                if(ship instanceof PlayerShip)
                                    SpaceOfChaos.INSTANCE.playerShip=null;
                            }
                            toRemove.add(projectile);
                            projectile.onDestroyed(this);
                        }else {
                            Vector2 backVector = new Vector2(projectile.x + projectile.velocity.x, projectile.y + projectile.velocity.y);
                            if (ship.contains(backVector)) {
                                ship.damage(projectile.damage);
                                ship.onProjectileImpact(projectile);
                                if (ship.getIntegrity() <= 0) {
                                    shipsToRemove.add(ship);
                                    if(ship instanceof PlayerShip)
                                        SpaceOfChaos.INSTANCE.playerShip=null;
                                }
                                toRemove.add(projectile);
                                projectile.onDestroyed(this);
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
        projectiles.removeAll(toRemove);
        ships.removeAll(shipsToRemove);
        projectiles.addAll(toAdd);
    }

    public String getStarName()
    {
        return star.name;
    }

    @Override
    public Map<String, Object> getData() {
        HashMap<String,Object> data=new HashMap<>();
        for (int i = 0; i < itemContainers.size(); i++) {
            Container container=itemContainers.get(i);
            data.put("container "+i,container.getData());
        }
        data.put("containers",itemContainers.size());
        data.put("occupied",occupied);
        for (int i = 0; i < planets.size(); i++) {
            Planet planet=planets.get(i);
            data.put("planet "+i,planet.getData());
        }
        data.put("planets",planets.size());
        data.put("x",positionX);
        data.put("y",positionY);
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile projectile=projectiles.get(i);
            data.put("projectile "+i,projectile.getData());
        }
        data.put("projectiles",projectiles.size());
        for (int i = 0; i < ships.size(); i++) {
            Ship next=ships.get(i);
            if(next instanceof NPCPilot npcPilot)
            {
                data.put("ship "+i,npcPilot.getData());
            }
        }
        data.put("ships",ships.size());
        data.put("star",star.getData());
        data.put("star gate",starGate.getData());
        data.put("id",id);
        return data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load(Map<String, Object> data) {
        id= (int) data.get("id");
        int containers= (int) data.get("containers");
        for (int i = 0; i < containers; i++) {
            Container container=new Container();
            container.load((Map<String, Object>) data.get("container "+i));
            itemContainers.add(container);
        }
        occupied= (boolean) data.get("occupied");
        int planetCount= (int) data.get("planets");
        for (int i = 0; i < planetCount; i++) {
            Planet planet=new Planet();
            planet.load((Map<String, Object>) data.get("planet "+i));
            planets.add(planet);
        }
        positionX= (int) data.get("x");
        positionY= (int) data.get("y");
        int shipCount= (int) data.get("ships");
        //npc loading
        for (int i = 0; i < shipCount; i++) {
            String next="ship "+i;
            if(data.containsKey(next))
            {
                NPCPilot npcPilot=new NPCPilot();
                npcPilot.load((Map<String, Object>) data.get(next));
                ships.add(npcPilot);
            }
        }
        //projectile loading
        int projectileCount= (int) data.get("projectiles");
        for (int i = 0; i < projectileCount; i++) {
            Projectile projectile=new Projectile();
            projectile.load((Map<String, Object>) data.get("projectile "+i));
            projectiles.add(projectile);
        }
        star=new Star();
        star.load((Map<String, Object>) data.get("star"));
        starGate=new StarGate();
        starGate.load((Map<String, Object>) data.get("star gate"));
    }
}
