package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class SystemScreen extends ScreenAdapter {

    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer,uiShapeRenderer;
    ArrayList<Planet> planets;
    Star star;
    Camera camera;
    Viewport viewport;
    StarShip playerShip;
    StarSystem starSystem;
    Rectangle viewportBounds;

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
        viewportBounds=new Rectangle(0,0,viewport.getScreenWidth(),viewport.getScreenHeight());
        uiShapeRenderer=SpaceGame.INSTANCE.uiShapeRenderer;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        if(playerShip!=null)
            playerShip.update(delta, viewport);

        starSystem.draw(spriteBatch, shapeRenderer);
        if(playerShip!=null)
            playerShip.draw(spriteBatch,shapeRenderer );

        if(playerShip!=null) {
            camera.position.set(playerShip.x, playerShip.y, 0);
//            camera.up.set(0, 1, 0);
//            camera.rotate(Vector3.Z, playerShip.rotation);
        }

        if(playerShip!=null) {
            uiShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            uiShapeRenderer.setColor(Color.SCARLET);
            float ip = (float) playerShip.integrity / playerShip.hull.integrity;
            float height=ip * Gdx.graphics.getBackBufferHeight()-200;
            uiShapeRenderer.rect((float) Gdx.graphics.getBackBufferWidth() - 60-34, (Gdx.graphics.getBackBufferHeight()-height)/2,60 , height);
            uiShapeRenderer.end();
        }
        final Vector2 starPos=new Vector2(0,0);
        spriteBatch.begin();
        if(!camera.frustum.pointInFrustum(0,0,0))
            drawWaypoint(starPos, SpaceGame.INSTANCE.starIcon);
        planets.forEach(planet -> {
            if(!camera.frustum.pointInFrustum(planet.x,planet.y,0)) {
                if (planet.isInhabited) {
                    drawWaypoint(planet.x,planet.y, SpaceGame.INSTANCE.inhabitedPlanetIcon);
                } else {
                    drawWaypoint(planet.x,planet.y, SpaceGame.INSTANCE.uninhabitedPlanetIcon);
                }
            }
        });
        if(!camera.frustum.pointInFrustum(starSystem.starGate.x,starSystem.starGate.y,0))
        {
            drawWaypoint(starSystem.starGate.x,starSystem.starGate.y, SpaceGame.INSTANCE.stargateIcon);
        }
        spriteBatch.end();
    }

    private void drawWaypoint(float tox,float toy,Texture icon)
    {
        if(playerShip!=null) {
            SpriteBatch uibatch = SpaceGame.INSTANCE.uiBatch;
            float xdist = tox - playerShip.x;
            float ydist = toy - playerShip.y;
            int backBufferWidth = Gdx.graphics.getBackBufferWidth();
            int backBufferHeight = Gdx.graphics.getBackBufferHeight();

            float halfWidth = backBufferWidth / 2;
            float halfHeight = backBufferHeight / 2;
            float rx;
            float ry;
            Vector2 uiCoords = viewport.project(new Vector2(tox, toy));
            if (Math.abs(xdist) > halfWidth) {
                if (xdist > 0)
                    rx = backBufferWidth - icon.getWidth();
                else
                    rx = 0;
            } else {
                rx = uiCoords.x;
            }

            if (Math.abs(ydist) > halfHeight) {
                if (ydist > 0) {
                    ry = backBufferHeight - icon.getHeight();
                } else
                    ry = 0;
            } else
                ry = uiCoords.y;


            uibatch.begin();
            uibatch.draw(icon, rx, ry);
            uibatch.end();
        }
    }

    private void drawWaypoint(Vector2 to, Texture icon) {
        if(playerShip!=null) {
            SpriteBatch uiBatch = SpaceGame.INSTANCE.uiBatch;
            final Vector2 playerPos = new Vector2(playerShip.x, playerShip.y);
            int backBufferWidth = Gdx.graphics.getBackBufferWidth();
            Vector2 halfWidth = (new Vector2(backBufferWidth / 2, 0));
            int backBufferHeight = Gdx.graphics.getBackBufferHeight();
            Vector2 halfHeight = (new Vector2(0, backBufferHeight / 2));
            float x = 0;
            float y = 0;
            if (halfWidth.x < Math.abs(playerPos.x)) {
                if (playerPos.x < to.x) {
                    x += backBufferWidth - icon.getWidth();
                }
            } else {
                x = -playerPos.x + backBufferWidth / 2 - icon.getWidth() / 2;
            }
            if (halfHeight.y < Math.abs(playerPos.y)) {
                if (playerPos.y < to.y)
                    y += backBufferHeight - icon.getHeight();
            } else {
                y = -playerPos.y + backBufferHeight / 2 - icon.getHeight() / 2;
            }

            uiBatch.begin();
            uiBatch.draw(icon, x, y);
            uiBatch.end();
        }
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
