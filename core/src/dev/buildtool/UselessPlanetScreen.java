package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import dev.buildtool.weapons.Weapon;

public class UselessPlanetScreen extends ScreenAdapter implements StackHandler{
    Planet planet;
    Viewport viewport;
    private Stage stage;
    private ArrayList<Slot> slots=new ArrayList<>();
    private Stack stack;
    public UselessPlanetScreen(Planet planet,PlayerShip playerShip,StarSystem system) {
        this.planet = planet;
        viewport=new FitViewport(Gdx.graphics.getBackBufferWidth(),SpaceOfChaos.getWindowHeight());
        viewport.apply();
        stage=new Stage(viewport);
        Skin skin=SpaceOfChaos.INSTANCE.skin;

        Table table=new Table();

        TextImageButton takeOffButton = new TextImageButton("Take off", skin, SpaceOfChaos.INSTANCE.takeOffTexture);
        takeOffButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SpaceOfChaos.INSTANCE.setScreen(new SystemScreen(system));
                playerShip.x=planet.x;
                playerShip.y=planet.y;
                SpaceOfChaos.INSTANCE.autoSave();
            }
        });
        table.setFillParent(true);

        //ship parts
        Table shipParts=new Table();
        shipParts.defaults().padTop(20);
        Inventory inventory=playerShip.getShipParts();
        Slot hull=new Slot(skin, 0,this,inventory,viewport, arg0 -> arg0!=null && arg0.item instanceof Hull);
        slots.add(hull);
        Slot weapon=new Slot(skin,1,this,inventory,viewport, arg0 -> arg0==null || arg0.item instanceof Weapon);
        slots.add(weapon);
        Slot secondaryWeapon=new Slot(skin,4,this,inventory,viewport, arg0 -> arg0==null || arg0.item instanceof Weapon);
        slots.add(secondaryWeapon);
        Slot engine=new Slot(skin,2,this,inventory,viewport, arg0 -> arg0!=null &&arg0.item instanceof Engine);
        slots.add(engine);
        Slot sideThrusters=new Slot(skin,3,this,inventory,viewport, arg0 -> arg0!=null &&  arg0.item instanceof SideThrusters);
        slots.add(sideThrusters);
        shipParts.add(new Label("Hull",skin));
        shipParts.add(hull);
        shipParts.row();
        shipParts.add(new Label("Weapon 1",skin));
        shipParts.add(weapon);
        shipParts.row();
        shipParts.add(new Label("Weapon 2",skin));
        shipParts.add(secondaryWeapon);
        shipParts.row();
        shipParts.add(new Label("Engine",skin));
        shipParts.add(engine);
        shipParts.row();
        shipParts.add(new Label("Side thrusters",skin));
        shipParts.add(sideThrusters);
        shipParts.row();

        Table playerInventory=new Table();
        int slotIndex=0;
        for (int i = PlayerShip.rows; i >0; i--) {
            for (int j = 0; j < PlayerShip.columns; j++) {
                Slot slot =new Slot(skin,slotIndex,this,playerShip.inventory, viewport);
                playerInventory.add(slot);
                slots.add(slot);
                slotIndex++;
            }
            playerInventory.row();
        }
        table.add(new Label("Planet "+planet.name,skin)).colspan(3);
        table.row();
        table.add(shipParts);
        table.add(playerInventory);
        table.add(takeOffButton);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.GRAY);
        stage.act(delta);
        stage.draw();
        SpriteBatch spriteBatch= SpaceOfChaos.INSTANCE.uiBatch;
        BitmapFont font= SpaceOfChaos.INSTANCE.bitmapFont;
        Vector2 mousePositionConverted=viewport.unproject(new Vector2(Gdx.input.getX(),Gdx.input.getY()));
        if(stack!=null)
        {
            spriteBatch.begin();
            spriteBatch.draw(stack.item.texture,mousePositionConverted.x,mousePositionConverted.y-32);
            if(stack.count>1)
                font.draw(spriteBatch,""+stack.count,mousePositionConverted.x+32,mousePositionConverted.y-32);
            spriteBatch.end();
        }
        for (Slot slot : slots) {
            slot.drawInfo();
        }
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
        return stack;
    }

    @Override
    public void setStackUnderMouse(Stack stack) {
        this.stack=stack;
    }
}
