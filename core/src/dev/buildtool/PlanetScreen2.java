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
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.util.dialog.Dialogs;
import com.kotcrab.vis.ui.util.dialog.OptionDialogAdapter;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;

import java.util.ArrayList;
import java.util.HashMap;

import dev.buildtool.weapons.Weapon;

public class PlanetScreen2 extends ScreenAdapter implements StackHandler {
    private final Stage stage;
    private final Viewport viewport;
    private Stack stackUnderMouse;
    private final ArrayList<Slot> slots =new ArrayList<>();

    private final Label moneyLabel;

    @Override
    public Stack getStackUnderMouse() {
        return stackUnderMouse;
    }

    @Override
    public void setStackUnderMouse(Stack stack) {
        stackUnderMouse=stack;
    }

    private final Planet planet;
    private final PlayerShip playerShip;
    private final Label capacityLabel;
    public PlanetScreen2(StarSystem system, Planet planet, PlayerShip player) {
        SpaceOfChaos.INSTANCE.updateWorld=false;
        Skin skin= SpaceOfChaos.INSTANCE.skin;
        playerShip=player;
        this.viewport =new FitViewport(Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());
        this.viewport.apply();
        moneyLabel=new Label("Money: "+player.money,skin);
        capacityLabel=new Label("",skin);
        calculateCapacity();
        TextImageButton takeOffButton = new TextImageButton("Take off", skin, SpaceOfChaos.INSTANCE.takeOffTexture);
        takeOffButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SpaceOfChaos.INSTANCE.setScreen(new SystemScreen(system));
                player.x=planet.x;
                player.y=planet.y;
                SpaceOfChaos.INSTANCE.autoSave();
            }
        });
        TextImageButton repairButton=new TextImageButton(player.integrity<player.getHull().integrity?"Repair hull":"Integrity is full",skin,SpaceOfChaos.INSTANCE.wrenchTexture);
        repairButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int toRepair=player.getHull().integrity-player.getIntegrity();
                int repairCost=toRepair*SpaceOfChaos.hullRepairCost;
                int canRepair=Math.min(repairCost,player.money);
                int amountToRepair=canRepair/SpaceOfChaos.hullRepairCost;
                if(toRepair>0) {
                    if(amountToRepair>0) {
                        Dialogs.showOptionDialog(stage, "Repair?", "Repair " + amountToRepair + " integrity for " + canRepair + "?", Dialogs.OptionDialogType.YES_NO, new OptionDialogAdapter() {
                            @Override
                            public void yes() {
                                player.money -= canRepair;
                                player.integrity += amountToRepair;
                                updateMoney();
                                if(player.integrity==player.getHull().integrity)
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

        TextButton toggleTime=new TextButton(SpaceOfChaos.INSTANCE.updateWorld?"World is simulating":"World is paused",skin);
        toggleTime.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SpaceOfChaos.INSTANCE.updateWorld=!SpaceOfChaos.INSTANCE.updateWorld;
                toggleTime.setText(SpaceOfChaos.INSTANCE.updateWorld?"World is simulating":"World is paused");
            }
        });
        this.planet=planet;

        stage=new Stage(this.viewport);
        Table playerInventory=new Table();
        int slotIndex=0;
        for (int i = PlayerShip.rows; i >0; i--) {
            for (int j = 0; j < PlayerShip.columns; j++) {
                Slot slot =new Slot(skin,slotIndex,PlanetScreen2.this,player.inventory, viewport);
                playerInventory.add(slot);
                slots.add(slot);
                slotIndex++;
            }
            playerInventory.row();
        }

        Table containingTable=new Table();
        TabPane tabPane=new TabPane(skin);
        //tab 1
        Table tab1=new Table();
        int in=0;
        tab1.padTop(20);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Slot slot =new Slot(skin, in,PlanetScreen2.this,planet.equipmentInventory,viewport){
                    @Override
                    protected boolean handleClick(int button, StackHandler stackHandler) {
                        if(stackHandler.getStackUnderMouse()==null) {
                            Stack stack = inventory.stacks[index];
                            Dialog dialog = new Dialog("Buy?", skin);
                            TextButton accept = new TextButton("Confirm", skin);
                            accept.addListener(new ChangeListener() {
                                @Override
                                public void changed(ChangeEvent event, Actor actor) {
                                    if(player.occupiedCapacity()>=player.getHull().capacity){
                                        Dialog cant=new Dialog("No space on ship",skin);
                                        cant.button("Ok");
                                        cant.show(stage);
                                    }
                                    else {
                                        if (player.money >= stack.item.basePrice) {
                                            player.addItem(new Stack(stack.item, 1));
                                            inventory.removeItem(stack.item, 1);
                                            player.money -= stack.item.basePrice;
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
                                if (stack.item.basePrice <= player.money)
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
                tab1.add(slot);
                slots.add(slot);
                in++;
            }
            tab1.row();
        }
        tab1.padBottom(40);
        Label sellLabel=new Label("Sell",skin);
        Image sellSlot=new Image(SpaceOfChaos.INSTANCE.cashTexture);
        sellSlot.addListener(new ClickListener(Input.Buttons.LEFT){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(stackUnderMouse!=null && stackUnderMouse.count==1) {
                    Dialogs.showOptionDialog(stage, "Sell?", "Confirm selling " + stackUnderMouse.item.name, Dialogs.OptionDialogType.YES_CANCEL, new OptionDialogAdapter() {
                        @Override
                        public void yes() {
                            player.money+=stackUnderMouse.item.basePrice;
                            stackUnderMouse=null;
                            updateMoney();
                        }
                    });
                }
            }
        });
        tab1.add(sellLabel).colspan(3);
        tab1.row();
        tab1.add(sellSlot).colspan(3);
        tab1.row();
        tab1.padRight(30);
        tabPane.addTab(tab1,"Equipment");

        //tab 2
        Table tab2=new Table();
        Table marketWares=new Table();
        marketWares.defaults().padRight(20).padBottom(10);
        marketWares.add(new Label("Wares", skin)).colspan(2);
        marketWares.add(new Label("Amount", skin),new Label("Price", skin));
        marketWares.row();

        Table purchaseHistoryTable=new Table();
        purchaseHistoryTable.defaults().padRight(20).padBottom(10);
        updatePurchaseTable(player, purchaseHistoryTable, skin);
        purchaseHistoryTable.right();
        BitmapFont font= SpaceOfChaos.INSTANCE.bitmapFont;
        planet.warePrices.forEach((ware, price) -> {
            Label wareName = new Label(ware.name, skin);
            Image image = new Image(ware.texture);
            Label warePrice;
            if(price>=Ware.BASE_PRICES.get(ware)*StarSystem.HIGHEST_PRICE_MULTIPLIER)
                warePrice=new Label(price.toString(),new Label.LabelStyle(font,Color.RED));
            else if(player.inventory.hasItem(Item.PRICE_SCANNER) && price<Ware.BASE_PRICES.get(ware))
            {
                warePrice=new Label(price.toString(),new Label.LabelStyle(font,Color.GREEN));
            }
            else
                warePrice = new Label(price.toString(), skin);
            Integer wareAmountt = planet.wareAmounts.get(ware);
            Label wareAmountLabel = new Label(wareAmountt.toString(), skin);

            TextButton sell=new TextButton("Sell",skin);

            //calculate ware count for player
            HashMap<Ware,Integer> playerWareCount=new HashMap<>();
            calculatePlayerWareCount(player, playerWareCount);
            Button buy = new TextButton("Buy", skin);
            buy.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Integer price = planet.warePrices.get(ware);
                    Integer currentAmount = planet.wareAmounts.get(ware);
                    if(currentAmount>0) {
                        int maximumToBuy = Math.min(currentAmount, player.money / price);
                        if (maximumToBuy > 0) {
                            if(player.getHull().capacity-player.occupiedCapacity()>0) {
                                Dialog dialog = new Dialog("Buying", skin);
                                Table table = new Table();
                                Label amountLabel = new Label("1", skin);
                                Slider amount = new Slider(1, Math.min(maximumToBuy, player.getHull().capacity - player.occupiedCapacity()), 1, false, skin);
                                amount.addListener(new ChangeListener() {
                                    @Override
                                    public void changed(ChangeEvent event, Actor actor) {
                                        amountLabel.setText("" + amount.getValue());
                                    }
                                });
                                Button accept = new TextButton("Accept", skin);
                                accept.addListener(new ChangeListener() {
                                    @Override
                                    public void changed(ChangeEvent event, Actor actor) {
                                        int wareAm = planet.wareAmounts.get(ware);
                                        int toBuy = (int) amount.getValue();
                                        wareAm -= toBuy;
                                        planet.wareAmounts.put(ware, wareAm);
                                        wareAmountLabel.setText(String.valueOf(wareAm));
                                        player.addItem(new Stack(ware, (int) toBuy));
                                        dialog.hide();
                                        player.money -= toBuy * price;
                                        updateMoney();
                                        WarePurchase warePurchase = new WarePurchase(ware, toBuy, price, toBuy * price);
                                        if (player.warePurchases.size() > 9) {
                                            player.warePurchases.removeFirst();
                                        }
                                        player.warePurchases.add(warePurchase);
                                        updatePurchaseTable(player, purchaseHistoryTable, skin);
                                        sell.setVisible(true);
                                        calculatePlayerWareCount(player, playerWareCount);
                                        calculateCapacity();
                                    }
                                });
                                Button cancel = new TextButton("Cancel", skin);
                                cancel.addListener(new ChangeListener() {
                                    @Override
                                    public void changed(ChangeEvent event, Actor actor) {
                                        dialog.hide();
                                    }
                                });
                                table.add(amountLabel, amount);
                                table.row();
                                table.add(accept, cancel);
                                dialog.add(table);
                                dialog.show(stage);
                            }
                            else {
                                Dialog cant=new Dialog("No space on ship",skin);
                                cant.button("Ok");
                                cant.show(stage);
                            }
                        } else {
                            Dialog dialog=new Dialog("Can't afford",skin);
                            Label message=new Label("Not enough money",skin);
                            dialog.button(new TextButton("OK",skin));
                            dialog.add(message);
                            dialog.show(stage);
                        }
                    }else {
                        Dialog dialog = new Dialog("Sold out", skin);
                        Label message = new Label(ware.name + " is sold out", skin);
                        dialog.button(new TextButton("OK", skin));
                        dialog.add(message);
                        dialog.show(stage);
                    }
                }
            });

            sell.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    int wareAmount=Math.min(Ware.MAXIMUM_WARE_AMOUNT,playerWareCount.get(ware));
                    Dialog sellDialog=new Dialog("Sell "+ware.name,skin);
                    Table table1=new Table();
                    Slider amountSlider=new Slider(1,wareAmount,1,false,skin);
                    amountSlider.setValue(wareAmount);
                    Label countLabel=new Label(wareAmount+"",skin);
                    Button accept=new TextButton("Accept",skin);
                    amountSlider.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            countLabel.setText(amountSlider.getValue()+"");
                        }
                    });
                    accept.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            int toSell= (int) amountSlider.getValue();
                            player.inventory.removeItem(ware,toSell);
                            int marketAmount=planet.wareAmounts.get(ware);
                            marketAmount+=toSell;
                            planet.wareAmounts.put(ware,marketAmount);
                            int playerNowHas=playerWareCount.get(ware)-toSell;
                            playerWareCount.put(ware,playerNowHas);
                            if(playerNowHas==0)
                            {
                                sell.setVisible(false);
                            }
                            wareAmountLabel.setText(marketAmount);
                            player.money+=toSell*price;
                            moneyLabel.setText("Money: "+player.money);
                            sellDialog.hide();
                            calculateCapacity();
                        }
                    });
                    Button cancel=new TextButton("Cancel",skin);
                    cancel.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            sellDialog.hide();
                        }
                    });
                    table1.add(countLabel);
                    table1.add(amountSlider).row();
                    table1.add(accept,cancel);
                    sellDialog.add(table1);
                    sellDialog.show(stage);
                }
            });

            marketWares.add(image, wareName, wareAmountLabel,warePrice);
            if (ware.needsLicense) {
                if (player.licences.get(ware)) {
                    marketWares.add(buy);
                    marketWares.add(sell);
                    if(playerWareCount.getOrDefault(ware,0)==0)
                        sell.setVisible(false);
                } else {
                    Label noLicense = new Label("No license", skin);
                    marketWares.add(noLicense).colspan(2);
                }
            } else {
                marketWares.add(buy);
                marketWares.add(sell);
                if(playerWareCount.getOrDefault(ware,0)==0)
                    sell.setVisible(false);
            }
            marketWares.row();
        });

        Table container=new Table();
        container.add(purchaseHistoryTable);
        container.row();
        //inner tables must not fill parent
        tab2.add(marketWares,container);
        tabPane.addTab(tab2,"Market").addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(!SpaceOfChaos.marketHelpShown)
                {
                    SpaceOfChaos.marketHelpShown=true;
                    Dialog help2=new Dialog("Help - market",skin);
                    Label label=new Label("At market you can buy and sell different wares. Some of the wares require a license, which can be bought at space station\n" +
                            "Red color indicates highest prices",skin);
                    help2.add(label);
                    help2.getContentTable().row();
                    TextButton gotit=new TextButton("Got it",skin);
                    gotit.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            help2.hide();
                        }
                    });
                    help2.add(gotit);
                    help2.show(stage);
                }
            }
        });

        //ship parts
        Table shipParts=new Table();
        shipParts.defaults().padTop(20);
        Inventory inventory=playerShip.getShipParts();
        Slot hull=new Slot(skin, 0,PlanetScreen2.this,inventory,viewport, arg0 -> arg0!=null && arg0.item instanceof Hull);
        slots.add(hull);
        Slot weapon=new Slot(skin,1,PlanetScreen2.this,inventory,viewport, arg0 -> arg0==null || arg0.item instanceof Weapon);
        slots.add(weapon);
        Slot secondaryWeapon=new Slot(skin,4,PlanetScreen2.this,inventory,viewport, arg0 -> arg0==null || arg0.item instanceof Weapon);
        slots.add(secondaryWeapon);
        Slot engine=new Slot(skin,2,PlanetScreen2.this,inventory,viewport, arg0 -> arg0!=null &&arg0.item instanceof Engine);
        slots.add(engine);
        Slot sideThrusters=new Slot(skin,3,PlanetScreen2.this,inventory,viewport, arg0 -> arg0!=null &&  arg0.item instanceof SideThrusters);
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

        containingTable.setFillParent(true);
        containingTable.add(new Label("Planet "+planet.name,skin)).colspan(3);
        containingTable.row();
        containingTable.add(moneyLabel);
        containingTable.add(capacityLabel);
        containingTable.add(takeOffButton);
        containingTable.add(repairButton);
        containingTable.add(toggleTime);
        containingTable.row();
        containingTable.add(tabPane);
        containingTable.add(playerInventory).colspan(3);
        containingTable.add(shipParts);
        stage.addActor(containingTable);

        if(!SpaceOfChaos.planetScreenHelpShown) {
            Dialog help = new Dialog("Help - planet screen", skin);
            Label s1 = new Label("You are currently landed on an inhabited planet. You can trade wares and ship parts here.\n" +
                    "Press 'Equipment' and 'Market' buttons to switch.\n" +
                    "You can also repair your hull here. While on a planet, you can toggle world simulation.\n" +
                    "Press 'Take off' button when you are ready to go to space.", skin);
            help.add(s1);
            help.getContentTable().row();
            TextButton gotIt = new TextButton("Got it", skin);
            gotIt.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    help.hide();
                }
            });
            help.add(gotIt);
            help.show(stage);
            SpaceOfChaos.planetScreenHelpShown=true;
        }
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.GRAY);
        stage.act(delta);
        stage.draw();
        SpriteBatch spriteBatch= SpaceOfChaos.INSTANCE.uiBatch;
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
        SpaceOfChaos.INSTANCE.updateWorld=true;
    }

    private static void calculatePlayerWareCount(PlayerShip player, HashMap<Ware, Integer> playerWareCount) {
        for (Stack stack : player.inventory.stacks) {
            if(stack!=null) {
                Item item = stack.item;
                if (item instanceof Ware w) {
                    int currentWareCount = playerWareCount.getOrDefault(w, 0);
                    currentWareCount += stack.count;
                    playerWareCount.put(w, currentWareCount);
                }
            }
        }
    }

    private static void updatePurchaseTable(PlayerShip player, Table purchaseHistoryTable, Skin skin) {
        if(!player.warePurchases.isEmpty()) {
            purchaseHistoryTable.clearChildren();
            purchaseHistoryTable.add(new Label("Market purchase history", skin)).colspan(5);
            purchaseHistoryTable.row();
            purchaseHistoryTable.add(new Label("Number", skin));
            purchaseHistoryTable.add(new Label("Ware", skin));
            purchaseHistoryTable.add(new Label("Price per unit", skin));
            purchaseHistoryTable.add(new Label("Amount bought", skin));
            purchaseHistoryTable.add(new Label("Total spent", skin));
            purchaseHistoryTable.row();
            int number = 1;
            for (WarePurchase warePurchase : player.warePurchases) {
                purchaseHistoryTable.add(new Label(number + ".", skin), new Label(warePurchase.ware.name, skin), new Label(warePurchase.pricePerUnit + "", skin), new Label(warePurchase.amountBought + "", skin), new Label(warePurchase.moneySpent + "", skin));
                purchaseHistoryTable.row();
                number++;
            }
        }
    }

    private void calculateCapacity()
    {
        int occupied= playerShip.occupiedCapacity();
        capacityLabel.setText("Capacity: "+occupied+"/"+playerShip.getHull().capacity);
    }

    void updateMoney()
    {
        moneyLabel.setText("Money: "+playerShip.money);
    }
}
