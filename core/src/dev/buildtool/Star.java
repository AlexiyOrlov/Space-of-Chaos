package dev.buildtool;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Star implements SaveData{
    private Texture texture=SpaceOfChaos.INSTANCE.starTextures.get(0);
    public String name;
    private final Circle area=new Circle(0,0,256);
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

    public Star() {
    }

    public Star(Texture texture) {
        this.texture = SpaceOfChaos.INSTANCE.starTextures.get(0);
        String randomName=starNames.iterator().next();
        name=randomName;
        starNames.remove(randomName);
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer)
    {
        spriteBatch.begin();
        spriteBatch.draw(texture,0,0);
        GlyphLayout layout=new GlyphLayout(SpaceOfChaos.INSTANCE.bitmapFont,name);
        SpaceOfChaos.INSTANCE.bitmapFont.draw(spriteBatch,name,256-layout.width/2,256);
        spriteBatch.end();

        if(SpaceOfChaos.debugDraw){
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.circle(area.x+texture.getWidth()/2,area.y+texture.getHeight()/2,area.radius);
            shapeRenderer.end();
        }
    }

    @Override
    public Map<String, Object> getData() {
        HashMap<String,Object> data=new HashMap<>();
        data.put("name",name);
        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        name= (String) data.get("name");
    }
}
