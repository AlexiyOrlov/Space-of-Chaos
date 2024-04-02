package dev.buildtool;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SystemScreen extends ScreenAdapter {

    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer;
    Array<Planet> planets;
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

        spriteBatch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        starSystem.draw(spriteBatch, shapeRenderer);
        playerShip.draw(spriteBatch);
        spriteBatch.end();
        shapeRenderer.end();

		camera.position.set(playerShip.x,playerShip.y,0);
		camera.up.set(0,1,0);
		camera.rotate(Vector3.Z,playerShip.rotation);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }
}
