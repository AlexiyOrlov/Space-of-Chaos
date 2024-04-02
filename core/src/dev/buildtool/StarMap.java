package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class StarMap extends ScreenAdapter {

    private final Array<StarSystem> starSystems;
    private final Stage stage;
    private final StarSystem currentStarSystem;
    private final float shipX;
    private final float shipY;
    private final Viewport viewport;
    private final Camera camera;

    public StarMap(StarSystem currentSystem,StarShip starShip) {
        starSystems=SpaceGame.INSTANCE.starSystems;
        viewport = new ScreenViewport();
        camera=viewport.getCamera();
        stage=new Stage(viewport);
        starSystems.forEach(starSystem -> {
            Image image=new Image(SpaceGame.INSTANCE.starIcon);
            image.setX(starSystem.positionX);
            image.setY(starSystem.positionY);
            stage.addActor(image);
            if(starShip.currentStarSystem==starSystem)
            {
                viewport.getCamera().position.x+= starSystem.positionX;
                viewport.getCamera().position.y+=starSystem.positionY;
            }
        });
        viewport.getCamera().position.x-= (float) Gdx.graphics.getBackBufferWidth() /2;
        viewport.getCamera().position.y-= (float) Gdx.graphics.getBackBufferHeight() /2;
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

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter(){
            boolean dragging;
            int prevX,prevY;
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                prevX=screenX;
                prevY=screenY;
                dragging=true;
                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                dragging=false;
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if(dragging) {
                    float distX = prevX - screenX;
                    float distY = prevY - screenY;
                    camera.position.x+=distX;
                    camera.position.y-=distY;
                    prevX=screenX;
                    prevY=screenY;
                }
                return true;
            }
        });
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
