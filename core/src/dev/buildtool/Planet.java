package dev.buildtool;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;

import dev.buildtool.weapons.WeaponRegistry;

public class Planet {
    private final Texture texture;
    public float x,y,currentAngle,speed;
    private final int distance;
    public TreeMap<Ware,Integer> warePrices;
    public TreeMap<Ware,Integer> wareAmounts;
    public HashMap<Ware,Float> wareManufactureProgress=new HashMap<>();
    public float radius;
    public Circle outline;
    private static final Random random=new Random();
    public ArrayList<Resource> resources;
    public int size;
    public int explorationProgress;
    public Inventory equipmentInventory;
    public final String name;
    private static final HashSet<String> planetNames=new HashSet<>();
    public ArrayList<NPCPilot> ships=new ArrayList<>();
    public StarSystem starSystem;
    private final boolean clockWise;

    private final ArrayList<NPCPilot> shipsToRemove;
    public Kind kind;
    private float shipManufacturingTime=15*60;

    public enum Kind{
        INHABITED,
        UNINHABITED,
        OCCUPIED
    }
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
        planetNames.add("Yenvaitun");
        planetNames.add("Hallos");
        planetNames.add("Nerth");
        planetNames.add("Silles");
        planetNames.add("Gephus");
        planetNames.add("Yater");
        planetNames.add("Galara");
        planetNames.add("Obone");
        planetNames.add("Vibos");
        planetNames.add("Teater");
        planetNames.add("Dagavis");
        planetNames.add("Lars");
        planetNames.add("Zurn");
        planetNames.add("Hegrilia");
        planetNames.add("Bobbes");
        planetNames.add("Daciama");
        planetNames.add("Velea");
        planetNames.add("Mora");
        planetNames.add("Sicuri");
        planetNames.add("Beiter");
        planetNames.add("Vichi");
        planetNames.add("Liea");
        planetNames.add("Mone");
        planetNames.add("Zoruta");
        planetNames.add("Drars");
        planetNames.add("Ezotov");
        planetNames.add("Chundore");
        planetNames.add("Kegawa");
        planetNames.add("Davanope");
        planetNames.add("Trosie");
        planetNames.add("Taulea");
        planetNames.add("Hachadus");
        planetNames.add("Vars");
        planetNames.add("Anvayama");
        planetNames.add("Kiozuno");
        planetNames.add("Cemirilia");
        planetNames.add("Somia");
        planetNames.add("Seron");
        planetNames.add("Vanvides");
        planetNames.add("Anides");
        planetNames.add("Serus");
        planetNames.add("Ahugawa");
        planetNames.add("Gnippe");
        planetNames.add("Sungocarro");
        planetNames.add("Lesitov");
        planetNames.add("Zeron");
        planetNames.add("Indi");
        planetNames.add("Dion");
        planetNames.add("Toto");
        planetNames.add("Llarenus");
        planetNames.add("Zeon");
        planetNames.add("Kamia");
        planetNames.add("Oter");
        planetNames.add("Paumia");
        planetNames.add("Phao");
        planetNames.add("Melanus");
        planetNames.add("Ocichi");
        planetNames.add("Vatrapus");
        planetNames.add("Molmion");
        planetNames.add("Sion");
        planetNames.add("Dithomia");
        planetNames.add("Nelea");
        planetNames.add("Silia");
        planetNames.add("Inzion");
        planetNames.add("Itrillion");
        planetNames.add("Pater");
        planetNames.add("Trypso");
        planetNames.add("Thauter");
        planetNames.add("Odion");
        planetNames.add("Zara");
        planetNames.add("Valiv");
        planetNames.add("Haowei");
    }

    public Planet(Texture texture, int distance, float angle, float orbitSpeed, StarSystem belongsTo, Kind kind) {
        this.texture = texture;
        starSystem=belongsTo;
        shipsToRemove=new ArrayList<>();
        this.kind=kind;
        String randomName=planetNames.iterator().next();
        name=randomName;
        planetNames.remove(randomName);
        resources=new ArrayList<>();
        this.x = (distance* MathUtils.cos(angle)) + (float) texture.getWidth() /2;
        this.y = (distance*MathUtils.sin(angle)) + (float) texture.getHeight() /2;
        currentAngle=angle;
        this.distance=distance;
        speed=orbitSpeed;
        if(kind==Kind.INHABITED) {
            warePrices = new TreeMap<>();
            wareAmounts = new TreeMap<>();
            equipmentInventory=new Inventory(9);
            equipmentInventory.addItem(new Stack(ExplorationDrone.MARK1,1));
            equipmentInventory.addItem(new Stack(Engine.MARK2,1));
            equipmentInventory.addItem(new Stack(Hull.HORNET,1));
            equipmentInventory.addItem(new Stack(Hull.BUMBLEBEE,1));
            equipmentInventory.addItem(new Stack(WeaponRegistry.SHOTGUN,1));
            equipmentInventory.addItem(new Stack(WeaponRegistry.MACHINE_GUN,1));
        } else if (kind == Kind.OCCUPIED) {
            shipManufacturingTime=random.nextInt(15*60,25*60);
        } else {
            int resources= SpaceOfChaos.random.nextInt(1,3);
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
        clockWise=random.nextBoolean();
        if(kind==Kind.INHABITED) {
            int guardCount=0;
            for (int i = 0; i < 2; i++) {
                if (random.nextBoolean()) {
                    NPCPilot guard = new NPCPilot(PilotAI.GUARD, WeaponRegistry.SHOTGUN, Hull.battleHulls.get(random.nextInt(Hull.battleHulls.size())), Engine.ENGINE_3, SideThrusters.SLOW, this,WeaponRegistry.AI_GUN1);
                    starSystem.ships.add(guard);
                    guard.x = x;
                    guard.y = y;
                    guardCount++;
                }
            }
            if (guardCount == 0) {
                if (random.nextBoolean()) {
                    NPCPilot pirate = new NPCPilot(PilotAI.PIRATE, WeaponRegistry.AI_GUN1, Hull.pirateHulls.get(random.nextInt(Hull.pirateHulls.size())), Engine.BASIC, SideThrusters.BASIC, this);
                    starSystem.ships.add(pirate);
                    pirate.x=x;
                    pirate.y=y;
                    System.out.println("Pirate in "+starSystem.star.name);
                }
            }

            if(random.nextBoolean())
            {
                NPCPilot trader=new NPCPilot(this,PilotAI.TRADER,WeaponRegistry.GUN,Hull.tradingHulls.get(random.nextInt(Hull.tradingHulls.size())), Engine.SLOW,SideThrusters.SLOW);
                ships.add(trader);
                starSystem.ships.add(trader);
            }
        } else if (kind == Kind.OCCUPIED) {
            int randomInt=random.nextInt(100);
            NPCPilot aiPilot;
            if(randomInt<25) {
                aiPilot = new NPCPilot(this, PilotAI.AI, WeaponRegistry.GUN,random.nextBoolean()?Hull.AI_SMALL1:Hull.AI_SMALL2,Engine.MARK2,SideThrusters.BASIC);
            } else if (randomInt < 50) {
                aiPilot = new NPCPilot(this, PilotAI.AI, WeaponRegistry.SHOTGUN,random.nextBoolean()?Hull.AI_MEDIUM1:Hull.AI_MEDIUM2,Engine.BASIC,SideThrusters.BASIC);
            } else if (randomInt < 75) {
                aiPilot=new NPCPilot(this,PilotAI.AI,WeaponRegistry.SHOTGUN,random.nextBoolean()?Hull.AI_BIG1:Hull.AI_BIG2,Engine.SLOW,SideThrusters.BASIC);
            }
            else
                aiPilot=new NPCPilot(this,PilotAI.AI,WeaponRegistry.AI_GUN1, random.nextBoolean()?Hull.AI_LARGE1:Hull.AI_LARGE2,Engine.SLOW,SideThrusters.SLOW);
            aiPilot.x=x;
            aiPilot.y=y;
            starSystem.ships.add(aiPilot);
        }
    }

    public void draw(SpriteBatch spriteBatch, ShapeRenderer shapeRenderer)
    {
        spriteBatch.begin();
        Functions.drawScaled(spriteBatch,texture,1,x-radius,y-radius);
        spriteBatch.end();

        if(SpaceOfChaos.debugDraw) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.circle(outline.x, outline.y, radius);
            shapeRenderer.end();
        }
    }

    public void drawName(SpriteBatch spriteBatch){
        spriteBatch.begin();
//        Matrix4 oldMatrix=spriteBatch.getTransformMatrix().cpy();
//        Matrix4 matrix4=new Matrix4();
//        if(SpaceGame.INSTANCE.playerShip!=null)
//            matrix4.rotate(Vector3.Z,SpaceGame.INSTANCE.playerShip.rotation);
//        matrix4.trn(x,y,0);
        BitmapFont font = SpaceOfChaos.INSTANCE.bitmapFont;
        GlyphLayout glyphLayout= SpaceOfChaos.INSTANCE.textMeasurer;
//        spriteBatch.setTransformMatrix(matrix4);
        if(kind==Kind.UNINHABITED)
        {
            glyphLayout.setText(font,"Uninhabited");
            font.draw(spriteBatch,"Uninhabited",x-glyphLayout.width/2,y-30);
        }
        glyphLayout.setText(font,name);
        font.draw(spriteBatch,name,x-glyphLayout.width/2,y);
//        spriteBatch.setTransformMatrix(oldMatrix);
        spriteBatch.end();
    }

    public void update(float deltaTime)
    {
        if(clockWise)
            currentAngle+=speed*MathUtils.degreesToRadians;
        else
            currentAngle-=speed*MathUtils.degreesToRadians;
        this.x = (distance* MathUtils.cos(currentAngle))+ (float) texture.getWidth() /2;
        this.y = (distance*MathUtils.sin(currentAngle))+ (float) texture.getHeight() /2;
        outline.set(x,y,radius);
        if(kind==Kind.INHABITED) {
            ships.forEach(npcPilot -> {
                npcPilot.workOnPlanet(deltaTime, this);
                if (!npcPilot.landed)
                {
                    shipsToRemove.add(npcPilot);
                }
            });
            ships.removeAll(shipsToRemove);
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
            if(shipManufacturingTime<=0)
            {
                long totalHumanShips=0;
                for (StarSystem system : SpaceOfChaos.INSTANCE.starSystems) {
                    long humanShips=system.ships.stream().filter(ship -> ship instanceof NPCPilot npcPilot &&npcPilot.pilotAI!=PilotAI.AI).count();
                    totalHumanShips+=humanShips;
                }
                if(totalHumanShips<SpaceOfChaos.INSTANCE.starSystems.size()* 3L)
                {
                    shipManufacturingTime=random.nextInt(15*60,25*60);
                    NPCPilot newPilot;
                    int ri=random.nextInt(100);
                    if(ri<33)
                    {
                        newPilot=new NPCPilot(PilotAI.PIRATE,random.nextBoolean()?WeaponRegistry.AI_GUN1:WeaponRegistry.SHOTGUN,Hull.pirateHulls.get(random.nextInt(Hull.pirateHulls.size())),Engine.engines.get(random.nextInt(Engine.engines.size())),SideThrusters.BASIC,this);
                    } else if (ri < 66) {
                        newPilot=new NPCPilot(PilotAI.GUARD,random.nextBoolean()?WeaponRegistry.SHOTGUN:WeaponRegistry.MACHINE_GUN,Hull.battleHulls.get(random.nextInt(Hull.battleHulls.size())), Engine.engines.get(random.nextInt(Engine.engines.size())),SideThrusters.BASIC,this);
                    }
                    else {
                        newPilot=new NPCPilot(PilotAI.TRADER,random.nextBoolean()?WeaponRegistry.GUN: WeaponRegistry.AI_GUN1,Hull.tradingHulls.get(random.nextInt(Hull.tradingHulls.size())),Engine.SLOW,SideThrusters.SLOW,this);
                    }
                    newPilot.x=x;
                    newPilot.y=y;
                    starSystem.ships.add(newPilot);
                    Functions.log("Ship produced");
                }


            }
        } else if (kind==Kind.OCCUPIED) {
            if(shipManufacturingTime<=0)
            {
                shipManufacturingTime=random.nextInt(15*60,25*60);
                NPCPilot npcPilot;
                int randomInt=random.nextInt(100);
                if(randomInt<25)
                {
                    npcPilot=new NPCPilot(PilotAI.AI,random.nextBoolean()?WeaponRegistry.GUN:WeaponRegistry.CLUSTER_GUN,random.nextBoolean()?Hull.AI_SMALL1:Hull.AI_SMALL2,Engine.MARK2,SideThrusters.BASIC,this);
                } else if (randomInt <= 50) {
                    npcPilot=new NPCPilot(PilotAI.AI,random.nextBoolean()?WeaponRegistry.AI_GUN1:WeaponRegistry.CLUSTER_GUN,random.nextBoolean()?Hull.AI_MEDIUM2:Hull.AI_MEDIUM1,Engine.BASIC,SideThrusters.BASIC,this);
                } else if (randomInt <= 75) {
                    npcPilot=new NPCPilot(PilotAI.AI,random.nextBoolean()?WeaponRegistry.MACHINE_GUN:WeaponRegistry.SHOTGUN,random.nextBoolean()?Hull.AI_BIG1:Hull.AI_BIG2,Engine.SLOW,SideThrusters.SLOW,this);
                }
                else {
                    npcPilot=new NPCPilot(PilotAI.AI,random.nextBoolean()?WeaponRegistry.MACHINE_GUN:WeaponRegistry.CLUSTER_GUN,random.nextBoolean()?Hull.AI_LARGE1:Hull.AI_LARGE2,Engine.SLOW,SideThrusters.SLOW,this);
                }
                npcPilot.x=x;
                npcPilot.y=y;
                Functions.log("Produced AI pilot in "+starSystem.getStarName());
                starSystem.ships.add(npcPilot);
            }
        }

        if(shipManufacturingTime>0)
        {
            shipManufacturingTime-=deltaTime;
        }
    }
}
