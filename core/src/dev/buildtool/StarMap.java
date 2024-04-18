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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

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
        target=new Image(SpaceOfChaos.INSTANCE.targetTexture);
        target.setOrigin(target.getWidth()/2,target.getHeight()/2);
        starSystems= SpaceOfChaos.INSTANCE.starSystems;
        viewport = new ScreenViewport();
        this.playerShip = playerShip;
        shapeRenderer=new ShapeRenderer();
        camera=viewport.getCamera();
        stage=new Stage(viewport);
        target.setVisible(false);
        stage.addActor(target);
        Texture starIcon = SpaceOfChaos.INSTANCE.starIcon;
        Skin skin= SpaceOfChaos.INSTANCE.skin;
        starSystems.forEach(starSystem -> {
            Image starImage=new Image(starIcon);
            starImage.setX(starSystem.positionX- (float) starIcon.getWidth() /2);
            starImage.setY(starSystem.positionY- (float) starIcon.getHeight() /2);
            stage.addActor(starImage);
            BitmapFont font = SpaceOfChaos.INSTANCE.bitmapFont;
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
            starImage.addListener(new ClickListener(Input.Buttons.RIGHT){
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
                                    playerShip.setCurrentSystem(selectedStarSystem);
                                    selectedStarSystem.ships.add(playerShip);
                                    playerShip.x=starSystem.starGate.getOppositeX();
                                    playerShip.y=starSystem.starGate.getOppositeY();
                                    SpaceOfChaos.INSTANCE.setScreen(new SystemScreen(starSystem));
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
            List<Planet> inhabitedPlanets=starSystem.planets.stream().filter(planet -> planet.kind== Planet.Kind.INHABITED).toList();
            List<Planet> uninhabitedPlanets=starSystem.planets.stream().filter(planet -> planet.kind== Planet.Kind.UNINHABITED).toList();
            TextTooltip tooltip;
            if(starSystem.occupied)
            {
                tooltip=new TextTooltip(starSystem.planets.size()+" planets",skin);
            }
            else
                tooltip = new TextTooltip((starSystem.spaceStation!=null?"Space station\n":"") +(inhabitedPlanets.isEmpty() ?"": inhabitedPlanets.size()+" inhabited planets\n")+(uninhabitedPlanets.isEmpty()?"": uninhabitedPlanets.size() + " uninhabited planets"), skin);
            tooltip.setInstant(true);
            starImage.addListener(tooltip);
            if(starSystem.ships.stream().anyMatch(ship -> ship instanceof NPCPilot npcPilot && npcPilot.pilotAI == PilotAI.AI) && starSystem.ships.stream().anyMatch(ship -> ship instanceof NPCPilot npcPilot && npcPilot.pilotAI != PilotAI.AI))
            {
                Image twoSwords=new Image(SpaceOfChaos.INSTANCE.twoSwordsTexture);
                twoSwords.setPosition(starImage.getX()+32, starImage.getY());
                twoSwords.setScale(0.01f);
                stage.addActor(twoSwords);
            }
        });
        camera.position.x-= (float) Gdx.graphics.getBackBufferWidth() /2;
        camera.position.y-= (float) Gdx.graphics.getBackBufferHeight() /2;
        currentStarSystem=currentSystem;
        shipX= playerShip.x;
        shipY= playerShip.y;
    }

    @Override
    public void render(float delta) {
        SpriteBatch spriteBatch= SpaceOfChaos.INSTANCE.uiBatch;
        ScreenUtils.clear(Color.BLACK);
        stage.act(delta);
        stage.draw();
        if(Gdx.input.isKeyJustPressed(Input.Keys.M))
        {
            SpaceOfChaos.INSTANCE.setScreen(new SystemScreen(currentStarSystem));
        }
        BitmapFont font = SpaceOfChaos.INSTANCE.bitmapFont;
        GlyphLayout glyphLayout=new GlyphLayout(font,"Galactic map");
        spriteBatch.begin();
        font.draw(spriteBatch,"Galactic map", (float) Gdx.graphics.getBackBufferWidth() /2- glyphLayout.width/2,Gdx.graphics.getBackBufferHeight()-30);
        glyphLayout.setText(font, "Press 'M' to exit");
        font.draw(spriteBatch,"Press 'M' to exit", (float) Gdx.graphics.getBackBufferWidth() /2- glyphLayout.width/2,Gdx.graphics.getBackBufferHeight()-50);
        glyphLayout.setText(font,"Double right click a system to travel");
        font.draw(spriteBatch,"Double right click a system to travel",(float) Gdx.graphics.getBackBufferWidth() /2- glyphLayout.width/2,Gdx.graphics.getBackBufferHeight()-70);
        font.draw(spriteBatch,"[GRAY]Gray []- system under AI control",0,70);
        font.draw(spriteBatch,"[YELLOW]Yellow []- free system",0,50);
        Functions.drawScaled(spriteBatch,SpaceOfChaos.INSTANCE.twoSwordsTexture, 0.01f,0,0);
        font.draw(spriteBatch," - system with conflict",25,20);
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
        SpaceOfChaos.INSTANCE.updateWorld=false;
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        SpaceOfChaos.INSTANCE.updateWorld=true;
    }
}
