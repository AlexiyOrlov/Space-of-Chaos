package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class HelpScreen extends ScreenAdapter {
    Stage stage;
    public HelpScreen(StarSystem starSystem) {
        Viewport viewport=new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        stage=new Stage(viewport);
        Skin skin=SpaceOfChaos.INSTANCE.skin;
        Table content=new Table();
        content.setFillParent(true);
        content.add(new Label("Move - WASD",skin));
        content.row();
        content.add(new Label("Toggle target mode - Tab",skin));
        content.row();
        content.add(new Label("Hire mercenary - press R, then click on the mercenary",skin));
        content.row();
        content.add(new Label("To travel to other systems use star gate",skin));
        content.row();
        TextButton close=new TextButton("Close",skin);
        close.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SpaceOfChaos.INSTANCE.setScreen(new SystemScreen(starSystem));
            }
        });
        content.add(close);
        content.row();
        stage.addActor(content);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.GRAY);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }
}
