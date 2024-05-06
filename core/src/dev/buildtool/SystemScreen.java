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
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

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
    final Stage stage;
    private final Table pauseMenu;
    private final Table playerInventory;
    private Stack stackUnderMouse;
    private boolean inventoryShown;
    private ArrayList<Slot> slots =new ArrayList<>(40);
    public final Deque<String> messageQueue=new LinkedList<>();
    public HashMap<String,Float> lastMessageTimes=new HashMap<>(10);

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
                try{
                    Dialog dialog=new Dialog("Save game",skin);
                    Path savePath=Path.of(SpaceOfChaos.INSTANCE.dataDir,"Space of Chaos","Saves");
                    var files= Files.walk(savePath).sorted(Comparator.comparingLong(path -> path.toFile().lastModified())).filter(path -> path.toString().endsWith(".yaml")).toList();
                    Skin skin=SpaceOfChaos.INSTANCE.skin;
                    files.forEach(path -> {
                        Label label1=new Label(path.getFileName().toString(),skin);
                        TextButton textButton=new TextButton("Overwrite",skin);
                        Table content=dialog.getContentTable();
                        content.add(label1,textButton);
                        content.row();
                        textButton.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                Dialogs.showOptionDialog(stage,"Overwrite?",null, Dialogs.OptionDialogType.YES_NO,new OptionDialogAdapter(){
                                    @Override
                                    public void yes() {
                                        Yaml yaml=new Yaml();
                                        String string=yaml.dump(SpaceOfChaos.INSTANCE.getData());
                                        try{
                                            Files.writeString(path, string);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        finally {
                                            dialog.hide();
                                        }
                                    }
                                });
                            }
                        });
                    });
                    TextButton newSave=new TextButton("New save",skin);
                    newSave.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            SpaceOfChaos.INSTANCE.saveGame(stage);
                        }
                    });
                    dialog.button(newSave);
                    dialog.button("Cancel");
                    dialog.show(stage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        TextButton load=new TextButton("Load",skin);
        load.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //copied from game class
                Path savePath=Path.of(SpaceOfChaos.INSTANCE.dataDir,"Space of Chaos","Saves");
                try {
                    var files= Files.walk(savePath).sorted(Comparator.comparingLong(path -> path.toFile().lastModified())).filter(path -> path.toString().endsWith(".yaml")).toList();
                    Skin skin=SpaceOfChaos.INSTANCE.skin;
                    Dialog dialog=new Dialog("List of saves",skin);

                    files.forEach(path -> {
                        Label label=new Label(path.getFileName().toString(),skin);
                        TextButton save=new TextButton("Load",skin);
                        save.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                SpaceOfChaos.INSTANCE.loadGame(path);
                                if(SpaceOfChaos.INSTANCE.playerShip.isLanded())
                                {
                                    System.out.println("Landed");
                                }
                                pauseMenu.setVisible(false);
                            }
                        });

                        Table table1=dialog.getContentTable();
                        table1.add(label);
                        try {
                            BasicFileAttributes attributes=  Files.readAttributes(path, BasicFileAttributes.class);
                            DateFormat dateFormat=new SimpleDateFormat("dd/MM", Locale.getDefault());
                            Label date=new Label("      On "+dateFormat.format(attributes.lastModifiedTime().toMillis()),skin);
                            table1.add(date);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        table1.add(save);
                        TextButton delete=new TextButton("Delete",skin);
                        delete.addListener(new ChangeListener() {
                            @Override
                            public void changed(ChangeEvent event, Actor actor) {
                                Dialogs.showOptionDialog(stage,"Confirm deletion","Delete this save?", Dialogs.OptionDialogType.YES_NO,new OptionDialogAdapter(){
                                    @Override
                                    public void yes() {
                                        try {
                                            Files.delete(path);
                                            table1.removeActor(label);
                                            table1.removeActor(load);
                                            table1.removeActor(delete);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                });
                            }
                        });
                        table1.add(delete);
                        table1.row();
                    });
                    TextButton cancel=new TextButton("Cancel",skin);
                    dialog.button(cancel);
                    dialog.show(stage);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        TextButton mainMenu=new TextButton("Main menu",skin);
        mainMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SpaceOfChaos.INSTANCE.setScreen(new StartScreen(SpaceOfChaos.INSTANCE));
            }
        });
        pauseMenu.add(label);
        pauseMenu.row();
        pauseMenu.add(save);
        pauseMenu.row();
        pauseMenu.add(load);
        pauseMenu.row();
        pauseMenu.add(mainMenu);
        pauseMenu.row();
        pauseMenu.add(quit);
        pauseMenu.row();
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
        for (int i = PlayerShip.rows; i >0; i--) {
            for (int j = 0; j < PlayerShip.columns; j++) {
                Slot slot =new Slot(skin,slotIndex,this,playerShip.inventory, stage.getViewport());
                inventory.add(slot);
                slots.add(slot);
                slotIndex++;
            }
            inventory.row();
        }
        playerInventory.setFillParent(true);
        playerInventory.setVisible(false);
        stage.addActor(playerInventory);
        Table content=new Table();
        Inventory shipEquipment=playerShip.getShipParts();
        Slot hull=new Slot(skin, 0,this,shipEquipment, stage.getViewport(), arg0 -> false);
        slots.add(hull);
        Slot weapon=new Slot(skin,1,this,shipEquipment,stage.getViewport(), arg0 -> arg0!=null && arg0.item instanceof Weapon);
        slots.add(weapon);
        Slot secondaryWeapon=new Slot(skin,4,this,shipEquipment,stage.getViewport(), arg0 -> false);
        slots.add(secondaryWeapon);
        Slot engine=new Slot(skin,2,this,shipEquipment,stage.getViewport(), arg0 -> false);
        slots.add(engine);
        Slot sideThrusters=new Slot(skin,3,this,shipEquipment,stage.getViewport(), arg0 -> false);
        slots.add(sideThrusters);
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
        Label money=new Label("Money: "+playerShip.money,new Label.LabelStyle(SpaceOfChaos.INSTANCE.bitmapFont, Color.WHITE));
        playerInventory.add(money).colspan(2);
        playerInventory.row();
        playerInventory.add(content);
        playerInventory.add(inventory);

        if(!SpaceOfChaos.systemScreenHelpShown) {
            Dialog help = new Dialog("Help - system screen", skin);
            Label label1 = new Label("""
                    This is current star system. You can move your ship with WASD keys.
                    Shoot your primary weapom with left mouse button, secondary - with right mouse button.
                    You can toggle whether you will hit only AI ships with the projectiles or any ship with Tab.
                    Inhabited planets are indicated by green circle on HUD, uninhabited - by gray circle.
                    To travel to other systems, approach star gate, which looks like a circular structure.
                    Press F1 to show reminder.
                    The goal of the game is to liberate all systems from AI.
                    """, skin);
            TextButton gotIt = new TextButton("Got it", skin);
            gotIt.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    help.hide();
                }
            });
            help.add(label1);
            help.getContentTable().row();
            help.add(gotIt);
            help.show(stage);
            SpaceOfChaos.systemScreenHelpShown=true;
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        Skin skin=SpaceOfChaos.INSTANCE.skin;
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
        if(starSystem.spaceStation!=null && !camera.frustum.pointInFrustum(starSystem.spaceStation.x,starSystem.spaceStation.y,0))
        {
            drawWaypoint(starSystem.spaceStation.x,starSystem.spaceStation.y,SpaceOfChaos.INSTANCE.spaceStationIcon);
        }
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
        Vector2 mouseUIPosition=stage.getViewport().unproject(new Vector2(Gdx.input.getX(),Gdx.input.getY()));
        if(stackUnderMouse!=null)
        {
            spriteBatch.begin();
            spriteBatch.draw(stackUnderMouse.item.texture,mouseUIPosition.x,mouseUIPosition.y-32);
            if(stackUnderMouse.count>1)
                font.draw(spriteBatch,""+stackUnderMouse.count,mouseUIPosition.x+32,mouseUIPosition.y-32);
            spriteBatch.end();
        }

        if(inventoryShown)
        {
            slots.forEach(Slot::drawInfo);
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
                if(ship instanceof NPCPilot npcPilot && npcPilot.contains(viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()))))
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
        else {
            if(!pauseMenu.isVisible()) {
                spriteBatch.begin();
                GlyphLayout glyphLayout = new GlyphLayout(font, "Ship was destroyed. Press ESC for menu");
                font.draw(spriteBatch, "Ship was destroyed. Press Esc for menu", Gdx.graphics.getBackBufferWidth() / 2 - glyphLayout.width / 2, SpaceOfChaos.getWindowHeight() / 2);
                spriteBatch.end();
            }
        }

        if(SpaceOfChaos.INSTANCE.updateWorld) {
            int y = 0;
            spriteBatch.begin();
            for (String s : messageQueue) {
                float messageTime = lastMessageTimes.get(s);
                if (messageTime > 0) {
                    messageTime -= delta;
                    lastMessageTimes.put(s, messageTime);
                    font.draw(spriteBatch, s, 0, SpaceOfChaos.getWindowHeight() - 80 - y);
                    y-=20;
                }
            }
            spriteBatch.end();
        }

        if(playerShip!=null && Gdx.input.justTouched() && playerShip.mouseAction)
        {
            Vector2 mousePos=viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            for (Ship ship : starSystem.ships) {
                if(ship.contains(mousePos) && ship.getAI()==PilotAI.MERCENARY && ship.getLeader()==null)
                {
                    int equipmentCost=ship.getEngine().basePrice;
                    equipmentCost+=ship.getThrusters().basePrice;
                    equipmentCost+=ship.getHull().basePrice;
                    equipmentCost+=ship.getPrimaryWeapon().basePrice;
                    equipmentCost+=ship.getSecondaryWeapon().basePrice;

                    Dialog dialog=new Dialog("Communication",skin);
                    TextButton hire=new TextButton("Hire this pilot for "+(equipmentCost/2)+"?\nYou have "+playerShip.money,skin);
                    dialog.button(hire);
                    int finalEquipmentCost = equipmentCost;
                    hire.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            Functions.log(String.valueOf(finalEquipmentCost));
                            playerShip.toggleMouseAction();
                            int cost=finalEquipmentCost/2;
                            if(cost<=playerShip.money)
                            {
                                playerShip.money-=cost;
                                ship.setLeader(playerShip);
                                playerShip.hiredShips.add(ship);
                                if(ship instanceof NPCPilot npcPilot)
                                {
                                    npcPilot.money+=cost;
                                }
                            }
                            else {
                                Dialogs.showOKDialog(stage,"Not enough money","You have "+playerShip.money+" money. This pilot costs "+cost+".");
                            }
                        }
                    });
                    TextButton cancel=new TextButton("Cancel",skin);
                    cancel.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            playerShip.toggleMouseAction();
                        }
                    });
                    dialog.button(cancel);
                    dialog.show(stage);
                    break;
                }
            }
        }

        if(playerShip!=null && !playerShip.mouseAction && Gdx.input.isKeyJustPressed(Input.Keys.F1))
        {
            SpaceOfChaos.INSTANCE.setScreen(new HelpScreen(starSystem));
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
        lastMessageTimes.put(message,10f);
    }
}
