package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

public class Star {
    private final Texture texture;
    public final String name;
    static final ArrayList<String> starNames=new ArrayList<>();
    static {
        starNames.add("Fi");
        starNames.add("Chaak");
        starNames.add("Cuapret");
        starNames.add("Hummuan");
        starNames.add("Pruf");
        starNames.add("Vay");
        starNames.add("Ram");
        starNames.add("Estraiklu");
        starNames.add("Klus");
        starNames.add("Trem");
        starNames.add("Sro");
        starNames.add("Azusdom");
        starNames.add("Nairof");
    }

    public Star(Texture texture) {
        this.texture = texture;
        String randomName=starNames.get(SpaceGame.random.nextInt(starNames.size()));
        name=randomName;
        starNames.remove(randomName);
    }

    public void draw(SpriteBatch spriteBatch)
    {
        spriteBatch.begin();
        spriteBatch.draw(texture,-texture.getWidth()/2,-texture.getHeight()/2);
        spriteBatch.end();
    }
}
