package dev.buildtool;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
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
    private float rotation=SpaceGame.random.nextInt(360);

    public NPCPilot(Planet currentlyLandedOn) {
        this.currentlyLandedOn = currentlyLandedOn;
    }

    public void work(float deltaTime)
    {
        if(landed)
        {
            if(timeSpentOnPlanet>=secondsOfRest)
            {
                //take off
                x=currentlyLandedOn.x;
                y=currentlyLandedOn.y;
                landed=false;
            }
            else {
                timeSpentOnPlanet+=deltaTime;
            }
        }
        else {
            StarSystem currentSystem=currentlyLandedOn.starSystem;
            List<StarSystem> closestSystems= SpaceGame.INSTANCE.starSystems.stream().filter(starSystem -> Vector2.dst(starSystem.positionX,starSystem.positionY,currentSystem.positionX,currentSystem.positionY)<= engine.jumpDistance).collect(Collectors.toList());

        }
    }

    public void draw(SpriteBatch spriteBatch)
    {
        if(!landed) {
            spriteBatch.begin();
            Functions.drawRotated(spriteBatch, hull.look,  x, y, rotation);
            spriteBatch.end();
        }
    }
}
