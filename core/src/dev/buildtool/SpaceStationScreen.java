package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;

import java.util.ArrayList;

import dev.buildtool.weapons.Weapon;

public class SpaceStationScreen extends ScreenAdapter implements StackHandler {
    private SpaceStation spaceStation;
    private final Stage stage;
    private final Viewport viewport;
    private Stack stackUnderMouse;
    private final ArrayList<SlotButton> slotButtons=new ArrayList<>();
    TabPane tabPane;
    private final Label moneyLabel,capacityLabel;
    private final PlayerShip playerShip;
    public SpaceStationScreen(SpaceStation spaceStation,PlayerShip playerShip) {
        SpaceOfChaos.INSTANCE.updateWorld=false;
        this.playerShip=playerShip;
        this.spaceStation = spaceStation;
        viewport=new FitViewport(Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());
        viewport.apply();
        stage=new Stage(viewport);
        Skin skin=SpaceOfChaos.INSTANCE.skin;
        capacityLabel=new Label("Capacity: ",skin);
        tabPane=new TabPane(skin);
        moneyLabel=new Label(""+playerShip.money,skin);
        Table container=new Table();
        container.setFillParent(true);
        Table table1=new Table();
        playerShip.licences.forEach((ware, aBoolean) -> {
            if(!aBoolean)
            {
                TextButton buyLicense=new TextButton("Buy trade license for "+ware.name+" for "+ware.licenseCost,skin);
                table1.add(buyLicense);
                table1.row();
                buyLicense.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if(playerShip.money>=ware.licenseCost)
                        {
                            playerShip.money-=ware.licenseCost;
                            playerShip.licences.put(ware,true);
                            Dialogs.showOKDialog(stage,"Purchased license","You can now trade "+ware.name);
                            table1.removeActor(buyLicense);
                        }
                        else {
                            Dialogs.showOKDialog(stage,"Not enough money","");
                        }
                    }
                });
            }
        });

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
        tabPane.addTab(table2,"Ship");

        //ship parts
        Table outer=new Table();
        Table stationInventory=new Table();
        int in=0;
        stationInventory.padTop(20);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                SlotButton slotButton=new SlotButton(skin, in,this,spaceStation.equipmentInventory,viewport){
                    @Override
                    protected boolean handleClick(int button, StackHandler stackHandler) {
                        if(stackHandler.getStackUnderMouse()==null) {
                            Stack stack = inventory.stacks[index];
                            Dialog dialog = new Dialog("Buy?", skin);
                            TextButton accept = new TextButton("Confirm", skin);
                            accept.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    if(playerShip.occupiedCapacity()>=playerShip.getHull().capacity){
                                        Dialog cant=new Dialog("No space on ship",skin);
                                        cant.button("Ok");
                                        cant.show(stage);
                                    }
                                    else {
                                        if (playerShip.money >= stack.item.basePrice) {
                                            playerShip.addItem(new Stack(stack.item, 1));
                                            inventory.removeItem(stack.item, 1);
                                            playerShip.money -= stack.item.basePrice;
                                            updateMoney();
                                            dialog.hide();
                                            calculateCapacity();
                                        }
                                    }
                                }
                            });
                            TextButton cancel = new TextButton("Cancel", skin);
                            cancel.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    dialog.hide();
                                }
                            });
                            dialog.add(accept, cancel);
                            if (stack != null) {
                                if (stack.item.basePrice <= playerShip.money)
                                    dialog.show(stage);
                                else {
                                    Dialog no = new Dialog("Not enough money", skin);
                                    no.button("Ok");
                                    no.show(stage);
                                }
                            }
                            return true;
                        }
                        return false;
                    }
                };
                stationInventory.add(slotButton);
                slotButtons.add(slotButton);
                in++;
            }
            stationInventory.row();
        }
        stationInventory.padBottom(40);

        Label sellLabel=new Label("Sell",skin);
        Image sellSlot=new Image(SpaceOfChaos.INSTANCE.cashTexture);
        sellSlot.addListener(new ClickListener(Input.Buttons.LEFT){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(stackUnderMouse!=null && stackUnderMouse.count==1) {
                    Dialogs.showOptionDialog(stage, "Sell?", "Confirm selling " + stackUnderMouse.item.name, Dialogs.OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
                        @Override
                        public void yes() {
                            playerShip.money+=stackUnderMouse.item.basePrice;
                            stackUnderMouse=null;
                            updateMoney();
                        }
                    });
                }
            }
        });
        outer.add(stationInventory).padRight(20);
        outer.row();
        outer.add(sellLabel);
        outer.row();
        outer.add(sellSlot);
        outer.row();

        table2.add(outer);
        table2.add(inventory);

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

        TextImageButton takeOffButton = new TextImageButton("Take off", skin, SpaceOfChaos.INSTANCE.takeOffTexture);
        takeOffButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SpaceOfChaos.INSTANCE.setScreen(new SystemScreen(playerShip.getCurrentSystem()));
                playerShip.x= spaceStation.x;
                playerShip.y= spaceStation.y;
                SpaceOfChaos.INSTANCE.autoSave();
            }
        });

        TextImageButton repairButton=new TextImageButton(playerShip.integrity<playerShip.getHull().integrity?"Repair hull":"Integrity is full",skin,SpaceOfChaos.INSTANCE.wrenchTexture);
        repairButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int toRepair=playerShip.getHull().integrity-playerShip.getIntegrity();
                int repairCost=toRepair*50;
                int canRepair=Math.min(repairCost,playerShip.money);
                int amountToRepair=canRepair/50;
                if(toRepair>0) {
                    if(amountToRepair>0) {
                        Dialogs.showOptionDialog(stage, "Repair?", "Repair " + amountToRepair + " integrity for " + canRepair + "?", Dialogs.OptionDialogType.YES_NO, new OptionDialogAdapter() {
                            @Override
                            public void yes() {
                                playerShip.money -= canRepair;
                                playerShip.integrity += amountToRepair;
                                updateMoney();
                                if(playerShip.integrity==playerShip.getHull().integrity)
                                {
                                    repairButton.setText("Integrity is full");
                                }
                            }
                        });
                    }
                    else {
                        Dialogs.showOKDialog(stage,"Not enough money","");
                    }
                }
                else {
                    Dialogs.showOKDialog(stage,"Integrity is full","No need to repair");
                }
            }
        });
        container.add(tabPane);
        container.add(takeOffButton);
        container.add(repairButton);
        stage.addActor(container);
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
        SpaceOfChaos.INSTANCE.updateWorld=true;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.LIGHT_GRAY);
        stage.act();
        stage.draw();
        SpriteBatch spriteBatch=SpaceOfChaos.INSTANCE.uiBatch;
        BitmapFont font= SpaceOfChaos.INSTANCE.bitmapFont;
        Vector2 mousePositionConverted=viewport.unproject(new Vector2(Gdx.input.getX(),Gdx.input.getY()));
        if(stackUnderMouse!=null)
        {
            spriteBatch.begin();
            spriteBatch.draw(stackUnderMouse.item.texture,mousePositionConverted.x,mousePositionConverted.y-32);
            if(stackUnderMouse.count>1)
                font.draw(spriteBatch,""+stackUnderMouse.count,mousePositionConverted.x+32,mousePositionConverted.y-32);
            spriteBatch.end();
        }
        slotButtons.forEach(SlotButton::drawInfo);
    }

    void updateMoney()
    {
        moneyLabel.setText("Money: "+playerShip.money);
    }

    private void calculateCapacity()
    {
        int occupied= playerShip.occupiedCapacity();
        capacityLabel.setText("Capacity: "+occupied+"/"+playerShip.getHull().capacity);
    }
}
