package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class StarMap extends ScreenAdapter {

    private final ArrayList<StarSystem> starSystems;
    private final Stage stage;
    private final StarSystem currentStarSystem;
    private final float shipX;
    private final float shipY;
    private final Viewport viewport;
    private final Camera camera;
    private final PlayerShip playerShip;
    private final ShapeRenderer shapeRenderer;
    private final Image target;
    private StarSystem selectedStarSystem;
    private float targetAngle;

    public StarMap(StarSystem currentSystem, PlayerShip playerShip) {
        target=new Image(SpaceGame.INSTANCE.targetTexture);
        target.setOrigin(target.getWidth()/2,target.getHeight()/2);
        starSystems=SpaceGame.INSTANCE.starSystems;
        viewport = new ScreenViewport();
        this.playerShip = playerShip;
        shapeRenderer=new ShapeRenderer();
        camera=viewport.getCamera();
        stage=new Stage(viewport);
        target.setVisible(false);
        stage.addActor(target);
        Texture starIcon = SpaceGame.INSTANCE.starIcon;
        starSystems.forEach(starSystem -> {
            Image image=new Image(starIcon);
            image.setX(starSystem.positionX- (float) starIcon.getWidth() /2);
            image.setY(starSystem.positionY- (float) starIcon.getHeight() /2);
            stage.addActor(image);
            BitmapFont font = SpaceGame.INSTANCE.bitmapFont;
            if(playerShip.currentStarSystem==starSystem)
            {
                camera.position.x+= starSystem.positionX;
                camera.position.y+=starSystem.positionY;
                Label cs=new Label("Current system ("+starSystem.star.name+")",new Label.LabelStyle(font,starSystem.occupied?Color.GRAY: Color.YELLOW));
                GlyphLayout glyphLayout=new GlyphLayout(font,"Current system ("+starSystem.star.name+")");
                cs.setPosition(starSystem.positionX- glyphLayout.width/2, starSystem.positionY+30);
                stage.addActor(cs);
            }
            else {
                Label starName=new Label(starSystem.star.name,new Label.LabelStyle(font,starSystem.occupied?Color.GRAY : Color.YELLOW));
                GlyphLayout glyphLayout=new GlyphLayout(font,starSystem.star.name);
                starName.setPosition(starSystem.positionX -glyphLayout.width/2,starSystem.positionY+30);
                stage.addActor(starName);
            }
            image.addListener(new ClickListener(Input.Buttons.RIGHT){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if(starSystem!= playerShip.currentStarSystem) {
                        if (Vector2.dst(starSystem.positionX, starSystem.positionY, currentSystem.positionX, currentSystem.positionY) <= playerShip.getEngine().jumpDistance) {
                            if (!target.isVisible()) {
                                target.setPosition(starSystem.positionX - target.getWidth() / 2, starSystem.positionY - target.getHeight() / 2);
                                target.setVisible(true);
                                selectedStarSystem = starSystem;
                            } else {
                                if (starSystem == selectedStarSystem) {
                                    playerShip.currentStarSystem.ships.remove(playerShip);
                                    playerShip.currentStarSystem = starSystem;
                                    starSystem.ships.add(playerShip);
                                    SpaceGame.INSTANCE.setScreen(new SystemScreen(starSystem, starSystem.starGate.x, starSystem.starGate.y));
                                } else {
                                    selectedStarSystem = starSystem;
                                    target.setPosition(starSystem.positionX - target.getWidth() / 2, starSystem.positionY - target.getHeight() / 2);
                                }
                            }
                        } else {
                            target.setVisible(false);
                        }
                    }
                }
            });
        });
        camera.position.x-= (float) Gdx.graphics.getBackBufferWidth() /2;
        camera.position.y-= (float) Gdx.graphics.getBackBufferHeight() /2;
        currentStarSystem=currentSystem;
        shipX= playerShip.x;
        shipY= playerShip.y;
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
        glyphLayout.setText(SpaceGame.INSTANCE.bitmapFont, "Press 'M' to exit");
        SpaceGame.INSTANCE.bitmapFont.draw(spriteBatch,"Press 'M' to exit", (float) Gdx.graphics.getBackBufferWidth() /2- glyphLayout.width/2,Gdx.graphics.getBackBufferHeight()-50);
        spriteBatch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.setColor(Color.GREEN);
        int shipJumpDistance = playerShip.getEngine().jumpDistance;
        shapeRenderer.ellipse(currentStarSystem.positionX-shipJumpDistance,currentStarSystem.positionY-shipJumpDistance, shipJumpDistance*2, shipJumpDistance*2);
        shapeRenderer.end();
        target.rotateBy(targetAngle);
        targetAngle+= MathUtils.degreesToRadians*12;
        target.setRotation(targetAngle);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width,height);
    }

    @Override
    public void show() {

        InputMultiplexer inputMultiplexer=new InputMultiplexer();
        InputAdapter inputAdapter = new InputAdapter() {
            boolean dragging;
            int prevX, prevY;

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == 0) {
                    prevX = screenX;
                    prevY = screenY;
                    dragging = true;
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                dragging = false;
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (dragging) {
                    float distX = prevX - screenX;
                    float distY = prevY - screenY;
                    camera.position.x += distX;
                    camera.position.y -= distY;
                    prevX = screenX;
                    prevY = screenY;
                    return true;
                }
                return false;
            }
        };
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(inputAdapter);
        Gdx.input.setInputProcessor(inputMultiplexer);
        SpaceGame.INSTANCE.updateWorld=false;
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        SpaceGame.INSTANCE.updateWorld=true;
    }
}
