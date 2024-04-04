package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

public class SystemScreen extends ScreenAdapter {

    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    ArrayList<Planet> planets;
    Star star;
    Camera camera;
    Viewport viewport;
    StarShip playerShip;
    StarSystem starSystem;

    public SystemScreen(StarSystem starSystem, float xForPlayer,float yForPlayer) {
        SpaceGame spaceGame=SpaceGame.INSTANCE;
        this.spriteBatch = spaceGame.batch;
        this.starSystem = starSystem;
        this.planets = this.starSystem.planets;
        this.star = this.starSystem.star;
        playerShip=spaceGame.playerShip;
        playerShip.x=xForPlayer;
        playerShip.y=yForPlayer;
        camera=new OrthographicCamera();
		viewport=new ScreenViewport(camera);
		viewport.apply();
        shapeRenderer=spaceGame.shapeRenderer;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        playerShip.update(delta);

        starSystem.draw(spriteBatch, shapeRenderer);
        playerShip.draw(spriteBatch,shapeRenderer );

		camera.position.set(playerShip.x,playerShip.y,0);
		camera.up.set(0,1,0);
		camera.rotate(Vector3.Z,playerShip.rotation);



        final Vector2 starPos=new Vector2(0,0);
        final Vector2 playerPos=new Vector2(playerShip.x,playerShip.y);
        Vector2 lowerLeftCorner=viewport.unproject(new Vector2(0,0));
        Vector2 upperLeftCorner=viewport.unproject(new Vector2(0,Gdx.graphics.getBackBufferHeight()-30));
        Vector2 rightLower=viewport.unproject(new Vector2(Gdx.graphics.getBackBufferWidth(),0));
        Vector2 rightUpper=viewport.unproject(new Vector2(Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight()));

        spriteBatch.begin();
        Vector2 left=lineLineIntersection(starPos,playerPos,lowerLeftCorner,upperLeftCorner);
        spriteBatch.draw(SpaceGame.INSTANCE.starIcon, left.x,left.y);

        Vector2 right=lineLineIntersection(starPos,playerPos,rightLower,rightUpper);
        spriteBatch.draw(SpaceGame.INSTANCE.starIcon, right.x,right.y);

        Vector2 bottom=lineLineIntersection(starPos,playerPos,lowerLeftCorner,rightLower);
        spriteBatch.draw(SpaceGame.INSTANCE.starIcon, bottom.x,bottom.y);

        Vector2 top=lineLineIntersection(starPos,playerPos,upperLeftCorner,rightUpper);
        spriteBatch.draw(SpaceGame.INSTANCE.starIcon, top.x,top.y);
        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }

    static Vector2 lineLineIntersection(Vector2 X1, Vector2 Y1, Vector2 X2, Vector2 Y2)
    {
        // Line AB represented as a1x + b1y = c1
        float a1 = Y1.y - X1.y;
        float b1 = X1.x - Y1.x;
        float c1 = a1*(X1.x) + b1*(X1.y);

        // Line CD represented as a2x + b2y = c2
        float a2 = Y2.y - X2.y;
        float b2 = X2.x - Y2.x;
        float c2 = a2*(X2.x)+ b2*(X2.y);

        float determinant = a1*b2 - a2*b1;

        if (determinant == 0)
        {
            // The lines are parallel. This is simplified
            // by returning a pair of FLT_MAX
            return new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
        }
        else
        {
            float x = (b2*c1 - b1*c2)/determinant;
            float y = (a1*c2 - a2*c1)/determinant;
            return new Vector2(x, y);
        }
    }
}
