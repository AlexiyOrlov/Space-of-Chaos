package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
    private StarShip starShip;
    private ShapeRenderer shapeRenderer;

    public StarMap(StarSystem currentSystem,StarShip starShip) {
        starSystems=SpaceGame.INSTANCE.starSystems;
        viewport = new ScreenViewport();
        this.starShip=starShip;
        shapeRenderer=new ShapeRenderer();
        camera=viewport.getCamera();
        stage=new Stage(viewport);
        Texture starIcon = SpaceGame.INSTANCE.starIcon;
        starSystems.forEach(starSystem -> {
            Image image=new Image(starIcon);
            image.setX(starSystem.positionX- (float) starIcon.getWidth() /2);
            image.setY(starSystem.positionY- (float) starIcon.getHeight() /2);
            stage.addActor(image);
            if(starShip.currentStarSystem==starSystem)
            {
                viewport.getCamera().position.x+= starSystem.positionX;
                viewport.getCamera().position.y+=starSystem.positionY;
                BitmapFont font = SpaceGame.INSTANCE.bitmapFont;
                Label cs=new Label("[YELLOW]Current system",new Label.LabelStyle(font, Color.WHITE));
                GlyphLayout glyphLayout=new GlyphLayout(font,"Current system");
                cs.setPosition(starSystem.positionX- glyphLayout.width/2, starSystem.positionY+30);
                stage.addActor(cs);
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
        SpriteBatch spriteBatch=SpaceGame.INSTANCE.uiBatch;
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();
        if(Gdx.input.isKeyJustPressed(Input.Keys.M))
        {
            SpaceGame.INSTANCE.setScreen(new SystemScreen(currentStarSystem,shipX,shipY));
        }
        GlyphLayout glyphLayout=new GlyphLayout(SpaceGame.INSTANCE.bitmapFont,"Galactic map");
        spriteBatch.begin();
        SpaceGame.INSTANCE.bitmapFont.draw(spriteBatch,"Galactic map", (float) Gdx.graphics.getBackBufferWidth() /2- glyphLayout.width/2,Gdx.graphics.getBackBufferHeight()-30);
        spriteBatch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.GREEN);
        int shipJumpDistance = starShip.engine.jumpDistance;
        shapeRenderer.ellipse(currentStarSystem.positionX-shipJumpDistance,currentStarSystem.positionY-shipJumpDistance, shipJumpDistance*2, shipJumpDistance*2);
        shapeRenderer.end();
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
