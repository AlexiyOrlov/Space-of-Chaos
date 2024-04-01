package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class StarMap extends ScreenAdapter {

    private final Array<StarSystem> starSystems;
    private final Stage stage;
    private StarSystem currentStarSystem;
    private float shipX,shipY;

    public StarMap(StarSystem currentSystem,StarShip starShip) {
        starSystems=SpaceGame.INSTANCE.starSystems;
        ScreenViewport viewport = new ScreenViewport();
        stage=new Stage(viewport);
        starSystems.forEach(starSystem -> {
            Image image=new Image(SpaceGame.INSTANCE.starIcon);
            image.setX(starSystem.positionX);
            image.setY(starSystem.positionY);
            stage.addActor(image);
        });
        viewport.getCamera().position.x-=Gdx.graphics.getBackBufferWidth()/2;
        viewport.getCamera().position.y-=Gdx.graphics.getBackBufferHeight()/2;
        currentStarSystem=currentSystem;
        shipX= starShip.x;
        shipY= starShip.y;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();
        if(Gdx.input.isKeyJustPressed(Input.Keys.M))
        {
            SpaceGame.INSTANCE.setScreen(new SystemScreen(currentStarSystem,shipX,shipY));
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height);
    }
}
