package dev.buildtool;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Inventory implements SaveData{
    public Stack[] stacks;
    public Slot[] slots;
    public HashMap<Integer, Predicate<PlayerShip>> predicateHashMap=new HashMap<>();
    public HashMap<Integer,Consumer<PlayerShip>> actionHashMap= new HashMap<>();

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
                Predicate<PlayerShip> predicate=predicateHashMap.get(clickedSlot);
                Consumer<PlayerShip> consumer=actionHashMap.get(clickedSlot);
                if (stackUnderMouse == null) {
                    if(predicate!=null  ) {
                        if(predicate.test(SpaceOfChaos.INSTANCE.playerShip)) {
                            stackUnderMouse = stacks[clickedSlot];
                            stacks[clickedSlot] = null;
                            consumer.accept(SpaceOfChaos.INSTANCE.playerShip);
                        }
                    }
                    else {
                        stackUnderMouse = stacks[clickedSlot];
                        stacks[clickedSlot] = null;
                    }
                } else {
                    Stack present = stacks[clickedSlot];
                    if (present == null && predicate==null) {
                        stacks[clickedSlot] = stackUnderMouse;
                        stackUnderMouse = null;
                    } else if (predicate==null && present.item != stackUnderMouse.item) {
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

    public boolean isEmpty()
    {
        boolean inventoryEmpty=true;
        for (Stack stack : stacks) {
            if(stack!=null)
            {
                inventoryEmpty=false;
                break;
            }
        }
        return inventoryEmpty;
    }

    public void setExtractionPredicateAndAction(Predicate<PlayerShip> playerShipPredicate,Consumer<PlayerShip> action,int slot)
    {
        actionHashMap.put(slot,action);
        this.predicateHashMap.put(slot,playerShipPredicate);
    }

    @Override
    public Map<String, Object> getData() {
        HashMap<String,Object> data=new HashMap<>();
        for (int i = 0; i < stacks.length; i++) {
            Stack next=stacks[i];
            if(next!=null)
            {
                data.put("stack "+i,next.getData());
            }
        }
        data.put("count",stacks.length);
        return data;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load(Map<String, Object> data) {
        int stackCount= (int) data.get("count");
        for (int i = 0; i < stackCount; i++) {
            if(data.containsKey("stack "+i)) {
                Stack stack = new Stack();
                stack.load((Map<String, Object>) data.get("stack "+i));
            }
        }
    }

    public boolean hasItem(Item item)
    {
        for (Stack next : stacks) {
            if (next != null && next.item == item)
                return true;
        }
        return false;
    }
}
