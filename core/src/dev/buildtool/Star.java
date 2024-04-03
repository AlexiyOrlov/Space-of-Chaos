package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

import java.util.ArrayList;

public class Star {
    private final Texture texture;
    public final String name;
    private Circle area;
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
        area=new Circle(0,0,256);
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer)
    {
        spriteBatch.begin();
        spriteBatch.draw(texture,-texture.getWidth()/2,-texture.getHeight()/2);
        spriteBatch.end();

        if(SpaceGame.debugDraw){
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.circle(area.x,area.y,area.radius);
            shapeRenderer.end();
        }
    }
}
