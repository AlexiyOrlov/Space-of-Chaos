package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class TextImageButton extends ImageTextButton {
    public TextImageButton(String text, Skin skin,Texture texture) {
        super(text, skin);
        clearChildren();
        if(texture!=null)
            add(new Image(texture));
        add(getLabel());
    }
}
