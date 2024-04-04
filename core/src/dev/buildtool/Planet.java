package dev.buildtool;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;

public class Planet {
    private final Texture texture;
    public float x,y,currentAngle,speed;
    private final int distance;
    public TreeMap<Ware,Integer> warePrices;
    public TreeMap<Ware,Integer> wareAmounts;
    public HashMap<Ware,Float> wareManufactureProgress=new HashMap<>();
    public float radius;
    public Circle outline;
    public boolean isInhabited;
    private static final Random random=new Random();
    public ArrayList<Resource> resources;
    public int size;
    public int explorationProgress;
    public Inventory equipmentInventory;
    public final String name;
    private static final ArrayList<String> planetNames=new ArrayList<>();
    public ArrayList<NPCPilot> ships=new ArrayList<>();
    public StarSystem starSystem;
    static {
        planetNames.add("Ankeigantu");
        planetNames.add("Caruta");
        planetNames.add("Namacarro");
        planetNames.add("Bochone");
        planetNames.add("Uatis");
        planetNames.add("Cetinus");
        planetNames.add("Dingorth");
        planetNames.add("Kaolara");
        planetNames.add("Demostea");
        planetNames.add("Tonkerth");
        planetNames.add("Dategantu");
        planetNames.add("Noria");
        planetNames.add("Comia");
        planetNames.add("Thizenus");
        planetNames.add("Sastea");
        planetNames.add("Disucarro");
        planetNames.add("Elvichi");
        planetNames.add("Carth");
        planetNames.add("Machichi");
        planetNames.add("Bitania");
        planetNames.add("Gyria");
        planetNames.add("Mao");
        planetNames.add("Inrepra");
        planetNames.add("Gelvevis");
        planetNames.add("Pabbone");
        planetNames.add("Munus");
        planetNames.add("Soyama");
        planetNames.add("Drixorus");
        planetNames.add("Yennore");
        planetNames.add("Igawa");
        planetNames.add("Aenov");
        planetNames.add("Treiphus");
        planetNames.add("Dapus");
        planetNames.add("Testea");
        planetNames.add("Enronus");
        planetNames.add("Treuruta");
        planetNames.add("Zapus");
        planetNames.add("Ungyria");
        planetNames.add("Chocugantu");
        planetNames.add("Zorix");
        planetNames.add("Onzaphus");
        planetNames.add("Melruhines");
        planetNames.add("Deotune");
        planetNames.add("Billes");
        planetNames.add("Phillon");
        planetNames.add("Yuchiorus");
        planetNames.add("Ilvoth");
        planetNames.add("Yathea");
        planetNames.add("Lade");
        planetNames.add("Gnion");
        planetNames.add("Culebos");
        planetNames.add("Enkara");
        planetNames.add("Chuibos");
        planetNames.add("Kiunus");
        planetNames.add("Vesuturu");
        planetNames.add("Vara");
        planetNames.add("Nichi");
        planetNames.add("Cognonus");
        planetNames.add("Danvailara");
        planetNames.add("Ceninda");
        planetNames.add("Heter");
        planetNames.add("Kaima");
        planetNames.add("Bichi");
        planetNames.add("Chankande");
        planetNames.add("Tellion");
        planetNames.add("Violara");
        planetNames.add("Cipso");
        planetNames.add("Strorix");

    }

    private ArrayList<NPCPilot> shipsToRemove;

    public Planet(Texture texture, int distance, float angle, float orbitSpeed, boolean inhabited, StarSystem belongsTo) {
        this.texture = texture;
        starSystem=belongsTo;
        shipsToRemove=new ArrayList<>();
        String randomName=planetNames.get(random.nextInt(planetNames.size()));
        name=randomName;
        planetNames.remove(randomName);
        resources=new ArrayList<>();
        isInhabited=inhabited;
        this.x = (float) (distance* MathUtils.cos(angle))+ (float) texture.getWidth() /2;
        this.y = (float) (distance*MathUtils.sin(angle))+ (float) texture.getHeight() /2;
        currentAngle=angle;
        this.distance=distance;
        speed=orbitSpeed;
        if(inhabited) {
            warePrices = new TreeMap<>();
            wareAmounts = new TreeMap<>();
            equipmentInventory=new Inventory(9);
            equipmentInventory.addItem(new Stack(ExplorationDrone.MARK1,1));
//            if(random.nextBoolean())
//            {
//                NPCPilot npcPilot=new NPCPilot(this);
//                ships.add(npcPilot);
//            }
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
        spriteBatch.end();

        if(SpaceGame.debugDraw) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.circle(outline.x, outline.y, radius);
            shapeRenderer.end();
        }
    }

    public void drawName(SpriteBatch spriteBatch){
        spriteBatch.begin();
        Matrix4 oldMatrix=spriteBatch.getTransformMatrix().cpy();
        Matrix4 matrix4=new Matrix4();
        matrix4.rotate(Vector3.Z,SpaceGame.INSTANCE.playerShip.rotation);
        matrix4.trn(x,y,0);
        BitmapFont font = SpaceGame.INSTANCE.bitmapFont;
        GlyphLayout glyphLayout=SpaceGame.INSTANCE.textMeasurer;
        spriteBatch.setTransformMatrix(matrix4);
        if(!isInhabited)
        {
            glyphLayout.setText(font,"Uninhabited");
            font.draw(spriteBatch,"Uninhabited",-glyphLayout.width/2,-30);
        }
        glyphLayout.setText(font,name);
        font.draw(spriteBatch,name,-glyphLayout.width/2,0);
        spriteBatch.setTransformMatrix(oldMatrix);
        spriteBatch.end();
    }

    public void update(float deltaTime)
    {
        currentAngle+=speed*MathUtils.degreesToRadians;
        this.x = (distance* MathUtils.cos(currentAngle))+ (float) texture.getWidth() /2;
        this.y = (distance*MathUtils.sin(currentAngle))+ (float) texture.getHeight() /2;
        outline.set(x,y,radius);
        if(isInhabited) {
            ships.forEach(npcPilot -> {
                npcPilot.workOnPlanet(deltaTime);
                if (npcPilot.currentlyLandedOn==null)
                    shipsToRemove.add(npcPilot);
            });
            ships.removeAll(shipsToRemove);
            starSystem.ships.addAll(shipsToRemove);
            shipsToRemove.clear();

            Ware.WARES.forEach(ware -> {
                int currentWareCount=wareAmounts.get(ware);
                if(currentWareCount<Ware.MAXIMUM_WARE_AMOUNT)
                {
                    float manufactureProgress=wareManufactureProgress.getOrDefault(ware,10f);
                    if(manufactureProgress<=0)
                    {
                        wareAmounts.compute(ware,(ware1, integer) -> integer+1);
                        manufactureProgress=10;
                    }
                    else {
                        manufactureProgress-=Ware.MANUFACTURING_SPEED.get(ware)*deltaTime*60;
                    }
                    wareManufactureProgress.put(ware,manufactureProgress);
                }
            });
        }
    }
}
