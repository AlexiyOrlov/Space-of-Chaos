package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

import dev.buildtool.weapons.Weapon;

public class SpaceStationScreen extends ScreenAdapter implements StackHandler {
    private SpaceStation spaceStation;
    private final Stage stage;
    private final Viewport viewport;
    private Stack stackUnderMouse;
    private final ArrayList<SlotButton> slotButtons=new ArrayList<>();
    TabPane tabPane;
    private final Label moneyLabel;
    public SpaceStationScreen(SpaceStation spaceStation,PlayerShip playerShip) {
        SpaceOfChaos.INSTANCE.updateWorld=false;
        this.spaceStation = spaceStation;
        viewport=new FitViewport(Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());
        viewport.apply();
        stage=new Stage(viewport);
        Skin skin=SpaceOfChaos.INSTANCE.skin;
        tabPane=new TabPane(skin);
        tabPane.setFillParent(true);
        moneyLabel=new Label(""+playerShip.money,skin);
        Table table1=new Table();
        table1.add(new Label("Tab 1",skin));
        tabPane.addTab(table1,"License bureau");
        Table table2=new Table();
        int index=0;
        Table inventory=new Table();
        for (int i = PlayerShip.rows; i >0 ; i--) {
            for (int j = 0; j < PlayerShip.columns; j++) {
                SlotButton slotButton=new SlotButton(skin,index,this,playerShip.inventory,viewport);
                inventory.add(slotButton);
                index++;
                slotButtons.add(slotButton);
            }
            inventory.row();
        }
        table2.add(inventory);
        tabPane.addTab(table2,"Ship");
        stage.addActor(tabPane);

        //ship parts
        Table shipParts=new Table();
        shipParts.defaults().padTop(20);
        Inventory parts=playerShip.getShipParts();
        SlotButton hull=new SlotButton(skin, 0,this,parts,viewport,arg0 -> arg0!=null && arg0.item instanceof Hull);
        slotButtons.add(hull);
        SlotButton weapon=new SlotButton(skin,1,this,parts,viewport,arg0 -> arg0==null || arg0.item instanceof Weapon);
        slotButtons.add(weapon);
        SlotButton secondaryWeapon=new SlotButton(skin,4,this,parts,viewport,arg0 -> arg0==null || arg0.item instanceof Weapon);
        slotButtons.add(secondaryWeapon);
        SlotButton engine=new SlotButton(skin,2,this,parts,viewport,arg0 -> arg0!=null &&arg0.item instanceof Engine);
        slotButtons.add(engine);
        SlotButton sideThrusters=new SlotButton(skin,3,this,parts,viewport,arg0 -> arg0!=null &&  arg0.item instanceof SideThrusters);
        slotButtons.add(sideThrusters);
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

        table2.add(shipParts);
    }

    @Override
    public Stack getStackUnderMouse() {
        return stackUnderMouse;
    }

    @Override
    public void setStackUnderMouse(Stack stack) {
        stackUnderMouse=stack;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        ScreenUtils.clear(Color.LIGHT_GRAY);
        stage.act();
        stage.draw();
        slotButtons.forEach(SlotButton::drawInfo);
    }
}
