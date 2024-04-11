package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;

import dev.buildtool.test.ShockWave;

public class ShaderTest extends ScreenAdapter {
    private Stage stage;

    public ShaderTest() {
        stage=new Stage();
        Texture texture=new Texture(Gdx.files.internal("textures/sky.png"));
        stage.addActor(ShockWave.getInstance());

        Image image1 = new Image(texture);
        image1.setPosition(0,0);
        image1.setSize(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        image1.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                ShockWave.getInstance().start(x,y);
            }
        });

        ShockWave.getInstance().addActor(image1);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();
    }
}
