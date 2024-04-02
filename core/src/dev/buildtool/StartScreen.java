package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;

public class StartScreen extends ScreenAdapter {
    private final Stage stage;


    public StartScreen(SpaceGame game) {
        stage=new Stage();
        Table table = new Table();
        table.setFillParent(true);
        Button button=new TextButton("Start new game",game.skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.initialize();
                lable:
                for (StarSystem system : game.starSystems) {
                    for (Planet planet : system.planets) {
                        if(planet.isInhabited){
                            game.playerShip=new StarShip(0,0,0,SpaceGame.INSTANCE.redStarshipTexture, system);
                            game.setScreen(new PlanetScreen(system, planet,game.playerShip));
                            game.updateWorld=true;
                            break lable;
                        }
                    }
                }
            }
        });
        stage.addActor(table);
        table.add(button);
        TextButton quit=new TextButton("Quit game",game.skin);
        quit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        table.row();
        table.add(quit);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height,true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }
}
