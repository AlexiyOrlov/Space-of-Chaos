package dev.buildtool;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Inventory {
    public Stack[] stacks;
    public Slot[] slots;

    public Inventory(int stackCount) {
        stacks=new Stack[stackCount];
        slots=new Slot[stackCount];
    }

    public void addItem(Stack stack)
    {
        boolean collected=false;
        for (Stack existingStack: stacks) {
            if(existingStack!=null) {
                if (existingStack.item == stack.item && existingStack.count<existingStack.item.maxSize) {
                    int maxCount=existingStack.item.maxSize;
                    int remainingSpace=maxCount-existingStack.count;
                    if(remainingSpace>=0)
                    {
                        if(stack.count>remainingSpace)
                        {
                            existingStack.count+=remainingSpace;
                        }
                        else {
                            existingStack.count+=stack.count;
                            stack.count=0;
                        }
                        if(stack.count==0)
                        {
                            collected=true;
                            break;
                        }
                    }
                    else {
                        existingStack.count=maxCount;
                        stack.count-=remainingSpace;
                    }
                }
            }
        }

        if(!collected)
        {
            for (int i=0;i<stacks.length;i++) {
                Stack next=stacks[i];
                if(next==null)
                {
                    stacks[i]=stack;
                    break;
                }
            }
        }
    }

    public void removeItem(Item item,int amount){
        for (int i = 0; i < stacks.length; i++) {
            Stack next=stacks[i];
            if(next!=null && next.item==item)
            {
                int toRemove=Math.min(amount,next.count);
                next.count-=toRemove;
                if(next.count==0)
                {
                    stacks[i]=null;
                }
                amount-=toRemove;
                if(amount==0)
                    break;
            }
        }
    }

    public void draw(SpriteBatch spriteBatch)
    {
        for (Slot slot : slots) {
            slot.draw(spriteBatch);
        }
    }

    public void drawSlotInfo(SpriteBatch spriteBatch, Viewport viewport)
    {
        for (Slot slot : slots) {
            slot.drawInfo(spriteBatch, viewport);
        }
    }

    public Stack processClick(Viewport viewport,Stack stackUnderMouse)
    {
        for (Slot slot : slots) {
            int clickedSlot=slot.processClick(viewport);
            if (clickedSlot != -1) {
                if (stackUnderMouse == null) {
                    stackUnderMouse = stacks[clickedSlot];
                    stacks[clickedSlot] = null;
                } else {
                    Stack present = stacks[clickedSlot];
                    if (present == null) {
                        stacks[clickedSlot] = stackUnderMouse;
                        stackUnderMouse = null;
                    } else if (present.item != stackUnderMouse.item) {
                        stacks[clickedSlot] = stackUnderMouse;
                        stackUnderMouse = present;
                    }
                }
                break;
            }
        }
        return stackUnderMouse;
    }

    public void setVisible(boolean b)
    {
        for (Slot slot : slots) {
            slot.visible=b;
        }
    }
}
