package dev.buildtool;

import java.util.HashMap;
import java.util.Map;

public class Inventory implements SaveData{
    public Stack[] stacks;

    public Inventory(int stackCount) {
        stacks=new Stack[stackCount];
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
                stacks[i]=stack;
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
