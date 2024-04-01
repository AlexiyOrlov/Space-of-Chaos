package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Slot {
    Texture background;
    int x,y;
    TextureRegion backgroundTexture;
    private final int slotIndex;
    private final Inventory inventory;
    BitmapFont bitmapFont=SpaceGame.INSTANCE.bitmapFont;
    public boolean visible=true;

    public Slot(Texture background, int x, int y, int inventoryIndex, Inventory inventory) {
        this.background = background;
        this.x = x;
        this.y = y;
        backgroundTexture =new TextureRegion(background);
        slotIndex=inventoryIndex;
        this.inventory =inventory;
    }

    public void draw(SpriteBatch spriteBatch)
    {
        if(visible) {
            spriteBatch.draw(backgroundTexture, x, y, 0, 0, 32, 32, 2, 2, 0);
            Stack next = inventory.stacks[slotIndex];
            if (next != null) {
                spriteBatch.draw(next.item.texture, x, y);
                bitmapFont.draw(spriteBatch, next.count + "", x + 32, y - 2);
            }
        }
    }

    /**
     * @return clicked slot index
     */
    public int drawInfo(SpriteBatch spriteBatch,Viewport viewport)
    {
        if(visible) {
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.input.getY();
            Vector2 mousePositionInWorld = viewport.unproject(new Vector2(mouseX, mouseY));
            Stack next = inventory.stacks[slotIndex];
            if (next != null) {
                if (mousePositionInWorld.x > x && mousePositionInWorld.x < x + 64 && mousePositionInWorld.y > y && mousePositionInWorld.y < y + 64) {
                    bitmapFont.draw(spriteBatch, next.item.name, mousePositionInWorld.x + 20, mousePositionInWorld.y + 10);
                    if (Gdx.input.isTouched())
                        return slotIndex;
                }
            }
            if (mousePositionInWorld.x > x && mousePositionInWorld.x < x + 64 && mousePositionInWorld.y > y && mousePositionInWorld.y < y + 64) {
                if (Gdx.input.justTouched())
                    return slotIndex;
            }
        }
        return -1;
    }
}
