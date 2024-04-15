package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Predicate;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

public class SlotButton extends Table {
    protected final int index;
    protected final Inventory inventory;
    private final Viewport viewport;
    private final Predicate<Stack> incomingStackPredicate;

    public SlotButton(Skin skin, int index,StackHandler stackHandler, Inventory inventory, Viewport viewport) {
        this(skin, SpaceOfChaos.INSTANCE.slotTexture3, index,stackHandler,inventory,viewport,null);
    }
    public SlotButton(Skin skin, int index,StackHandler stackHandler, Inventory inventory, Viewport viewport,Predicate<Stack> incomingStackPredicate) {
        this(skin, SpaceOfChaos.INSTANCE.slotTexture3, index,stackHandler,inventory,viewport,incomingStackPredicate);
    }

    public SlotButton(Skin skin, Texture background, int index, StackHandler stackHandler, Inventory inventory, Viewport viewport, Predicate<Stack> incomingStackPredicate) {
        super(skin);
        this.inventory=inventory;
        this.incomingStackPredicate=incomingStackPredicate;
        add(new Image(background));
        this.index=index;
        this.viewport=viewport;
        addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (handleClick(button,stackHandler)) return true;
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    protected boolean handleClick(int button,StackHandler stackHandler) {
        if(button ==0)
        {
            if(stackHandler!=null) {
                Stack stackUnderMouse = stackHandler.getStackUnderMouse();
                if(incomingStackPredicate==null || incomingStackPredicate.evaluate(stackUnderMouse)) {
                    if (stackUnderMouse == null) {
                        stackHandler.setStackUnderMouse(inventory.stacks[index]);
                        inventory.stacks[index] = null;
                    } else {
                        Stack present = inventory.stacks[index];
                        if (present == null) {
                            inventory.stacks[index] = stackUnderMouse;
                            stackHandler.setStackUnderMouse(null);
                        } else if (present.item != stackUnderMouse.item) {
                            inventory.stacks[index] = stackUnderMouse;
                            stackHandler.setStackUnderMouse(present);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Stack stack = inventory.stacks[index];
        if(stack !=null)
        {
            BitmapFont font= SpaceOfChaos.INSTANCE.bitmapFont;
            batch.draw(stack.item.texture,getX(),getY());
            if(stack.count>1)
                font.draw(batch,stack.count+"",getX()+20,getY()+14);
        }
    }

    public boolean isOverSlot()
    {
        int mx=Gdx.input.getX();
        int my=Gdx.input.getY();
        Vector2 sc= this.localToScreenCoordinates(new Vector2(0,0));
        return mx > sc.x && mx < sc.x + getWidth() && my > sc.y-getHeight() && my < sc.y;
    }

    public void drawInfo()
    {
        int mx=Gdx.input.getX();
        int my=Gdx.input.getY();
        Vector2 mp=viewport.unproject(new Vector2(mx,my));

        if (isOverSlot()) {
            Stack stack = inventory.stacks[index];
            if (stack != null) {
                SpriteBatch spriteBatch = SpaceOfChaos.INSTANCE.uiBatch;
                BitmapFont font = SpaceOfChaos.INSTANCE.bitmapFont;
                List<String> itemInfo=stack.item.getTooltip();
                ShapeRenderer shapeRenderer=SpaceOfChaos.INSTANCE.uiShapeRenderer;
                shapeRenderer.setColor(Color.BLACK);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                String longest= itemInfo.stream().reduce((s, s2) -> s.length()<s2.length()?s2:s).get();
                GlyphLayout layout=new GlyphLayout(font,longest);
                shapeRenderer.rect(mp.x+28,mp.y-20*itemInfo.size()+24,layout.width+14,20*itemInfo.size());
                shapeRenderer.end();
                spriteBatch.begin();
                for (int i = 0; i < itemInfo.size(); i++) {
                    String next=itemInfo.get(i);
                    font.draw(spriteBatch,next, mp.x+30, mp.y-20*i+20);

                }
                spriteBatch.end();
            }
        }
    }
}
