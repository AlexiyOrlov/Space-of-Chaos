package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

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
                Path savePath=Path.of(SpaceOfChaos.INSTANCE.dataDir,"Space of Chaos","Saves");
                try {
                    var files=Files.walk(savePath).sorted(Comparator.comparingLong(path -> path.toFile().lastModified())).filter(path -> path.toString().endsWith(".yaml")).toList();
                    Skin skin=SpaceOfChaos.INSTANCE.skin;
                    Table forPane=new Table();
                    ScrollPane scrollPane=new ScrollPane(forPane,skin);
                    Dialog dialog=new Dialog("List of saves",skin);
                    files.forEach(path -> {
                        Label label=new Label(path.getFileName().toString(),skin);
                        TextButton load=new TextButton("Load",skin);
                        load.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                SpaceOfChaos.INSTANCE.loadGame(path);
                                if(SpaceOfChaos.INSTANCE.playerShip.isLanded())
                                {
                                    System.out.println("Landed");
                                }
                            }
                        });

                        TextButton delete=new TextButton("Delete",skin);
                        delete.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                Dialogs.showOptionDialog(stage,"Confirm deletion","Delete this save?", Dialogs.OptionDialogType.YES_NO,new OptionDialogAdapter(){
                                    @Override
                                    public void yes() {
                                        try {
                                            Files.delete(path);
                                            forPane.removeActor(label);
                                            forPane.removeActor(load);
                                            forPane.removeActor(delete);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                            }
                        });
                        forPane.add(label);
                        try {
                            BasicFileAttributes attributes=  Files.readAttributes(path, BasicFileAttributes.class);
                            DateFormat dateFormat=new SimpleDateFormat("dd/MM", Locale.getDefault());
                            Label date=new Label("      On "+dateFormat.format(attributes.lastModifiedTime().toMillis()),skin);
                            forPane.add(date);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        forPane.add(load);
                        forPane.add(delete);
                        forPane.row();
                    });
                    TextButton cancel=new TextButton("Cancel",skin);
                    dialog.button(cancel);
                    dialog.add(scrollPane);
                    dialog.show(stage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
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
        ScreenUtils.clear(Color.GRAY);
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
