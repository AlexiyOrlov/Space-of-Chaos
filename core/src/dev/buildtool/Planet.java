package dev.buildtool;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;

public class Planet {
    private final Texture texture;
    public float x,y,currentAngle,speed;
    private final int distance;
    public TreeMap<Ware,Integer> warePrices;
    public TreeMap<Ware,Integer> wareAmounts;
    public float radius;
    public Circle outline;
    public boolean isInhabited;
    private static final Random random=new Random();
    public ArrayList<Resource> resources;
    public int size;
    public int explorationProgress;
    public Inventory equipmentInventory;

    public Planet(Texture texture, int distance, float angle, float orbitSpeed, boolean inhabited) {
        //TODO fix
        this.texture = texture;
        resources=new ArrayList<>();
//        this.x = (float) (distance* Math.cos(angle));
//        this.y = (float) (distance*Math.sin(angle));
        isInhabited=inhabited;
        this.x = (float) (distance* MathUtils.cos(angle))+ (float) texture.getWidth() /2;
        this.y = (float) (distance*MathUtils.sin(angle))+ (float) texture.getHeight() /2;
        currentAngle=angle;
        this.distance=distance;
        speed=orbitSpeed;
        if(inhabited) {
            warePrices = new TreeMap<>();
            wareAmounts = new TreeMap<>();
            Ware.WARES.forEach(ware -> {
                int basePrice = Ware.BASE_PRICES.get(ware);
                warePrices.put(ware, random.nextInt(basePrice / 3, basePrice * 3));
                wareAmounts.put(ware, random.nextInt(10, 500));
            });
            equipmentInventory=new Inventory(9);
            equipmentInventory.addItem(new Stack(ExplorationDrone.MARK1,1));
        }
        else {
            int resources=SpaceGame.random.nextInt(1,3);
            int resourcesGenerated=0;
            HashSet<Resource> resourceSet=new HashSet<>();
            while (resourcesGenerated<resources) {
                Resource randomResource=Resource.RESOURCES.get(random.nextInt(Resource.RESOURCES.size()));
                if(random.nextInt(100)<randomResource.chanceToOccur)
                {
                    if(!resourceSet.contains(randomResource)) {
                        resourcesGenerated++;
                        this.resources.add(randomResource);
                        resourceSet.add(randomResource);
                    }
                }
            }
        }
        size=random.nextInt(1000,10000);
        radius= (float) texture.getWidth() /2;
        outline=new Circle();
        outline.set(x,y,radius);
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer)
    {
        spriteBatch.begin();
        Functions.drawScaled(spriteBatch,texture,1,x-radius,y-radius);
        shapeRenderer.setColor(Color.YELLOW);
        //TODO correlate with outline
        shapeRenderer.ellipse(outline.x-radius,outline.y-radius, radius*2,radius*2);
        if(!isInhabited)
        {
            Matrix4 oldMatrix=spriteBatch.getTransformMatrix().cpy();
            Matrix4 matrix4=new Matrix4();
            matrix4.rotate(Vector3.Z,SpaceGame.INSTANCE.playerShip.rotation);
            matrix4.trn(x,y,0);

            spriteBatch.setTransformMatrix(matrix4);
            BitmapFont font = SpaceGame.INSTANCE.bitmapFont;
            GlyphLayout glyphLayout=SpaceGame.INSTANCE.textMeasurer;
            glyphLayout.setText(font,"Uninhabited");
            font.draw(spriteBatch,"Uninhabited",-glyphLayout.width/2,0);
            spriteBatch.setTransformMatrix(oldMatrix);
        }
        spriteBatch.end();
    }

    public void update()
    {
        currentAngle+=speed*MathUtils.degreesToRadians;
        this.x = (float) (distance* MathUtils.cos(currentAngle))+ (float) texture.getWidth() /2;
        this.y = (float) (distance*MathUtils.sin(currentAngle))+ (float) texture.getHeight() /2;
        outline.set(x,y,radius);
    }
}
