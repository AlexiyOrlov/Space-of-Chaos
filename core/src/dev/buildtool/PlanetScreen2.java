package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.tabbedpane.Tab;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPane;
import com.kotcrab.vis.ui.widget.tabbedpane.TabbedPaneAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import dev.buildtool.weapons.Weapon;

public class PlanetScreen2 extends ScreenAdapter implements StackHandler {
    private final Stage stage;
    private final Viewport viewport;
    private Stack stackUnderMouse;
    private final ArrayList<SlotButton> slotButtons=new ArrayList<>();

    private final Label moneyLabel;

    @Override
    public Stack getStackUnderMouse() {
        return stackUnderMouse;
    }

    @Override
    public void setStackUnderMouse(Stack stack) {
        stackUnderMouse=stack;
    }

    class ShipTab extends Tab{
        private Table content;

        public ShipTab(Viewport viewport,PlayerShip playerShip) {
            super(false,false);
            Skin skin=SpaceGame.INSTANCE.skin;
            content=new Table();
            content.defaults().padTop(20);
            Inventory inventory=playerShip.getShipParts();
            SlotButton hull=new SlotButton(skin, 0,PlanetScreen2.this,inventory,viewport,arg0 -> arg0!=null && arg0.item instanceof Hull);
            slotButtons.add(hull);
            SlotButton weapon=new SlotButton(skin,1,PlanetScreen2.this,inventory,viewport,arg0 -> arg0!=null && arg0.item instanceof Weapon);
            slotButtons.add(weapon);
            SlotButton engine=new SlotButton(skin,2,PlanetScreen2.this,inventory,viewport,arg0 -> arg0!=null &&arg0.item instanceof Engine);
            slotButtons.add(engine);
            SlotButton sideThrusters=new SlotButton(skin,3,PlanetScreen2.this,inventory,viewport,arg0 -> arg0!=null &&  arg0.item instanceof SideThrusters);
            slotButtons.add(sideThrusters);
            content.add(new Label("Hull",skin));
            content.add(hull);
            content.row();
            content.add(new Label("Weapon",skin));
            content.add(weapon);
            content.row();
            content.add(new Label("Engine",skin));
            content.add(engine);
            content.row();
            content.add(new Label("Side engine",skin));
            content.add(sideThrusters);
            content.row();
        }

        @Override
        public String getTabTitle() {
            return "Ship";
        }

        @Override
        public Table getContentTable() {
            return content;
        }
    }

    class EquipmentTab extends Tab{

        private final Table content=new Table();

        public EquipmentTab(Viewport viewport,PlayerShip player) {
            super(false,false);
            Skin skin=SpaceGame.INSTANCE.skin;
            int in=0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    SlotButton slotButton=new SlotButton(skin, in,PlanetScreen2.this,planet.equipmentInventory,viewport){
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
                    content.add(slotButton);
                    slotButtons.add(slotButton);
                    in++;
                }
                content.row();
            }
        }

        @Override
        public String getTabTitle() {
            return "Equipment";
        }

        @Override
        public Table getContentTable() {
            return content;
        }
    }

    class MarketTab extends Tab{

        private final Table content=new Table();

        public MarketTab(PlayerShip player, Label moneyLabel) {
            super(false,false);
            content.setFillParent(true);
            Skin skin=SpaceGame.INSTANCE.skin;
            Table marketWares=new Table();
            marketWares.defaults().padRight(20).padBottom(10);
            marketWares.add(new Label("Wares", skin)).colspan(2);
            marketWares.add(new Label("Amount", skin),new Label("Price", skin));
            marketWares.row();

            Table purchaseHistoryTable=new Table();
            purchaseHistoryTable.defaults().padRight(20).padBottom(10);
            updatePurchaseTable(player, purchaseHistoryTable, skin);
            purchaseHistoryTable.right();

            planet.warePrices.forEach((ware, price) -> {
                Label wareName = new Label(ware.name, skin);
                Image image = new Image(ware.texture);
                Label warePrice;
                if(price<Ware.BASE_PRICES.get(ware))
                    warePrice = new Label(price+" (below average)", skin);
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
                            //TODO take into account ship capacity
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
                            Label message = new Label(ware.type + " is sold out", skin);
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
                        if(playerWareCount.getOrDefault(ware,0)>0)
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
            content.add(marketWares,container);
        }

        @Override
        public String getTabTitle() {
            return "Market";
        }

        @Override
        public Table getContentTable() {
            return content;
        }
    }
    private Planet planet;
    private PlayerShip playerShip;
    private Label capacityLabel;
    public PlanetScreen2(StarSystem system, Planet planet, PlayerShip player) {
        Skin skin=SpaceGame.INSTANCE.skin;
        playerShip=player;
        this.viewport =new FitViewport(Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());
        this.viewport.apply();
        Table outer=new Table();
        moneyLabel=new Label("Money: "+player.money,skin);
        capacityLabel=new Label("",skin);
        calculateCapacity();
        outer.add(moneyLabel);
        outer.add(capacityLabel);
        TextImageButton takeOffButton = new TextImageButton("Take off", skin, SpaceGame.INSTANCE.takeOffTexture);
        takeOffButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SpaceGame.INSTANCE.setScreen(new SystemScreen(system, planet.x,planet.y));
            }
        });
        outer.add(takeOffButton);
        outer.row();
        outer.setFillParent(true);
        this.planet=planet;
        TabbedPane tabbedPane = new TabbedPane();
        tabbedPane.add(new EquipmentTab(viewport,player));
        tabbedPane.add(new MarketTab(player,moneyLabel));
        tabbedPane.add(new ShipTab(viewport,player));

        stage=new Stage(this.viewport);
        Table tabs= tabbedPane.getTable();
        outer.add(tabs);
        outer.row();
        Table tabContainer=new Table();
        outer.add(tabContainer);
        tabbedPane.addListener(new TabbedPaneAdapter(){
            @Override
            public void switchedTab(Tab tab) {
                Table content=tab.getContentTable();
                tabContainer.clearChildren();
                tabContainer.add(content).expand().fill();
            }
        });

        stage.addActor(outer);
        tabbedPane.switchTab(tabbedPane.getTabs().get(0));

        Table playerInventory=new Table();
        int slotIndex=0;
        for (int i = 4; i >0; i--) {
            for (int j = 0; j < 10; j++) {
                SlotButton slotButton=new SlotButton(skin,slotIndex,PlanetScreen2.this,player.inventory, viewport);
                playerInventory.add(slotButton);
                slotButtons.add(slotButton);
                slotIndex++;
            }
            playerInventory.row();
        }
        outer.add(playerInventory);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.GRAY);
        stage.act(delta);
        stage.draw();
        SpriteBatch spriteBatch=SpaceGame.INSTANCE.batch;
        BitmapFont font=SpaceGame.INSTANCE.bitmapFont;
        Vector2 mousePositionConverted=viewport.unproject(new Vector2(Gdx.input.getX(),Gdx.input.getY()));
        //TODO fix
        if(stackUnderMouse!=null)
        {
            spriteBatch.begin();
            spriteBatch.draw(stackUnderMouse.item.texture,mousePositionConverted.x,mousePositionConverted.y-32);
            if(stackUnderMouse.count>1)
                font.draw(spriteBatch,""+stackUnderMouse.count,mousePositionConverted.x+32,mousePositionConverted.y-32);
            spriteBatch.end();
        }
        for (SlotButton slotButton : slotButtons) {
            slotButton.drawInfo();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        SpaceGame.INSTANCE.updateWorld=false;
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        SpaceGame.INSTANCE.updateWorld=true;
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
