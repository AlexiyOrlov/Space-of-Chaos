package dev.buildtool;

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
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import dev.buildtool.weapons.WeaponRegistry;

public class Planet implements SaveData {
    private Texture texture;
    public float x,y,currentAngle,speed;
    private int distance;
    public TreeMap<Ware,Integer> warePrices=new TreeMap<>();
    public TreeMap<Ware,Integer> wareAmounts=new TreeMap<>();
    public HashMap<Ware,Float> wareManufactureProgress=new HashMap<>();
    public float radius;
    public Circle outline=new Circle();
    private static final Random random=new Random();
    public ArrayList<Resource> resources=new ArrayList<>();
    public int size;
    public int explorationProgress;
    public Inventory equipmentInventory=new Inventory(9);
    public String name;
    private static final HashSet<String> planetNames=new HashSet<>();
    public ArrayList<NPCPilot> ships=new ArrayList<>();
    public StarSystem starSystem;
    private boolean clockWise;

    private ArrayList<NPCPilot> shipsToRemove=new ArrayList<>();
    public Kind kind;
    private float shipManufacturingTime=15*60;
    public int id;

    @Override
    public Map<String, Object> getData() {
        HashMap<String,Object> data=new HashMap<>();
        data.put("texture",SpaceOfChaos.INSTANCE.textureHashMap.inverse().get(texture));
        data.put("clockwise",clockWise);
        data.put("angle",currentAngle);
        data.put("distance",distance);
        data.put("equipment",equipmentInventory.getData());
        data.put("exploration progress",explorationProgress);
        data.put("type",kind.toString());
        data.put("name",name);
        data.put("outline x",outline.x);
        data.put("outline y",outline.y);
        data.put("radius",radius);
        for (int i = 0; i < resources.size(); i++) {
            Resource resource=resources.get(i);
            data.put("resource "+i,resource.id);
        }
        data.put("resource count",resources.size());
        data.put("ship manufacturing time",shipManufacturingTime);
        for (int i = 0; i < ships.size(); i++) {
            NPCPilot ship=ships.get(i);
            data.put("ship "+i,ship.getData());
        }
        data.put("ships",ships.size());
        data.put("speed",speed);
        //star system
        data.put("texture",SpaceOfChaos.INSTANCE.textureHashMap.inverse().get(texture));
        int next=0;
        for (Ware ware : wareAmounts.keySet()) {
            data.put("ware "+next,ware.name);
            data.put("amount "+next,wareAmounts.get(ware));
            next++;
        }
        data.put("ware amounts",wareAmounts.size());
        next=0;
        for (Ware ware : wareManufactureProgress.keySet()) {
            data.put("ware manufactured "+next,ware.name);
            data.put("progress "+next,wareManufactureProgress.get(ware));
            next++;
        }
        data.put("ware manufacturing size",wareManufactureProgress.size());
        next=0;
        for (Ware ware : warePrices.keySet()) {
            data.put("ware sold "+next,ware.name);
            data.put("price "+next,warePrices.get(ware));
            next++;
        }
        data.put("ware sold count",warePrices.size());
        data.put("id",id);
        return data;
    }

    @Override
    public void load(Map<String, Object> data) {
        texture=SpaceOfChaos.INSTANCE.textureHashMap.get((Integer) data.get("texture"));
        clockWise= (boolean) data.get("clockwise");
        currentAngle= (float)(double) data.get("angle");
        distance= (int) data.get("distance");
        equipmentInventory.load((Map<String, Object>) data.get("equipment"));
        explorationProgress= (int) data.get("exploration progress");
        kind= Kind.valueOf((String) data.get("type"));
        name= (String) data.get("name");
        outline.x= (float)(double) data.get("outline x");
        outline.y= (float)(double) data.get("outline y");
        radius= (float)(double) data.get("radius");
        int resourceCount= (int) data.get("resource count");
        for (int i = 0; i < resourceCount; i++) {
            Resource resource=Resource.ids.get((int) data.get("resource "+i));
            resources.add(resource);
        }
        shipManufacturingTime= (float)(double) data.get("ship manufacturing time");
        int shipCount= (int) data.get("ships");
        for (int i = 0; i < shipCount; i++) {
            NPCPilot npcPilot=new NPCPilot();
            npcPilot.load((Map<String, Object>) data.get("ship "+i));
        }
        speed= (float)(double) data.get("speed");
        texture=SpaceOfChaos.INSTANCE.textureHashMap.get((int) data.get("texture"));
        int wareAmountCount= (int) data.get("ware amounts");
        for (int i = 0; i < wareAmountCount; i++) {
            Ware ware= (Ware) Item.REGISTRY.get((String) data.get("ware "+i));
            wareAmounts.put(ware, (Integer) data.get("amount "+i));
        }
        int manufacturedWareAmount= (int) data.get("ware manufacturing size");
        for (int i = 0; i < manufacturedWareAmount; i++) {
            Ware ware= (Ware) Item.REGISTRY.get((String) data.get("ware manufactured "+i));
            double p= (double) data.get("progress "+i);
            wareManufactureProgress.put(ware, (float) p);
        }
        int waresSold= (int) data.get("ware sold count");
        for (int i = 0; i < waresSold; i++) {
            Ware ware=(Ware) Item.REGISTRY.get((String) data.get("ware sold "+i));
            warePrices.put(ware, (Integer) data.get("price "+i));
        }
        id= (int) data.get("id");
    }

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

    public Planet() {
    }

    public Planet(Texture texture, int distance, float angle, float orbitSpeed, StarSystem belongsTo, Kind kind, int id) {
        this.id=id;
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
        outline.set(x,y,radius);
        clockWise=random.nextBoolean();
        if(kind==Kind.INHABITED) {
            int guardCount=0;
            for (int i = 0; i < 2; i++) {
                if (random.nextBoolean()) {
                    NPCPilot guard = new NPCPilot(PilotAI.GUARD, WeaponRegistry.SHOTGUN, Hull.battleHulls.get(random.nextInt(Hull.battleHulls.size())), Engine.ENGINE_3, SideThrusters.MARK2, this,WeaponRegistry.AI_GUN1, starSystem);
                    starSystem.ships.add(guard);
                    guard.x = x;
                    guard.y = y;
                    guardCount++;
                }
            }
            if (guardCount == 0) {
                if (random.nextBoolean()) {
                    NPCPilot pirate = new NPCPilot(PilotAI.PIRATE, WeaponRegistry.AI_GUN1, Hull.pirateHulls.get(random.nextInt(Hull.pirateHulls.size())), Engine.BASIC, SideThrusters.MARK3, this,starSystem );
                    starSystem.ships.add(pirate);
                    pirate.x=x;
                    pirate.y=y;
                    Functions.log("Pirate in "+starSystem.star.name);
                }
            }

            if(random.nextBoolean())
            {
                NPCPilot trader=new NPCPilot(this,PilotAI.TRADER,WeaponRegistry.GUN,Hull.tradingHulls.get(random.nextInt(Hull.tradingHulls.size())), Engine.SLOW,SideThrusters.SLOW, starSystem,this );
                ships.add(trader);
                starSystem.ships.add(trader);
            }
        } else if (kind == Kind.OCCUPIED) {
            int randomInt=random.nextInt(100);
            NPCPilot aiPilot;
            if(randomInt<25) {
                aiPilot = new NPCPilot( PilotAI.AI, WeaponRegistry.GUN,random.nextBoolean()?Hull.AI_SMALL1:Hull.AI_SMALL2,Engine.MARK2,SideThrusters.BASIC,this, starSystem);
            } else if (randomInt < 50) {
                aiPilot = new NPCPilot( PilotAI.AI, WeaponRegistry.SHOTGUN,random.nextBoolean()?Hull.AI_MEDIUM1:Hull.AI_MEDIUM2,Engine.BASIC,SideThrusters.BASIC,this,starSystem );
            } else if (randomInt < 75) {
                aiPilot=new NPCPilot(PilotAI.AI,WeaponRegistry.SHOTGUN,random.nextBoolean()?Hull.AI_BIG1:Hull.AI_BIG2,Engine.SLOW,SideThrusters.BASIC,this, starSystem);
            }
            else
                aiPilot=new NPCPilot(PilotAI.AI,WeaponRegistry.AI_GUN1, random.nextBoolean()?Hull.AI_LARGE1:Hull.AI_LARGE2,Engine.SLOW,SideThrusters.SLOW,this,WeaponRegistry.MISSILE_LAUNCHER, starSystem);
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
                        newPilot=new NPCPilot(PilotAI.PIRATE,random.nextBoolean()?WeaponRegistry.AI_GUN1:WeaponRegistry.SHOTGUN,Hull.pirateHulls.get(random.nextInt(Hull.pirateHulls.size())),Engine.engines.get(random.nextInt(Engine.engines.size())),SideThrusters.BASIC,this,starSystem );
                    } else if (ri < 66) {
                        newPilot=new NPCPilot(PilotAI.GUARD,random.nextBoolean()?WeaponRegistry.SHOTGUN:WeaponRegistry.MACHINE_GUN,Hull.battleHulls.get(random.nextInt(Hull.battleHulls.size())), Engine.engines.get(random.nextInt(Engine.engines.size())),SideThrusters.BASIC,this, starSystem);
                    }
                    else {
                        newPilot=new NPCPilot(PilotAI.TRADER,random.nextBoolean()?WeaponRegistry.GUN: WeaponRegistry.AI_GUN1,Hull.tradingHulls.get(random.nextInt(Hull.tradingHulls.size())),Engine.SLOW,SideThrusters.SLOW,this, starSystem);
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
                    npcPilot=new NPCPilot(PilotAI.AI,random.nextBoolean()?WeaponRegistry.GUN:WeaponRegistry.CLUSTER_GUN,random.nextBoolean()?Hull.AI_SMALL1:Hull.AI_SMALL2,Engine.MARK2,SideThrusters.BASIC,this, starSystem);
                } else if (randomInt <= 50) {
                    npcPilot=new NPCPilot(PilotAI.AI,random.nextBoolean()?WeaponRegistry.AI_GUN1:WeaponRegistry.CLUSTER_GUN,random.nextBoolean()?Hull.AI_MEDIUM2:Hull.AI_MEDIUM1,Engine.BASIC,SideThrusters.BASIC,this, starSystem);
                } else if (randomInt <= 75) {
                    npcPilot=new NPCPilot(PilotAI.AI,random.nextBoolean()?WeaponRegistry.MACHINE_GUN:WeaponRegistry.SHOTGUN,random.nextBoolean()?Hull.AI_BIG1:Hull.AI_BIG2,Engine.SLOW,SideThrusters.SLOW,this, starSystem);
                }
                else {
                    npcPilot=new NPCPilot(PilotAI.AI,random.nextBoolean()?WeaponRegistry.MACHINE_GUN:WeaponRegistry.CLUSTER_GUN,random.nextBoolean()?Hull.AI_LARGE1:Hull.AI_LARGE2,Engine.SLOW,SideThrusters.SLOW,this, starSystem);
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
