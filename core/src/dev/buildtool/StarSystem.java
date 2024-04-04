package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

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
    public StarSystem(ArrayList<Texture> planetTextures,ArrayList<Texture> starTextures,int x,int y) {
        this.planets = new ArrayList<>(7);
        Random random = SpaceGame.random;
        starGate=new StarGate(400, random.nextFloat(-MathUtils.PI,MathUtils.PI));
        int inhabitedPlanetCount=0;
        int distance=600;
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
                            priceMultiplier = 1.5f;
                        } else if (randomWareAmount < 250) {
                            priceMultiplier = 1.25f;
                        } else if (randomWareAmount > 750) {
                            priceMultiplier = 0.5f;
                        } else if (randomWareAmount > 500) {
                            priceMultiplier = 0.75f;
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
    }

    public void update()
    {
        float dt=Gdx.graphics.getDeltaTime();
        planets.forEach(planet -> planet.update(dt));
        ships.forEach(npcPilot -> {
            npcPilot.work(dt);
            if(npcPilot.canJump)
            {
                shipsToTransfer.add(npcPilot);
                npcPilot.canJump=false;
            }
        });
        ships.removeAll(shipsToTransfer);
        shipsToTransfer.forEach(npcPilot -> {
            npcPilot.navigatingTo.ships.add(npcPilot);
            npcPilot.currentSystem=npcPilot.navigatingTo;
            System.out.println("Jumped to "+npcPilot.currentSystem.star.name);
            npcPilot.setPosition(npcPilot.navigatingTo.starGate.x,npcPilot.navigatingTo.starGate.y);
            npcPilot.canJump=false;
        });
        shipsToTransfer.clear();
        starGate.update(dt);
    }

    public String getStarName()
    {
        return star.name;
    }
}
