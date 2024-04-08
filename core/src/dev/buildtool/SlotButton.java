package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class SlotButton extends Table {
    private int index;
    private final Inventory inventory;

    public SlotButton(Skin skin, Texture background,int index,StackHandler stackHandler,Inventory inventory) {
        super(skin);
        this.inventory=inventory;
        add(new Image(background));
        this.index=index;

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
}
