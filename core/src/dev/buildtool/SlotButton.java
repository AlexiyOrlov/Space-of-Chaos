package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
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
    private StackHandler stackHandler;
    private Texture background;
    private Stack stack;
    public int index;
    private Inventory inventory;

    public SlotButton(Skin skin, Texture background,int index,StackHandler stackHandler,Inventory inventory) {
        super(skin);
        this.background = background;
        this.stackHandler=stackHandler;
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
        if(inventory.stacks[index]!=null)
            batch.draw(inventory.stacks[index].item.texture,getX(),getY());
    }
}
