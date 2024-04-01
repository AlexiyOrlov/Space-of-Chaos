package dev.buildtool;

public class Inventory {
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
}
