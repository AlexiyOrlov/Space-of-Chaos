package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Star {
    private final Texture texture;
    public final String name;
    private Circle area;
    static final HashSet<String> starNames=new HashSet<>();
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
        starNames.add("Neu");
        starNames.add("Phuns");
        starNames.add("Stroi");
        starNames.add("Fiusi");
        starNames.add("Keuri");
        starNames.add("Trua");
        starNames.add("Zrar");
        starNames.add("Kruan");
        starNames.add("Laib");
        starNames.add("Phaachud");
        starNames.add("Bellu");
        starNames.add("Riun");
        starNames.add("Strir");
        starNames.add("Kots");
        starNames.add("Lif");
        starNames.add("Kuqua");
        starNames.add("Thiks");
        starNames.add("Voi");
        starNames.add("Klilt");
        starNames.add("Glaida");
        starNames.add("Yunur");
        starNames.add("Kru");
        starNames.add("Wuam");
        starNames.add("Yaf");
        starNames.add("Owoy");
        starNames.add("Triulsam");
        starNames.add("Laidsek");
        starNames.add("Gletteb");
    }

    public Star(Texture texture) {
        this.texture = texture;
        String randomName=starNames.iterator().next();
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
