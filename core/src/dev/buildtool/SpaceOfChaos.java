package dev.buildtool;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.google.common.collect.HashBiMap;
import com.kotcrab.vis.ui.VisUI;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SpaceOfChaos extends Game implements SaveData{
	public static Random random=new Random();
	SpriteBatch worldBatch;
	SpriteBatch uiBatch;
	ArrayList<Texture> planetTextures;
	AssetManager assetManager;
	public ArrayList<StarSystem> starSystems=new ArrayList<>();
	public ArrayList<Texture> starTextures;
	public PlayerShip playerShip;
	Texture redStarshipTexture;
	public Texture scalesTexture,takeOffTexture,slotTexture,alcoholTexture,toolsTexture,carPartsTexture,firearmsTexture,
			jewelleryTexture, clothesTexture,electronicsTexture,waterTexture,furnitureTexture,foodTexture,medicineTexture,
		basicProjectile,ironOreTexture,copperOreTexture,diamondTexture,droneTexture1,gearTexture,slotTexture2,starIcon,
		targetTexture,skyTexture, tradingHull1Texture,starGateTexture,inhabitedPlanetIcon,uninhabitedPlanetIcon,
		battleHull2,pelletTexture,stargateIcon,battleHull3,pirateHull1,engine1Texture,engine2Texture,engine3Texture,shipIcon,
		slotTexture3,thrusters1Texture,thrusters2Texture,gunTexture,shotgunTexture,cashTexture,
		containerTexture,shipIcon3,blackHullTexture,blackHull2Texture,tradingHull2Texture,pirateHull2Texture,pirateHull3Texture,
		basicGunTexture,machineGunTexture,battleHull3Texture,aiSmallHull1,aiSmallHull2,aiMediumHull1,aiMediumHull2,aiBigHull1,
			aiBigHull2,aiLargeHull1,aiLargeHull2,redProjectileTexture,clusterGunTexture,greenCircle,yellowCircle,twoSwordsTexture,
		missileTexture,missileLauncherTexture,reticle,gatlingGunTexture,triShotTexture,aiBiggerHull1,aiBiggerHull2,targetRadar,
		allTargetRadar,shipDetector,priceScanner,wrenchTexture,tradingHull3,dragonflyHull,beetleHull,sideThrusters3,
		sideThrusters4,sideThrusters5,spaceStation,spaceStationIcon,ladyBugHull,cockroachHull,flyHull;
	public static SpaceOfChaos INSTANCE;
	public Skin skin;
	ShapeRenderer shapeRenderer,uiShapeRenderer;
	public BitmapFont bitmapFont;
	public GlyphLayout textMeasurer;
	public boolean updateWorld;
	static boolean debugDraw;
	public Sound machineGunSound,laserShotSound,blasterSound,shotGunSound,explosionSound,swishSound,shot2sound,drrrSound,
		stargateJump,aiGun,missileSound;
	private final ArrayList<Texture> textures=new ArrayList<>(600);
	private final ArrayList<Sound> sounds=new ArrayList<>(100);
	private float aiAttackTimer=random.nextInt(7*60,12*60);
	private float humanAttackTimer=random.nextInt(7*60,12*60);
	private Texture explosionSprite;
	public Animation<TextureRegion> explosionAnimation;
	SystemScreen systemScreen;
	private float systemCheckTime;
	private static int textureID;
	public HashBiMap<Integer,Texture> textureHashMap=HashBiMap.create(600);
	String dataDir=null;
	public HashMap<Integer,StarSystem> idMap=new HashMap<>();
	static int nextSystemId;
	Pixmap crossCursor;
	public static final int hullRepairCost=50;
	public  static boolean planetScreenHelpShown,systemScreenHelpShown,marketHelpShown;
	@Override
	public void create () {
		INSTANCE=this;
		if(UIUtils.isMac)
		{
			dataDir=System.getProperty("user.home")+"/Library/Application Support";
		} else if (UIUtils.isLinux) {
			dataDir=System.getProperty("user.home");
		} else if (UIUtils.isWindows) {
			dataDir=System.getenv("AppData");
		}
		Functions.log("Data directory is "+dataDir);
		textMeasurer=new GlyphLayout();
		FreeTypeFontGenerator generator=new FreeTypeFontGenerator(Gdx.files.internal("fonts/GentiumPlus-R.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter freeTypeFontParameter=new FreeTypeFontGenerator.FreeTypeFontParameter();
		freeTypeFontParameter.size=20;
		bitmapFont=generator.generateFont(freeTypeFontParameter);
		generator.dispose();
		bitmapFont.getData().markupEnabled=true;
		assetManager=new AssetManager();
		worldBatch = new SpriteBatch();
		uiBatch=new SpriteBatch();
		shapeRenderer=new ShapeRenderer();
		uiShapeRenderer=new ShapeRenderer();
		loadTexture("star");
		loadTexture("green planet");
		loadTexture("greenish planet");
		loadTexture("mixed planet");
		loadTexture("planet3");
		loadTexture("planet5");
		loadTexture("red ship");
		loadTexture("scales64");
		loadTexture("take off");
		loadTexture("blue square");
		loadTexture("projectile 1");
		loadTexture("iron ore");
		loadTexture("copper ore");
		loadTexture("resource");
		loadTexture("drone 1");
		loadTexture("gears64");
		loadTexture("slot");
		loadTexture("star icon");
		loadTexture("target_indicator");
		loadTexture("trade hull 1");
		loadTexture("battle hull 2");
		loadTexture("pellet");
		loadTexture("cash64");

		loadTexture("alcohol");
		loadTexture("tools");
		loadTexture("car parts");
		loadTexture("firearms");
		loadTexture("jewellery");
		loadTexture("clothes");
		loadTexture("electronics");
		loadTexture("water bottle");
		loadTexture("furniture");
		loadTexture("food");
		loadTexture("medicine");
		loadTexture("sky");
		loadTexture("star gate");
		loadTexture("inhabited planet");
		loadTexture("uninhabited planet");
		loadTexture("star gate icon");
		loadTexture("battle hull 1");
		loadTexture("pirate hull 1");
		loadTexture("engine1");
		loadTexture("engine2");
		loadTexture("engine3");
		loadTexture("ship icon");
		loadTexture("ship icon2");
		loadTexture("slot 2");
		loadTexture("thrusters 1");
		loadTexture("thrusters 2");
		loadTexture("gun");
		loadTexture("shotgun");
		loadTexture("drone 1");
		loadTexture("container");
		loadTexture("ship icon2");
		loadTexture("hull2");
		loadTexture("bumblebee hull");
		loadTexture("trading hull2");
		loadTexture("pirate hull2");
		loadTexture("pirate hull3");
		loadTexture("basic gun");
		loadTexture("machine gun");
		loadTexture("battle hull 3");
		loadTexture("ai big hull 1");
		loadTexture("ai big hull 2");
		loadTexture("ai hull small 2");
		loadTexture("ai hull small 1");
		loadTexture("ai large hull 1");
		loadTexture("ai large hull 2");
		loadTexture("ai medium hull 2");
		loadTexture("ai medium hull 1");
		loadTexture("projectile2");
		loadTexture("cluster gun");
		loadTexture("yellow circle");
		loadTexture("green circle");
		loadTexture("two swords");
		loadTexture("missile");
		loadTexture("gun4");
		loadTexture("small explosion");
		loadTexture("reticle");
		loadTexture("minigun");
		loadTexture("trishot");
		loadTexture("ai bigger hull1");
		loadTexture("ai bigger hull2");
		loadTexture("target radar");
		loadTexture("all target radar");
		loadTexture("ship detector");
		loadTexture("price scanner");
		loadTexture("wrench");
		loadTexture("trading hull3");
		loadTexture("dragonfly");
		loadTexture("beetle");
		loadTexture("side thrusters 3");
		loadTexture("side thrusters4");
		loadTexture("side thrusters 5");
		loadTexture("space station");
		loadTexture("space station icon");
		loadTexture("ladybug");
		loadTexture("cockroach");
		loadTexture("fly");
		assetManager.finishLoading();

		alcoholTexture=getTexture("alcohol");
		toolsTexture=getTexture("tools");
		carPartsTexture=getTexture("car parts");
		firearmsTexture=getTexture("firearms");
		jewelleryTexture=getTexture("jewellery");
		clothesTexture=getTexture("clothes");
		electronicsTexture=getTexture("electronics");
		waterTexture=getTexture("water bottle");
		furnitureTexture=getTexture("furniture");
		foodTexture=getTexture("food");
		medicineTexture=getTexture("medicine");

		basicProjectile=getTexture("projectile 1");
		copperOreTexture=getTexture("copper ore");
		ironOreTexture=getTexture("iron ore");
		diamondTexture=getTexture("resource");
		droneTexture1=getTexture("drone 1");
		gearTexture=getTexture("gears64");
		slotTexture2=getTexture("slot");
		starIcon=getTexture("star icon");
		targetTexture=getTexture("target_indicator");
		skyTexture=getTexture("sky");
		skyTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		tradingHull1Texture=getTexture("trade hull 1");
		starGateTexture=getTexture("star gate");
		battleHull2=getTexture("battle hull 2");
		pelletTexture=getTexture("pellet");
		engine1Texture=getTexture("engine1");
		engine2Texture=getTexture("engine2");
		engine3Texture=getTexture("engine3");
		slotTexture3=getTexture("slot 2");
		thrusters1Texture=getTexture("thrusters 1");
		thrusters2Texture=getTexture("thrusters 2");
		gunTexture=getTexture("gun");
		shotgunTexture=getTexture("shotgun");
		cashTexture=getTexture("cash64");
		containerTexture=getTexture("container");
		shipIcon3=getTexture("ship icon2");
		blackHullTexture=getTexture("hull2");
		blackHull2Texture=getTexture("bumblebee hull");
		pirateHull3Texture=getTexture("pirate hull3");
		basicGunTexture=getTexture("basic gun");
		machineGunTexture=getTexture("machine gun");

		starTextures=new ArrayList<>(3);
		starTextures.add(getTexture("star"));
		planetTextures=new ArrayList<>();
		planetTextures.add(getTexture("green planet"));
		planetTextures.add(getTexture("greenish planet"));
		planetTextures.add(getTexture("mixed planet"));
		planetTextures.add(getTexture("planet3"));
		planetTextures.add(getTexture("planet5"));
		inhabitedPlanetIcon=getTexture("inhabited planet");
		uninhabitedPlanetIcon=getTexture("uninhabited planet");
		stargateIcon=getTexture("star gate icon");
		battleHull3=getTexture("battle hull 1");
		pirateHull1=getTexture("pirate hull 1");
		shipIcon=getTexture("ship icon");
		tradingHull2Texture=getTexture("trading hull2");
		pirateHull2Texture=getTexture("pirate hull2");
		battleHull3Texture=getTexture("battle hull 3");

		takeOffTexture=getTexture("take off");
		scalesTexture=getTexture("scales64");
		redStarshipTexture=getTexture("red ship");
		slotTexture=getTexture("blue square");
		redProjectileTexture=getTexture("projectile2");
		clusterGunTexture= getTexture("cluster gun");

		aiSmallHull1=getTexture("ai hull small 1");
		aiSmallHull2=getTexture("ai hull small 2");
		aiMediumHull1=getTexture("ai medium hull 1");
		aiMediumHull2=getTexture("ai medium hull 2");
		aiBigHull1=getTexture("ai big hull 1");
		aiBigHull2=getTexture("ai big hull 2");
		aiLargeHull1=getTexture("ai large hull 1");
		aiLargeHull2=getTexture("ai large hull 2");
		skin=new Skin();
		skin.add("font",bitmapFont);
		skin.addRegions(new TextureAtlas(Gdx.files.internal("skins/cloud/cloud-form-ui.atlas")));
		skin.load(Gdx.files.internal("skins/cloud/cloud-form-ui.json"));
		VisUI.load();

		machineGunSound=loadSound("machine gun.wav");
		laserShotSound=loadSound("laser-shot.wav");
		blasterSound=loadSound("retro-shot-blaster.wav");
		shotGunSound=loadSound("shotgun-spas.mp3");
		explosionSound=loadSound("explosion.mp3");
		shot2sound=loadSound("uzi shot.wav");
		drrrSound=loadSound("drrr.ogg");
		stargateJump=loadSound("star gate.ogg");
		swishSound=loadSound("swish.wav");
		aiGun=loadSound("ai gun.ogg");
		missileSound=loadSound("missile.ogg");

		greenCircle=getTexture("green circle");
		yellowCircle=getTexture(("yellow circle"));
		twoSwordsTexture=getTexture("two swords");
		missileTexture=getTexture("missile");
		missileLauncherTexture=getTexture("gun4");
		explosionSprite=getTexture("small explosion");
		TextureRegion[][] frames=TextureRegion.split(explosionSprite,64,64);
		TextureRegion[] frames2=new TextureRegion[15];
		int index=0;
		for (int i = 0; i < 1; i++) {
			for (int j = 0; j < 15; j++) {
				frames2[index++]=frames[i][j];
			}
		}
		explosionAnimation=new Animation<>(0.066f,frames2);

		reticle=getTexture("reticle");
		gatlingGunTexture=getTexture("minigun");
		triShotTexture=getTexture("trishot");
		aiBiggerHull1=getTexture("ai bigger hull1");
		aiBiggerHull2=getTexture("ai bigger hull2");
		targetRadar=getTexture("target radar");
		allTargetRadar=getTexture("all target radar");
		shipDetector=getTexture("ship detector");
		priceScanner=getTexture("price scanner");
		wrenchTexture=getTexture("wrench");
		tradingHull3=getTexture("trading hull3");
		dragonflyHull=getTexture("dragonfly");
		beetleHull=getTexture("beetle");
		sideThrusters3=getTexture("side thrusters 3");
		sideThrusters4=getTexture("side thrusters4");
		sideThrusters5=getTexture("side thrusters 5");
		spaceStation=getTexture("space station");
		spaceStationIcon=getTexture("space station icon");
		ladyBugHull=getTexture("ladybug");
		cockroachHull=getTexture("cockroach");

		crossCursor=new Pixmap(Gdx.files.internal("textures/cross cursor.png"));

		flyHull=getTexture("fly");

		setScreen(new StartScreen(this));
	}

	private Sound loadSound(String soundName)
	{
		Sound sound=Gdx.audio.newSound(Gdx.files.internal("sounds/"+soundName));
		sounds.add(sound);
		return sound;
	}

	@Override
	public void dispose () {
		worldBatch.dispose();
		skin.dispose();
		uiBatch.dispose();
//		bitmapFont.dispose();
		textures.forEach(Texture::dispose);
		sounds.forEach(Sound::dispose);
		VisUI.dispose();
		crossCursor.dispose();
	}

	public void initialize(int systemCount)
	{
		starSystems=new ArrayList<>();
		Planet.initialize();
		Star.initialize();
		int x=0;
		int y=0;
		int planetCount=0;
		for (int i = 0; i <systemCount; i++) {
			int xleft=random.nextInt(-300,-100);
			int xright=random.nextInt(100,300);
			int yleft=random.nextInt(-300,-100);
			int yright=random.nextInt(100,300);
			while (true) {
				boolean recalculate=false;
				for (StarSystem starSystem : starSystems) {
					if (Vector2.dst(starSystem.positionX, starSystem.positionY, x, y) < 100) {
						if (random.nextBoolean())
							x += xleft;
						else
							x += xright;
						if (random.nextBoolean())
							y += yleft;
						else
							y += yright;
						recalculate=true;
					}
				}
				if(!recalculate)
					break;
			}
			StarSystem starSystem=new StarSystem(planetTextures,starTextures,x,y, i>systemCount/2, i);
			SpaceOfChaos.INSTANCE.idMap.put(i,starSystem);
			starSystems.add(starSystem);
			planetCount+=starSystem.planets.size();
		}
		long spaceStationCount=starSystems.stream().filter(starSystem -> starSystem.spaceStation!=null).count();
		for (int i = (int) spaceStationCount; i < 3; i++) {
			while(true)
			{
				StarSystem randomSystem=starSystems.get(random.nextInt(starSystems.size()));
				if(randomSystem.spaceStation==null)
				{
					randomSystem.spaceStation = new SpaceStation(random.nextFloat(-MathUtils.PI, MathUtils.PI), randomSystem.distanceFromStar+300);
					break;
				}
			}
		}
		Functions.log("Generated "+planetCount+ " planets in total");
		updateWorld=false;
	}

	@Override
	public void render() {
		super.render();
		if(updateWorld)
		{
			float deltaTime = Gdx.graphics.getDeltaTime();
			starSystems.forEach(StarSystem::update);
			if(aiAttackTimer<=0)
			{
				List<StarSystem> validOccupiedSystems= starSystems.stream().filter(starSystem -> {
					int aiShipCount=0;
					for (Ship ship : starSystem.ships) {
						if(ship instanceof NPCPilot npcPilot)
						{
							if(npcPilot.pilotAI==PilotAI.AI)
							{
								aiShipCount++;
							}
							else {
								//if there is a non-AI ship,skip
								return false;
							}
						}
					}
					if(aiShipCount>2) {
						ArrayList<StarSystem> otherSystems = new ArrayList<>(starSystems.size());
						otherSystems.addAll(starSystems);
						otherSystems.remove(starSystem);
						for (StarSystem otherSystem : otherSystems) {
							if (!otherSystem.occupied && Vector2.dst(otherSystem.positionX, otherSystem.positionY, starSystem.positionX, starSystem.positionY) <= 400) {
								return true;
							}
						}
					}
					return false;
				} ).toList();
				if(!validOccupiedSystems.isEmpty())
				{
					StarSystem randomSystem=validOccupiedSystems.get(random.nextInt(validOccupiedSystems.size()));
					List<StarSystem> closestFreeSystems= new ArrayList<>(starSystems.stream().filter(starSystem -> !starSystem.occupied && Vector2.dst(starSystem.positionX, starSystem.positionY, randomSystem.positionX, randomSystem.positionY) <= 400).toList());
					closestFreeSystems.remove(randomSystem);
					StarSystem attackTarget=closestFreeSystems.get(random.nextInt(closestFreeSystems.size()));
					randomSystem.ships.forEach(ship -> {
						if(ship instanceof NPCPilot npcPilot && npcPilot.pilotAI==PilotAI.AI)
						{
							npcPilot.navigatingTo=attackTarget;
						}
					});
					Functions.log("Incoming attack from "+randomSystem.getStarName()+" on "+attackTarget.getStarName());
					if(systemScreen!=null)
					{
						systemScreen.addMessage("AI is attacking system "+randomSystem.getStarName());
					}
					aiAttackTimer=random.nextInt(7*60,12*60);
				}
			}
			else {
				aiAttackTimer-= deltaTime;
			}
			if(humanAttackTimer<=0)
			{
				List<StarSystem> occupiedSystems=starSystems.stream().filter(starSystem -> starSystem.occupied).toList();
				List<StarSystem> systemsWithGuards=starSystems.stream().filter(starSystem -> {
					if(!starSystem.occupied)
					{
						long guardCount=starSystem.ships.stream().filter(ship -> ship instanceof NPCPilot npcPilot && npcPilot.pilotAI==PilotAI.GUARD).count();
						if(guardCount>=3)
						{
							for (StarSystem occupiedSystem : occupiedSystems) {
								if(Vector2.dst(occupiedSystem.positionX,occupiedSystem.positionY,starSystem.positionX,starSystem.positionY)<=400)
								{
									return true;
								}
							}
						}
					}
					return false;
				}).toList();
				if(!systemsWithGuards.isEmpty()) {
					StarSystem randomHumanSystem = systemsWithGuards.get(random.nextInt(systemsWithGuards.size()));
					List<StarSystem> closeOccupiedSystems = occupiedSystems.stream().filter(starSystem -> Vector2.dst(starSystem.positionX, starSystem.positionY, randomHumanSystem.positionX, randomHumanSystem.positionY) <= 400).toList();
					StarSystem closeOccupiedSystem = closeOccupiedSystems.get(random.nextInt(closeOccupiedSystems.size()));
					randomHumanSystem.ships.forEach(ship -> {
						if (ship instanceof NPCPilot npcPilot && npcPilot.pilotAI == PilotAI.GUARD) {
							npcPilot.navigatingTo = closeOccupiedSystem;
						}
					});
					Functions.log("Human attack from " + randomHumanSystem.getStarName() + " on " + closeOccupiedSystem.getStarName());
					if(systemScreen!=null)
						systemScreen.addMessage("Human attack on "+closeOccupiedSystem.getStarName()+" is happening");
					humanAttackTimer = random.nextInt(7 * 60, 12 * 60);
				}
			}
			else
				humanAttackTimer-=deltaTime;
			if(systemCheckTime<=0) {
				starSystems.forEach(starSystem -> {
					if (starSystem.occupied) {
						if (starSystem.ships.stream().noneMatch(ship -> ship instanceof NPCPilot npcPilot && npcPilot.pilotAI == PilotAI.AI)) {
							if (starSystem.ships.stream().anyMatch(ship -> {
								if (ship instanceof NPCPilot npcPilot) {
									return npcPilot.pilotAI != PilotAI.AI;
								} else
									return ship instanceof PlayerShip;
							})) {
								starSystem.occupied = false;
								//TODO do something
								starSystem.setPriceFactors(random);
								starSystem.planets.forEach(planet -> {
									if(random.nextBoolean())
										planet.kind= Planet.Kind.UNINHABITED;
									else
									{
										planet.kind= Planet.Kind.INHABITED;
										//TODO remove in future
										planet.initializeEquipment();
										planet.initializeMarket();
									}
								});
								Functions.log("System " + starSystem.getStarName() + " was liberated");
								if (systemScreen != null)
									systemScreen.addMessage("Star system " + starSystem.getStarName() + " has been liberated");
							}
						}
					} else {
						if (starSystem.ships.stream().anyMatch(ship -> ship instanceof NPCPilot npcPilot && npcPilot.pilotAI == PilotAI.AI)) {
							if (starSystem.ships.stream().noneMatch(ship -> {
								if (ship instanceof NPCPilot npcPilot) {
									return npcPilot.pilotAI != PilotAI.AI;
								}
								return ship instanceof PlayerShip;
							})) {
								starSystem.occupied = true;
								Functions.log("System " + starSystem.getStarName() + " was captured");
								if (systemScreen != null)
									systemScreen.addMessage("Star system " + starSystem.getStarName() + " has been captured by AI");
							}
						}
					}
				});
				systemCheckTime=0.5f;
				if(starSystems.stream().noneMatch(starSystem -> starSystem.occupied))
				{
					Functions.log("All systems have been liberated");
					if(systemScreen!=null)
					{
						Dialog dialog=new Dialog("All systems have been liberated. You have won the game!",skin);
						TextButton ok=new TextButton("Go to main menu",skin);
						ok.addListener(new ChangeListener() {
							@Override
							public void changed(ChangeEvent event, Actor actor) {
								setScreen(new StartScreen(SpaceOfChaos.this));
							}
						});
						dialog.button(ok);
						dialog.show(systemScreen.stage);
					}
				}
			}
			else {
				systemCheckTime-=deltaTime;
			}
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.F3))
		{
			debugDraw=!debugDraw;
		}

	}

	private void loadTexture(String name)
	{
		assetManager.load("textures/"+name+".png", Texture.class);
	}

	private Texture getTexture(String name)
	{
		Texture texture = assetManager.get("textures/" + name + ".png");
		textures.add(texture);
		textureHashMap.put(textureID++,texture);
		return texture;
	}

	public static int getWindowHeight()
	{
		return Gdx.graphics.getHeight();
	}

	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
		if(screen instanceof SystemScreen systemScreen)
		{
			this.systemScreen=systemScreen;
		}
	}

	public void saveGame(Stage stage)
	{

        assert dataDir != null;
        Path dataPath=Path.of(dataDir,"Space of Chaos","Saves");
		if(!Files.exists(dataPath)) {
			try {
				Files.createDirectories(dataPath);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		Dialog dialog=new Dialog("Save game?",skin);
		TextField input=new TextField(null,skin);
		dialog.add(input);
		dialog.button(new TextButton("Cancel",skin));
		TextButton textButton=new TextButton("Save",skin);
		textButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Yaml yaml=new Yaml();
				String dumped=yaml.dump(getData());
				String messageText = input.getText();
				if(messageText!=null &&!messageText.isEmpty()) {
					Path dataFile = Path.of(dataPath.toString(), messageText+".yaml");
					if (!Files.exists(dataFile)) {
						try {
							Files.createFile(dataFile);
							try {
								Files.writeString(dataFile, dumped);
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
					else {
						Dialog overwrite=new Dialog("Overwrite existing save?",skin);
						TextButton yes=new TextButton("Yes",skin);
						yes.addListener(new ChangeListener() {
							@Override
							public void changed(ChangeEvent event, Actor actor) {
								try {
									Files.writeString(dataFile, dumped);
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
							}
						});
						TextButton no=new TextButton("No",skin);
						overwrite.button(yes);
						overwrite.button(no);
						overwrite.show(stage);
					}
				}
			}
		});
		dialog.button(textButton);
		dialog.show(stage);
	}

	public void autoSave()
	{
		try {
			Yaml yaml=new Yaml();
			String dumped=yaml.dump(getData());
			Path dataPath=Path.of(dataDir,"Space of Chaos","Saves");
			Path dataFile = Path.of(dataPath.toString(), "Auto-save.yaml");
			Files.writeString(dataFile, dumped);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void loadGame(Path save)
	{
		if(Files.exists(save))
		{
			try {
				Yaml yaml=new Yaml();
				String yamlString=Files.readString(save);
				LinkedHashMap<String,Object> dataMap=yaml.load(yamlString);
				load(dataMap);
				updateWorld=true;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Override
	public Map<String, Object> getData() {
		HashMap<String,Object> data=new HashMap<>(1000);
		data.put("player ship",playerShip.getData());
		data.put("ai attack timer",aiAttackTimer);
		data.put("human attack timer",humanAttackTimer);
		for (int i = 0; i < starSystems.size(); i++) {
			StarSystem starSystem=starSystems.get(i);
			data.put("star system "+i,starSystem.getData());
		}
		data.put("system count",starSystems.size());
		data.put("planet help shown",planetScreenHelpShown);
		data.put("system screen help shown",systemScreenHelpShown);
		data.put("market help shown",marketHelpShown);
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void load(Map<String, Object> data) {
		LinkedHashMap<String,Object> playerData= (LinkedHashMap<String, Object>) data.get("player ship");
		playerShip=new PlayerShip();
		playerShip.load(playerData);
		aiAttackTimer= (float)(double) data.get("ai attack timer");
		humanAttackTimer= (float)(double) data.get("human attack timer");
		starSystems.clear();
		int systemCount= (int) data.get("system count");
		for (int i = 0; i < systemCount; i++) {
			StarSystem starSystem=new StarSystem();
			starSystem.load((Map<String, Object>) data.get("star system "+i));
			starSystems.add(starSystem);
		}
		if(data.containsKey("planet help shown"))
			planetScreenHelpShown= (boolean) data.get("planet help shown");
		if(data.containsKey("system screen help shown"))
			systemScreenHelpShown= (boolean) data.get("system screen help shown");
		if(data.containsKey("market help shown"))
			marketHelpShown= (boolean) data.get("market help shown");
		ArrayList<StarSystem> tmp=new ArrayList<>(starSystems);
		for (StarSystem system : starSystems) {
			for (Planet planet : system.planets) {
				boolean found=false;
				for (StarSystem starSystem : tmp) {
					if(planet.starSystemId==starSystem.id)
					{
						planet.starSystem=starSystem;
						found=true;
						break;
					}
				}
				if(!found)
					throw new RuntimeException("Star system not found");
			}
		}

		for (StarSystem starSystem : starSystems) {
			if(starSystem.id==playerShip.currentSystemId)
			{
				starSystem.ships.removeIf(ship -> ship instanceof PlayerShip);
				playerShip.setCurrentSystem(starSystem);
				starSystem.ships.add(playerShip);
				setScreen(new SystemScreen(starSystem));
				break;
			}
		}

		for (StarSystem starSystem : starSystems) {
			for (Ship ship : starSystem.ships) {
				if(ship instanceof NPCPilot npcPilot) {
					twice:
					for (StarSystem system : tmp) {
						for (Planet planet : system.planets) {
							if (planet.id == npcPilot.homePlanetId)
							{
								npcPilot.homePlanet=planet;
								break twice;
							}
						}
					}
				}
			}
		}

		for (StarSystem starSystem : starSystems) {
			for (Ship ship : starSystem.ships) {
				if (ship instanceof NPCPilot npcPilot) {
					for (StarSystem system : tmp) {
						if (system.id == npcPilot.currentSystemId) {
							npcPilot.setCurrentSystem(system);
							break;
						}
					}
				}
			}
		}

		ArrayList<Ship> tmpShips=new ArrayList<>(200);
		for (StarSystem starSystem : starSystems) {
			tmpShips.addAll(starSystem.ships);
		}
		for (StarSystem starSystem : starSystems) {
			for (Ship ship : starSystem.ships) {
				if(ship instanceof NPCPilot npcPilot && npcPilot.leaderId!=-1)
				{
					for (Ship tmpShip : tmpShips) {
						if(tmpShip.getId()==npcPilot.leaderId)
						{
							ship.setLeader(tmpShip);
							if(tmpShip instanceof PlayerShip ps)
							{
								ps.hiredShips.add(ship);
							}
							break;
						}
					}
				}
			}
		}
    }
}
