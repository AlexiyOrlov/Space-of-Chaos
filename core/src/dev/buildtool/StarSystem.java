package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

public class StarSystem {
    public Array<Planet> planets;
    public Star star;
    public int positionX,positionY;
    public StarSystem(ArrayList<Texture> planetTextures,ArrayList<Texture> starTextures,int x,int y) {
        this.planets = new Array<>(3);
        int inhabitedPlanetCount=0;
        int distance=600;
        for (int i = 0; i < 6; i++) {
            boolean inhabited=false;
            if(inhabitedPlanetCount<3)
            {
                inhabited=SpaceGame.random.nextBoolean();
                if(inhabited)
                    inhabitedPlanetCount++;
            }
            planets.add(new Planet(planetTextures.get(SpaceGame.random.nextInt(planetTextures.size())), distance, SpaceGame.random.nextFloat(-MathUtils.PI,MathUtils.PI), SpaceGame.random.nextFloat(0.01f,0.1f), inhabited));

            distance+=300;
        }
        star=new Star(starTextures.get(SpaceGame.random.nextInt(starTextures.size())));
        positionX=x;
        positionY=y;
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer)
    {
        star.draw(spriteBatch);
        planets.forEach(planet -> planet.draw(spriteBatch,shapeRenderer ));
    }

    public void update()
    {
        planets.forEach(Planet::update);
    }
}
