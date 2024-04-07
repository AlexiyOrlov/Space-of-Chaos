package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
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
import com.badlogic.gdx.utils.Predicate;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;

public class PlanetScreen extends ScreenAdapter {
    private final Stage stage;
    private final Viewport viewport;
    private Stack stackUnderMouse;
    private final PlayerShip player;
    private final Planet planet;
    private HashMap<Integer, Predicate<PlayerShip>> predicates=new HashMap<>();
    private Inventory shipParts;

    public PlanetScreen(StarSystem system, Planet planet, PlayerShip player) {
        viewport=new FitViewport(Gdx.graphics.getBackBufferWidth(),Gdx.graphics.getBackBufferHeight());
        viewport.apply();
        this.planet=planet;
        stage = new Stage(viewport);
        Table table = new Table();
        table.setFillParent(true);
        Skin skin = SpaceGame.INSTANCE.skin;
        this.player = player;

        Table purchaseHistoryTable=new Table();
        purchaseHistoryTable.setVisible(false);
        purchaseHistoryTable.setFillParent(true);
        purchaseHistoryTable.defaults().padRight(20).padBottom(10);
        updatePurchaseTable(player, purchaseHistoryTable, skin);
        purchaseHistoryTable.right();
        stage.addActor(purchaseHistoryTable);

        Table market = new Table(skin);
        Label moneyLabel=new Label("Money: "+player.money,skin);
        if(planet.isInhabited) {
            table.add(moneyLabel);
            table.row();
            market.setFillParent(true);
            market.setVisible(false);
            market.defaults().padRight(20).padBottom(10);
            market.add(new Label("Wares", skin)).colspan(2);
            market.add(new Label("Amount", skin),new Label("Price", skin));
            market.row();

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
                                Dialog dialog = new Dialog("Buying", skin);
                                Table table = new Table();
                                Label amountLabel = new Label("1", skin);
                                Slider amount = new Slider(1, maximumToBuy, 1, false, skin);
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
                                        moneyLabel.setText("Money: "+player.money);
                                        WarePurchase warePurchase=new WarePurchase(ware,toBuy,price,toBuy*price);
                                        if(player.warePurchases.size()>9)
                                        {
                                            player.warePurchases.removeFirst();
                                        }
                                        player.warePurchases.add(warePurchase);
                                        updatePurchaseTable(player,purchaseHistoryTable,skin);
                                        sell.setVisible(true);
                                        calculatePlayerWareCount(player,playerWareCount);
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

                market.add(image, wareName, wareAmountLabel,warePrice);
                if (ware.needsLicense) {
                    if (player.licences.get(ware)) {
                        market.add(buy);
                        market.add(sell);
                        if(playerWareCount.getOrDefault(ware,0)>0)
                            sell.setVisible(false);
                    } else {
                        Label noLicense = new Label("No license", skin);
                        market.add(noLicense).colspan(2);
                    }
                } else {
                    market.add(buy);
                    market.add(sell);
                    if(playerWareCount.getOrDefault(ware,0)==0)
                        sell.setVisible(false);
                }
                market.row();
            });

            stage.addActor(market);

            TextImageButton marketButton = new TextImageButton("Market", skin, SpaceGame.INSTANCE.scalesTexture);
            marketButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    market.setVisible(true);
                    purchaseHistoryTable.setVisible(true);
                    planet.equipmentInventory.setVisible(false);
                    shipParts.setVisible(false);
                }
            });
            table.add(marketButton);

            Table equipmentShop=new Table();
            equipmentShop.setFillParent(true);
            TextImageButton equipmentButton=new TextImageButton("Equipment",skin, SpaceGame.INSTANCE.gearTexture);
            equipmentButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    planet.equipmentInventory.setVisible(true);
                    market.setVisible(false);
                    purchaseHistoryTable.setVisible(false);
                    shipParts.setVisible(false);
                }
            });
            table.add(equipmentButton);

            stage.addActor(equipmentShop);

        }
        else {
            Table resources=new Table();
            resources.setFillParent(true);
            resources.setVisible(false);
            if(planet.explorationProgress<planet.size)
            {
                Label needsToBeExplored=new Label("Explored: "+planet.explorationProgress+"/"+planet.size,skin);
                resources.add(needsToBeExplored);
            }
            else {
                resources.add(new Label("Resource",skin));
                resources.row();
                resources.add(new Label("=========",skin));
                resources.row();
                planet.resources.forEach(resource -> {
                    resources.add(new Label(resource.name,skin)).row();
                });
            }

            TextImageButton resourcesButton=new TextImageButton("Resources",skin,SpaceGame.INSTANCE.diamondTexture);
            resourcesButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    resources.setVisible(true);
                }
            });
            stage.addActor(resources);

            table.add(resourcesButton);
        }


        TextImageButton takeOffButton = new TextImageButton("Take off", skin, SpaceGame.INSTANCE.takeOffTexture);
        takeOffButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SpaceGame.INSTANCE.setScreen(new SystemScreen(system, planet.x,planet.y));
            }
        });
        table.add(takeOffButton);
        table.bottom();
        stage.addActor(table);

        Texture slotTexture = SpaceGame.INSTANCE.slotTexture2;
        int width=Gdx.graphics.getBackBufferWidth();
        int height=Gdx.graphics.getBackBufferHeight();
        shipParts=new Inventory(4);
        Slot hullSlot=new Slot(slotTexture, width/2,height/2-64,0,shipParts);
        shipParts.slots[0]=hullSlot;
        Slot engineSlot=new Slot(slotTexture,width/2,height/2-128,1,shipParts);
        shipParts.slots[1]=engineSlot;
        Slot weapon=new Slot(slotTexture,width/2,height/2-192,2,shipParts);
        shipParts.slots[2]=weapon;
        Slot thrusters=new Slot(slotTexture,width/2,height/2-256,3,shipParts);
        shipParts.slots[3]=thrusters;
        shipParts.setVisible(false);

        TextImageButton shipEquipment=new TextImageButton("Ship",skin,SpaceGame.INSTANCE.ship2icon);
        shipEquipment.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                shipParts.setVisible(true);
                if(planet.isInhabited){
                    planet.equipmentInventory.setVisible(false);
                    market.setVisible(false);
                    purchaseHistoryTable.setVisible(false);
                }
            }
        });
        table.add(shipEquipment);

        int index = 0;
        for (int i = 4; i > 0; i--) {
            for (int j = 0; j < 10; j++) {
                Vector2 slotPosition = new Vector2(Gdx.graphics.getBackBufferWidth() + j * 64 - 64 * 10, i * 80-60);
                Slot slot = new Slot(slotTexture, (int) slotPosition.x, (int) slotPosition.y, index, player.inventory);
                player.inventory.slots[index]=slot;
                index++;
            }
        }

        if(planet.isInhabited) {
            int in = 0;
            for (int i = 3; i > 0; i--) {
                for (int j = 0; j < 3; j++) {
                    Vector2 slotPosition = new Vector2(Gdx.graphics.getBackBufferWidth() / 2 + j * 64 - 96, i * 64 + Gdx.graphics.getBackBufferHeight() / 2);
                    Slot slot = new Slot(SpaceGame.INSTANCE.slotTexture2, (int) slotPosition.x, (int) slotPosition.y, in, planet.equipmentInventory);

                    Stack stack = planet.equipmentInventory.stacks[in];

                    planet.equipmentInventory.setExtractionPredicateAndAction(playerShip -> playerShip.money>=stack.item.basePrice, playerShip -> {
                        playerShip.money-=stack.item.basePrice;
                        moneyLabel.setText("Money: "+playerShip.money);
                    },in);

                    planet.equipmentInventory.slots[in] = slot;
                    in++;
                }
            }
            planet.equipmentInventory.setVisible(false);
        }
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

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        SpaceGame.INSTANCE.updateWorld=false;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.LIGHT_GRAY);
        SpriteBatch spriteBatch=SpaceGame.INSTANCE.uiBatch;
        BitmapFont font=SpaceGame.INSTANCE.bitmapFont;
        stage.act();
        stage.draw();
        spriteBatch.begin();
        //draw slots and stacks first
        player.inventory.draw(spriteBatch);
        shipParts.draw(spriteBatch);
        if(planet.isInhabited) {
            planet.equipmentInventory.draw(spriteBatch);
            //draw tooltips
            player.inventory.drawSlotInfo(spriteBatch, viewport);
            planet.equipmentInventory.drawSlotInfo(spriteBatch, viewport);
            if (Gdx.input.justTouched()) {
                stackUnderMouse = player.inventory.processClick(viewport, stackUnderMouse);
                stackUnderMouse = planet.equipmentInventory.processClick(viewport, stackUnderMouse);

            }
        }
        shipParts.drawSlotInfo(spriteBatch,viewport);
        stackUnderMouse=shipParts.processClick(viewport,stackUnderMouse);

        GlyphLayout glyphLayout=new GlyphLayout(font, planet.name);
        font.draw(spriteBatch,planet.name, (float) Gdx.graphics.getBackBufferWidth() /2-glyphLayout.width/2,Gdx.graphics.getBackBufferHeight()-30);

        if(stackUnderMouse!=null)
        {
            Vector2 mousePositionConverted=viewport.unproject(new Vector2(Gdx.input.getX(),Gdx.input.getY()));
            spriteBatch.draw(stackUnderMouse.item.texture,mousePositionConverted.x,mousePositionConverted.y-32);
            font.draw(spriteBatch,""+stackUnderMouse.count,mousePositionConverted.x+32,mousePositionConverted.y-32);
        }
        spriteBatch.end();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        SpaceGame.INSTANCE.updateWorld=true;
    }

    private class TransactionConfirmation extends ChangeListener{
        private PlayerShip playerShip;
        private Label moneyLabel;
        private Stack stack;
        private Dialog buy;

        public TransactionConfirmation(PlayerShip playerShip, Label moneyLabel,Stack stack) {
            this.playerShip = playerShip;
            this.moneyLabel = moneyLabel;
            this.stack=stack;
        }

        public boolean accepted;
        @Override
        public void changed(ChangeEvent event, Actor actor) {
            Skin skin=SpaceGame.INSTANCE.skin;
//            Dialog buy=new Dialog("Buy?",skin);
            TextButton confirm=new TextButton("Confirm purchase",skin);
            confirm.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    playerShip.money -= stack.item.basePrice;
                    buy.hide();
                    moneyLabel.setText("Money: "+playerShip.money);
                    accepted=true;
                }
            });
            buy.add(confirm);
            TextButton cancel=new TextButton("Cancel",skin);
            cancel.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    buy.hide();
                    accepted=false;
                }
            });
            buy.add(cancel);
            buy.show(stage);
        }

        public boolean purchased()
        {
            return accepted;
        }
    }
}
