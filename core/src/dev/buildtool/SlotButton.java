package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SlotButton extends Table {
    private final int index;
    private final Inventory inventory;
    private final Viewport viewport;

    public SlotButton(Skin skin, Texture background, int index, StackHandler stackHandler, Inventory inventory, Viewport viewport) {
        super(skin);
        this.inventory=inventory;
        add(new Image(background));
        this.index=index;
        this.viewport=viewport;
        addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(button==0)
                {
                    if(stackHandler!=null) {
                        Stack stackUnderMouse = stackHandler.getStackUnderMouse();
                        if(stackUnderMouse==null)
                        {
                            stackHandler.setStackUnderMouse(inventory.stacks[index]);
                            inventory.stacks[index]=null;
                        }
                        else {
                            Stack present = inventory.stacks[index];
                            if (present == null) {
                                inventory.stacks[index] = stackUnderMouse;
                                stackHandler.setStackUnderMouse(null);
                            } else if (present.item != stackUnderMouse.item) {
                                inventory.stacks[index] = stackUnderMouse;
                                stackHandler.setStackUnderMouse(present);
                            }
                        }
                        return true;
                    }
                }
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Stack stack = inventory.stacks[index];
        if(stack !=null)
        {
            BitmapFont font=SpaceGame.INSTANCE.bitmapFont;
            batch.draw(stack.item.texture,getX(),getY());
            if(stack.count>1)
                font.draw(batch,stack.count+"",getX()+10,getY()+14);
        }
    }

    public boolean isOverSlot()
    {
        int mx=Gdx.input.getX();
        int my=Gdx.input.getY();
        Vector2 mp=viewport.unproject(new Vector2(mx,my));
        Vector2 sc= this.localToScreenCoordinates(new Vector2(0,0));
        return mp.x > sc.x && mp.x < sc.x + getWidth() && mp.y > sc.y-getHeight() && mp.y < sc.y;
    }

    public void drawInfo()
    {
        int mx=Gdx.input.getX();
        int my=Gdx.input.getY();
        Vector2 mp=viewport.unproject(new Vector2(mx,my));
        if(index==0) {
            if (isOverSlot()) {
                if (inventory.stacks[index] != null) {
                    SpriteBatch spriteBatch = SpaceGame.INSTANCE.batch;
                    BitmapFont font = SpaceGame.INSTANCE.bitmapFont;
                    spriteBatch.begin();
                    font.draw(spriteBatch, inventory.stacks[index].item.name, mp.x+20, mp.y+20);
                    spriteBatch.end();
                }
            }

        }
    }
}
