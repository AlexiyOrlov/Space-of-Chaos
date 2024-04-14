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

import java.util.Random;

public class StartScreen extends ScreenAdapter {
    private final Stage stage;


    public StartScreen(SpaceOfChaos game) {
        stage=new Stage();
        Table table = new Table();
        table.setFillParent(true);
        Random random= SpaceOfChaos.random;
        Button button=new TextButton("Start new game",game.skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.initialize();
                StarSystem next=game.starSystems.get(random.nextInt(game.starSystems.size()));
                lable:
                while (true) {
                    for (Planet planet : next.planets) {
                        if(planet.kind== Planet.Kind.INHABITED){
                            PlayerShip playerShip = new PlayerShip(0, 0, 0, next);
                            game.playerShip= playerShip;
                            playerShip.money=1000;
                            game.setScreen(new PlanetScreen2(next, planet,playerShip));
                            next.ships.add(playerShip);
                            game.playerShip.x=planet.x;
                            game.playerShip.y=planet.y;
                            break lable;
                        }
                    }
                    next=game.starSystems.get(random.nextInt(game.starSystems.size()));
                }
            }
        });
        stage.addActor(table);
        table.add(button);
        table.row();
        TextButton load=new TextButton("Load game",game.skin);
        load.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SpaceOfChaos.INSTANCE.loadGame();
                if(SpaceOfChaos.INSTANCE.playerShip.isLanded())
                {
                    System.out.println("Landed");
                }
                else {
                    game.setScreen(new SystemScreen(SpaceOfChaos.INSTANCE.playerShip.currentStarSystem,-1,-1));
                }
            }
        });
        table.add(load);
        table.row();
        TextButton quit=new TextButton("Quit game",game.skin);
        quit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        table.row();
        table.add(quit);
        table.row();
        TextButton shaderTest=new TextButton("Test shader",game.skin);
        shaderTest.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new ShaderTest());
            }
        });
        table.add(shaderTest);
        table.row();
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
