package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

import dev.buildtool.weapons.Weapon;

public class SystemScreen extends ScreenAdapter implements StackHandler {

    SpriteBatch spriteBatch;
    ShapeRenderer shapeRenderer,uiShapeRenderer;
    ArrayList<Planet> planets;
    Star star;
    Camera camera;
    Viewport viewport;
    StarSystem starSystem;
    Rectangle viewportBounds;
    private final Stage stage;
    private final Table pauseMenu;
    private final Table playerInventory;
    private Stack stackUnderMouse;
    private boolean inventoryShown;
    private ArrayList<SlotButton> slotButtons=new ArrayList<>(40);
    public final Deque<String> messageQueue=new LinkedList<>();
    public float lastMessageTime;

    public SystemScreen(StarSystem starSystem) {
        SpaceOfChaos spaceOfChaos = SpaceOfChaos.INSTANCE;
        this.spriteBatch = spaceOfChaos.worldBatch;
        this.starSystem = starSystem;
        this.planets = this.starSystem.planets;
        this.star = this.starSystem.star;
        PlayerShip playerShip= SpaceOfChaos.INSTANCE.playerShip;
        camera=new OrthographicCamera();
		viewport=new ScreenViewport(camera);
		viewport.apply();
        shapeRenderer= spaceOfChaos.shapeRenderer;
        viewportBounds=new Rectangle(0,0,viewport.getScreenWidth(),viewport.getScreenHeight());
        uiShapeRenderer= SpaceOfChaos.INSTANCE.uiShapeRenderer;
        stage=new Stage();
        pauseMenu=new Table();
        pauseMenu.setFillParent(true);
        Skin skin= SpaceOfChaos.INSTANCE.skin;
        TextButton quit=new TextButton("Quit",skin);
        quit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        Label label=new Label("Game paused",skin);
        TextButton save=new TextButton("Save",skin);
        save.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SpaceOfChaos.INSTANCE.saveGame(stage);
            }
        });
        TextButton load=new TextButton("Load",skin);
        load.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                SpaceOfChaos.INSTANCE.loadGame();
                pauseMenu.setVisible(false);
            }
        });
        pauseMenu.add(label);
        pauseMenu.row();
        pauseMenu.add(save);
        pauseMenu.row();
        pauseMenu.add(load);
        pauseMenu.row();
        pauseMenu.add(quit);
        pauseMenu.setVisible(false);
        stage.addActor(pauseMenu);
        Pixmap pixmap=new Pixmap(1,1, Pixmap.Format.RGB565);
        pixmap.setColor(Color.GRAY);
        pixmap.fill();
        TextureRegionDrawable textureRegionDrawable=new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pauseMenu.setBackground(textureRegionDrawable);

        playerInventory=new Table();
        Table inventory=new Table();
        int slotIndex=0;
        for (int i = 4; i >0; i--) {
            for (int j = 0; j < 10; j++) {
                SlotButton slotButton=new SlotButton(skin,slotIndex,this,playerShip.inventory, stage.getViewport());
                inventory.add(slotButton);
                slotButtons.add(slotButton);
                slotIndex++;
            }
            inventory.row();
        }
        playerInventory.setFillParent(true);
        playerInventory.setVisible(false);
        stage.addActor(playerInventory);
        Table content=new Table();
        Inventory shipEquipment=playerShip.getShipParts();
        SlotButton hull=new SlotButton(skin, 0,this,shipEquipment, stage.getViewport(),arg0 -> false);
        slotButtons.add(hull);
        SlotButton weapon=new SlotButton(skin,1,this,shipEquipment,stage.getViewport(),arg0 -> arg0!=null && arg0.item instanceof Weapon);
        slotButtons.add(weapon);
        SlotButton secondaryWeapon=new SlotButton(skin,4,this,shipEquipment,stage.getViewport(),arg0 -> false);
        slotButtons.add(secondaryWeapon);
        SlotButton engine=new SlotButton(skin,2,this,shipEquipment,stage.getViewport(),arg0 -> false);
        slotButtons.add(engine);
        SlotButton sideThrusters=new SlotButton(skin,3,this,shipEquipment,stage.getViewport(),arg0 -> false);
        slotButtons.add(sideThrusters);
        Label.LabelStyle labelStyle=new Label.LabelStyle(SpaceOfChaos.INSTANCE.bitmapFont, Color.WHITE);
        content.add(new Label("Hull",labelStyle));
        content.add(hull).padRight(20);
        content.row();
        content.add(new Label("Weapon",labelStyle));
        content.add(weapon).padRight(20);
        content.row();
        content.add(new Label("Weapon 2",labelStyle));
        content.add(secondaryWeapon).padRight(20);
        content.row();
        content.add(new Label("Engine",labelStyle));
        content.add(engine).padRight(20);
        content.row();
        content.add(new Label("Side thrusters",labelStyle));
        content.add(sideThrusters).padRight(20);
        content.row();
        playerInventory.add(content);
        playerInventory.add(inventory);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        PlayerShip playerShip= SpaceOfChaos.INSTANCE.playerShip;
        if(playerShip!=null)
            playerShip.update(delta, viewport);

        starSystem.draw(spriteBatch, shapeRenderer,delta);
        if(playerShip!=null) {
            camera.position.set(playerShip.x, playerShip.y, 0);

            if(playerShip.integrity>0) {
                uiShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                uiShapeRenderer.setColor(Color.GOLDENROD);
                float ip = (float) playerShip.integrity / playerShip.getHull().integrity;
                float height = ip * (Gdx.graphics.getBackBufferHeight() - 200);
                uiShapeRenderer.rect((float) Gdx.graphics.getBackBufferWidth() - 60 - 34, (Gdx.graphics.getBackBufferHeight() - height) / 2, 60, height);
                uiShapeRenderer.end();
            }
            if(playerShip.homingTarget!=null)
            {
                NPCPilot target=playerShip.homingTarget;
                spriteBatch.begin();
                Functions.drawRotated(spriteBatch,SpaceOfChaos.INSTANCE.reticle, playerShip.homingTarget.x,playerShip.homingTarget.y,playerShip.reticleRotation);
                spriteBatch.end();

                if(playerShip.inventory.hasItem(Item.TARGET_RADAR))
                {
                    float integrityPercent=target.getIntegrityPercent();
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.setColor(Color.GREEN);
                    shapeRenderer.rect(target.x-50,target.y+ target.hull.look.getHeight()/2,100*integrityPercent,10);
                    shapeRenderer.end();
                }
            }
        }
        final Vector2 starPos=new Vector2(0,0);
        spriteBatch.begin();
        if(!camera.frustum.pointInFrustum(0,0,0))
            drawWaypoint(starPos, SpaceOfChaos.INSTANCE.starIcon);
        planets.forEach(planet -> {
            if(!camera.frustum.pointInFrustum(planet.x,planet.y,0)) {
                if (planet.kind== Planet.Kind.INHABITED) {
                    drawWaypoint(planet.x,planet.y, SpaceOfChaos.INSTANCE.inhabitedPlanetIcon);
                } else {
                    drawWaypoint(planet.x,planet.y, SpaceOfChaos.INSTANCE.uninhabitedPlanetIcon);
                }
            }
        });
        if(!camera.frustum.pointInFrustum(starSystem.starGate.x,starSystem.starGate.y,0))
        {
            drawWaypoint(starSystem.starGate.x,starSystem.starGate.y, SpaceOfChaos.INSTANCE.stargateIcon);
        }
        if(playerShip!=null && playerShip.inventory.hasItem(Item.SHIP_RADAR))
        {
            starSystem.ships.forEach(ship -> {
                if(ship!=playerShip && !camera.frustum.pointInFrustum(ship.getX(),ship.getY(),0))
                {
                    drawWaypoint(ship.getX(),ship.getY(), SpaceOfChaos.INSTANCE.shipIcon3);
                }
            });
        }
        spriteBatch.end();

        stage.act(delta);
        stage.draw();

        SpriteBatch spriteBatch= SpaceOfChaos.INSTANCE.uiBatch;
        BitmapFont font= SpaceOfChaos.INSTANCE.bitmapFont;
        Vector2 mousePositionConverted=stage.getViewport().unproject(new Vector2(Gdx.input.getX(),Gdx.input.getY()));
        if(stackUnderMouse!=null)
        {
            spriteBatch.begin();
            spriteBatch.draw(stackUnderMouse.item.texture,mousePositionConverted.x,mousePositionConverted.y-32);
            if(stackUnderMouse.count>1)
                font.draw(spriteBatch,""+stackUnderMouse.count,mousePositionConverted.x+32,mousePositionConverted.y-32);
            spriteBatch.end();
        }

        if(inventoryShown)
        {
            slotButtons.forEach(SlotButton::drawInfo);
        }

        if(!inventoryShown) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                SpaceOfChaos.INSTANCE.updateWorld = !SpaceOfChaos.INSTANCE.updateWorld;
                pauseMenu.setVisible(!SpaceOfChaos.INSTANCE.updateWorld);
            }
        }

        if(!pauseMenu.isVisible()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                SpaceOfChaos.INSTANCE.updateWorld = !SpaceOfChaos.INSTANCE.updateWorld;
                inventoryShown = !SpaceOfChaos.INSTANCE.updateWorld;
                playerInventory.setVisible(inventoryShown);
            }
        }

        if(playerShip!=null) {
            for (Ship ship : starSystem.ships) {
                if(ship instanceof NPCPilot npcPilot && npcPilot.contains(mousePositionConverted))
                {
                    if(playerShip.damageOnlyAIShips)
                    {
                        if(npcPilot.pilotAI==PilotAI.AI)
                        {
                            playerShip.homingTarget=npcPilot;
                            break;
                        }
                    }
                    else
                    {
                        playerShip.homingTarget=npcPilot;
                        break;
                    }
                }
            }
        }

        if(lastMessageTime>0)
        {
            lastMessageTime-=delta;
            spriteBatch.begin();
            font.draw(spriteBatch,messageQueue.getLast(),0,SpaceOfChaos.getWindowHeight()-80);
            spriteBatch.end();
        }
    }

    private void drawWaypoint(float tox,float toy,Texture icon)
    {
        PlayerShip playerShip= SpaceOfChaos.INSTANCE.playerShip;
        if(playerShip!=null) {
            SpriteBatch uibatch = SpaceOfChaos.INSTANCE.uiBatch;
            float xdist = tox - playerShip.x;
            float ydist = toy - playerShip.y;
            int backBufferWidth = Gdx.graphics.getBackBufferWidth();
            int backBufferHeight = Gdx.graphics.getBackBufferHeight();

            float halfWidth = backBufferWidth / 2;
            float halfHeight = backBufferHeight / 2;
            float rx;
            float ry;
            Vector2 uiCoords = viewport.project(new Vector2(tox, toy));
            if (Math.abs(xdist) > halfWidth) {
                if (xdist > 0)
                    rx = backBufferWidth - icon.getWidth();
                else
                    rx = 0;
            } else {
                rx = uiCoords.x;
            }

            if (Math.abs(ydist) > halfHeight) {
                if (ydist > 0) {
                    ry = backBufferHeight - icon.getHeight();
                } else
                    ry = 0;
            } else
                ry = uiCoords.y;


            uibatch.begin();
            uibatch.draw(icon, rx, ry);
            uibatch.end();
        }
    }

    private void drawWaypoint(Vector2 to, Texture icon) {
        PlayerShip playerShip= SpaceOfChaos.INSTANCE.playerShip;
        if(playerShip!=null) {
            SpriteBatch uiBatch = SpaceOfChaos.INSTANCE.uiBatch;
            final Vector2 playerPos = new Vector2(playerShip.x, playerShip.y);
            int backBufferWidth = Gdx.graphics.getBackBufferWidth();
            Vector2 halfWidth = (new Vector2(backBufferWidth / 2, 0));
            int backBufferHeight = Gdx.graphics.getBackBufferHeight();
            Vector2 halfHeight = (new Vector2(0, backBufferHeight / 2));
            float x = 0;
            float y = 0;
            if (halfWidth.x < Math.abs(playerPos.x)) {
                if (playerPos.x < to.x) {
                    x += backBufferWidth - icon.getWidth();
                }
            } else {
                x = -playerPos.x + backBufferWidth / 2 - icon.getWidth() / 2;
            }
            if (halfHeight.y < Math.abs(playerPos.y)) {
                if (playerPos.y < to.y)
                    y += backBufferHeight - icon.getHeight();
            } else {
                y = -playerPos.y + backBufferHeight / 2 - icon.getHeight() / 2;
            }

            uiBatch.begin();
            uiBatch.draw(icon, x, y);
            uiBatch.end();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
        stage.getViewport().update(width,height);
    }

    static Vector2 lineLineIntersection(Vector2 X1, Vector2 Y1, Vector2 X2, Vector2 Y2)
    {
        // Line AB represented as a1x + b1y = c1
        float a1 = Y1.y - X1.y;
        float b1 = X1.x - Y1.x;
        float c1 = a1*(X1.x) + b1*(X1.y);

        // Line CD represented as a2x + b2y = c2
        float a2 = Y2.y - X2.y;
        float b2 = X2.x - Y2.x;
        float c2 = a2*(X2.x)+ b2*(X2.y);

        float determinant = a1*b2 - a2*b1;

        if (determinant == 0)
        {
            // The lines are parallel. This is simplified
            // by returning a pair of FLT_MAX
            return new Vector2(Float.MAX_VALUE, Float.MAX_VALUE);
        }
        else
        {
            float x = (b2*c1 - b1*c2)/determinant;
            float y = (a1*c2 - a2*c1)/determinant;
            return new Vector2(x, y);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
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
    public Stack getStackUnderMouse() {
        return stackUnderMouse;
    }

    @Override
    public void setStackUnderMouse(Stack stack) {
        stackUnderMouse=stack;
    }

    public void addMessage(String message)
    {
        if(messageQueue.size()>9)
        {
            messageQueue.remove();
        }
        messageQueue.add(message);
        lastMessageTime=10;
    }
}
